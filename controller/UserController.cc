#include "UserController.h"
#include <string>
#include "jwt-cpp/traits/open-source-parsers-jsoncpp/traits.h"
using traits = jwt::traits::open_source_parsers_jsoncpp;
using claim = jwt::basic_claim<traits>;
void UserController::login(
    const HttpRequestPtr& req,
    std::function<void(const HttpResponsePtr&)>&& callback) 
{
    try 
    {
        // 1. Получаем JSON из запроса
        auto json = req->getJsonObject();
        
        if (!json) 
        {
            throw std::runtime_error("Invalid JSON");
        }

        // 2. Извлекаем номер и пароль
        std::string phone_number = json->get("phone_number", "").asString();
        std::string password = json->get("password", "").asString();

        if (phone_number.empty() || password.empty()) {
            throw std::runtime_error("Phone number or password is missing");
        }

        // 3. Получаем подключение к БД
        auto dbClient = drogon::app().getDbClient();

        // 4. Создаём сервис
        UserService userService(dbClient);

        // 5. Вызываем метод логина
        std::string token = userService.login(phone_number, password);
        if (token.empty()) 
        {
            throw std::runtime_error("Invalid phone number or password");
        }

        // 6. Формируем JSON-ответ
        Json::Value ret;
        ret["token"]  = token;
        ret["status"] = "success";

        // 7. Отправляем ответ
        auto resp = HttpResponse::newHttpJsonResponse(ret);
        resp->setStatusCode(k200OK);
        callback(resp);
    }
    catch (const std::exception& e) 
    {
        Json::Value error;
        error["error"]  = e.what();
        error["status"] = "error";

        auto resp = HttpResponse::newHttpJsonResponse(error);
        resp->setStatusCode(k400BadRequest);
        callback(resp);
    }
}
void UserController::registerUser(const HttpRequestPtr& req,
              std::function<void(const HttpResponsePtr&)>&& callback)
    {
        try 
        {
        // 1. Получаем JSON из запроса
        auto json = req->getJsonObject();
        if (!json) 
        {
            throw std::runtime_error("Invalid JSON");
        }

        // 2. Извлекаем даные
        std::string phone_number = json->get("phone_number", "").asString();
        std::string password = json->get("password", "").asString();
        std::string name = json->get("name", "").asString();
        std::string last_name = json->get("last_name", "").asString();
        std::string surname = json->get("surname", "").asString();
        std::string document = json->get("document", "").asString();

        if (phone_number.empty() || password.empty() || name.empty() || last_name.empty() || surname.empty()
            || document.empty()) 
        {
            throw std::runtime_error("phone_number or password or name or last_name or surname or documents or role_type must be not NULL");
        }

        // 3. Получаем подключение к БД
        auto dbClient = drogon::app().getDbClient();

        // 4. Создаём сервис
        UserService userService(dbClient);

        // 5. Вызываем метод регистрации
        auto users = userService.registerUser(
            phone_number,
            password,
            name,
            last_name,
            surname,
            document
        );

        
        // Формируем JSON-ответ
        Json::Value jsonUser;
        jsonUser["id"] = users.getId();
        jsonUser["phone_number"] = users.getPhoneNumber();
        jsonUser["name"] = users.getName();
        jsonUser["last_name"] = users.getLastName();
        jsonUser["surname"] = users.getSurname();
        jsonUser["document"] = users.getDocument();

        //Создаем и настраиваем ответ
        auto resp = HttpResponse::newHttpJsonResponse(jsonUser);
        callback(resp);
    }
    catch (const std::exception& e) 
    {
        Json::Value error;
        error["error"]  = e.what();
        error["status"] = "error";

        auto resp = HttpResponse::newHttpJsonResponse(error);
        resp->setStatusCode(k400BadRequest);
        callback(resp);
    }
    }

void UserController::getUsers(const HttpRequestPtr& req,
              std::function<void(const HttpResponsePtr&)>&& callback)
{
    try 
    {
        auto token = Headerhelper::getTokenFromHeaders(req);
        auto decoded = jwt::decode<traits>(token);
        
        if (!Headerhelper::verifyToken(decoded))
        {
            throw std::runtime_error("Token olds");
        }
        if (!Headerhelper::checkRoles(decoded, "user_read"))
        {
            throw std::runtime_error("Not rights Role - User_read");
        }
        // 1. Получаем подключение к БД
        auto dbClient = drogon::app().getDbClient();
        // 2. Создаём сервис
        UserService userService(dbClient);
        auto users = userService.getUsers();

        // 3. Формируем JSON-ответ
        Json::Value jsonUsers;
        for (const auto& user : users) 
        {
            Json::Value jsonUser;
            jsonUser["id"] = user.getId();
            jsonUser["phone_number"] = user.getPhoneNumber();
            jsonUser["name"] = user.getName();
            jsonUser["last_name"] = user.getLastName();
            jsonUser["surname"] = user.getSurname();
            jsonUser["ducuments"] = user.getDocument();
            jsonUsers.append(jsonUser);
        }

        // 4. Создаем и настраиваем ответ
        auto resp = HttpResponse::newHttpJsonResponse(jsonUsers);
        callback(resp);
    } 
    catch (const std::exception& e) 
    {
        // 5. Обработка ошибок
        Json::Value error;
        error["error"] = e.what();
        auto resp = HttpResponse::newHttpJsonResponse(error);
        resp->setStatusCode(k500InternalServerError);
        callback(resp);
    }

}
void UserController::getUser(const HttpRequestPtr& req,
              std::function<void(const HttpResponsePtr&)>&& callback, int user_id)
{
    try 
    {
         // 6. Получение данных пользователя
        auto dbClient = drogon::app().getDbClient();
        UserService userService(dbClient);
        UserData user;
        std::string token = Headerhelper::getTokenFromHeaders(req);
        // 3. Декодирование JWT с улучшенной обработкой ошибок
        auto decoded = jwt::decode<traits>(token);
        // Проверка id доступа
        auto idClaim = decoded.get_payload_claim("Id");
        auto tokenUserId = std::stoi(idClaim.as_string());
        
        if (!Headerhelper::verifyToken(decoded))
        {
            throw std::runtime_error("Token olds");
        }
        if (!Headerhelper::checkRoles(decoded, "user_read") || user_id != tokenUserId)
        {
            throw std::runtime_error("Not rights Role - User_read and Not needs ID we may delete only your account");
        }
        
        user = userService.getUser(user_id);

        // 7. Формирование безопасного ответа (без пароля)
        Json::Value jsonUser;
        jsonUser["id"] = user.getId();
        jsonUser["phone_number"] = user.getPhoneNumber();
        jsonUser["name"] = user.getName();
        jsonUser["last_name"] = user.getLastName();
        jsonUser["surname"] = user.getSurname();
        jsonUser["document"] = user.getDocument();
        Json::Value rolesArray(Json::arrayValue); // Создаем JSON-массив
        for (const auto& role : user.getRoles())
        {
            rolesArray.append(role); // Добавляем каждую роль в массив
        }
        jsonUser["roles"] = rolesArray; // Добавляем массив в основной объект

        auto resp = HttpResponse::newHttpJsonResponse(jsonUser);
        callback(resp);
    } 
    catch (const std::runtime_error& e) 
    {
        Json::Value error;
        error["error"] = e.what();
        error["status"] = "error";
        
        auto resp = HttpResponse::newHttpJsonResponse(error);
        resp->setStatusCode(HttpStatusCode::k401Unauthorized);
        callback(resp);
    }
}

void UserController::deleteUser(const HttpRequestPtr& req,
              std::function<void(const HttpResponsePtr&)>&& callback, int userId)
{
    bool result = false;
    try 
    {
        std::string token = Headerhelper::getTokenFromHeaders(req);
        // 3. Декодирование JWT с улучшенной обработкой ошибок
        auto decoded = jwt::decode<traits>(token);
        //получение ID
        auto idClaim = decoded.get_payload_claim("Id");
        auto tokenUserId = std::stoi(idClaim.as_string());
        
        if (!Headerhelper::verifyToken(decoded))
        {
            throw std::runtime_error("Token olds");
        }
        if (!Headerhelper::checkRoles(decoded, "user_write") || userId != tokenUserId)
        {
            throw std::runtime_error("Not rights Role - News_read");
        }

        // 6. Получение данных пользователя
        auto dbClient = drogon::app().getDbClient();
        UserService userService(dbClient);
        
        result = userService.deleteUser(userId);
    } 
    catch (const std::runtime_error& e) 
    {
        Json::Value error;
        error["error"] = e.what();
        error["status"] = "error";
        
        auto resp = HttpResponse::newHttpJsonResponse(error);
        resp->setStatusCode(HttpStatusCode::k401Unauthorized);
        callback(resp);
    } 

    if (!result)
    {
        // 3. Возвращаем 404
        auto resp = HttpResponse::newHttpResponse();
        resp->setStatusCode(k404NotFound);
        callback(resp);
    }

    // 3. Возвращаем 204 No Content
    auto resp = HttpResponse::newHttpResponse();
    resp->setStatusCode(k204NoContent);
    callback(resp);
}

void UserController::addRole(const HttpRequestPtr& req,
              std::function<void(const HttpResponsePtr&)>&& callback, int user_id, int role_id)
{
    // 1. Получаем подключение к БД
    auto dbClient = drogon::app().getDbClient();
    // 2. Создаём сервис
    UserService userService(dbClient);
    std::string token = Headerhelper::getTokenFromHeaders(req);
    // 3. Декодирование JWT с улучшенной обработкой ошибок
    auto decoded = jwt::decode<traits>(token);
    
    if (!Headerhelper::verifyToken(decoded))
    {
        throw std::runtime_error("Token olds");
    }
    if (!Headerhelper::checkRoles(decoded, "role_write"))
    {
        throw std::runtime_error("Not rights Role - Role_write");
    }

    userService.addRole(user_id, role_id); 
    // 3. Возвращаем 201
    auto resp = HttpResponse::newHttpResponse();
    resp->setStatusCode(k201Created);
    callback(resp);
}

void UserController::deleteRole(const HttpRequestPtr& req,
              std::function<void(const HttpResponsePtr&)>&& callback, int user_id, int role_id)
{
    // 1. Получаем подключение к БД
    auto dbClient = drogon::app().getDbClient();
    // 2. Создаём сервис
    UserService userService(dbClient);
    std::string token = Headerhelper::getTokenFromHeaders(req);
    // 3. Декодирование JWT с улучшенной обработкой ошибок
    auto decoded = jwt::decode<traits>(token);
    
    if (!Headerhelper::verifyToken(decoded))
    {
        throw std::runtime_error("Token olds");
    }
    if (!Headerhelper::checkRoles(decoded, "role_write"))
    {
        throw std::runtime_error("Not rights Role - Role_write");
    }
    
    bool result = userService.deleteRole(user_id, role_id); 
    if (!result)
    {
        // 3. Возвращаем 404
        auto resp = HttpResponse::newHttpResponse();
        resp->setStatusCode(k404NotFound);
        callback(resp);
    }
    // 3. Возвращаем 204 No Content
    auto resp = HttpResponse::newHttpResponse();
    resp->setStatusCode(k204NoContent);
    callback(resp);
}