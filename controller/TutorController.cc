#include "TutorController.h"
void TutorController::postTutor(const HttpRequestPtr& req,
                   std::function<void(const HttpResponsePtr&)>&& callback)
{
    std::string token = Headerhelper::getTokenFromHeaders(req);
    auto decoded = jwt::decode<traits>(token);
    if (!Headerhelper::verifyToken(decoded)) 
    {
        auto resp = HttpResponse::newHttpResponse();
        resp->setStatusCode(k401Unauthorized);
        callback(resp);
        return;
    }
        
    if (!Headerhelper::checkRoles(decoded,"tutor_write"))
    {
        throw std::runtime_error("Access denied: insufficient privileges");
    }
    
    // Получаем JSON данные
    auto json = req->getJsonObject();
    if (!json) 
    {
        throw std::runtime_error("Invalid JSON");
    }

    // Извлекаем данные из JSON
    std::string header = json->get("header", "").asString();
    std::string body = json->get("body", "").asString();
    std::string date = json->get("date", "").asString();
    // Получаем массив изображений
        std::list<std::string> image_paths;
        if (json->isMember("tutor_path") && (*json)["tutor_path"].isArray()) 
        {
            const Json::Value& imagesArray = (*json)["tutor_path"];
            for (const auto& image : imagesArray) 
            {
                std::string path = image.asString();
                if (!path.empty()) 
                {
                    image_paths.push_back(path);
                }
            }
        }

    // 3. Получаем подключение к БД
    auto dbClient = drogon::app().getDbClient();
    TutorService tutor(dbClient);
    
    auto tutor_data = tutor.createTutor(
        header,
        body,
        date,
        image_paths
    );

    // 3. Формируем JSON-ответ
    Json::Value jsonTutor;
    jsonTutor["id"] = tutor_data.getId();
    jsonTutor["header"] = tutor_data.getHeader();
    jsonTutor["body"] = tutor_data.getBody();
    jsonTutor["date"] = tutor_data.getDate();
    Json::Value jsonImages(Json::arrayValue);
    for (const auto& image_path : tutor_data.getImagePaths()) 
    {
        jsonImages.append(image_path);
    }
    jsonTutor["tutor_path"] = jsonImages;

    // 4. Создаем и настраиваем ответ
    auto resp = HttpResponse::newHttpJsonResponse(jsonTutor);
    callback(resp);
}

    void TutorController::getTutor(const HttpRequestPtr& req,
                 std::function<void(const HttpResponsePtr&)>&& callback)
        {   
            std::string token = Headerhelper::getTokenFromHeaders(req);
            auto decoded = jwt::decode<traits>(token);
            if (!Headerhelper::verifyToken(decoded)) 
            {
                auto resp = HttpResponse::newHttpResponse();
                resp->setStatusCode(k401Unauthorized);
                callback(resp);
                return;
            }
            // 3. Получаем подключение к БД
            auto dbClient = drogon::app().getDbClient();
            TutorService tutor(dbClient);
            auto tutor_all = tutor.getTutor();
            // 3. Формируем JSON-ответ
            Json::Value jsonTutors;
            for (const auto& tutors : tutor_all)
            {
            Json::Value jsonTutor;
            jsonTutor["id"] = tutors.getId();
            jsonTutor["header"] = tutors.getHeader();
            jsonTutor["body"] = tutors.getBody();
            jsonTutor["date"] = tutors.getDate();
            // Добавляем массив изображений
            Json::Value jsonImages(Json::arrayValue);
            for (const auto& image_path : tutors.getImagePaths()) 
            {
                jsonImages.append(image_path);
            }
            jsonTutor["tutor_path"] = jsonImages; // исправлено имя поля
            jsonTutors.append(jsonTutor);   
            }
            // 4. Создаем и настраиваем ответ
            auto resp = HttpResponse::newHttpJsonResponse(jsonTutors);
            callback(resp);
        }

    
    void TutorController::deleteTutor(const HttpRequestPtr& req,
                 std::function<void(const HttpResponsePtr&)>&& callback, int id_tutor)
    {
        std::string token = Headerhelper::getTokenFromHeaders(req);
        auto decoded = jwt::decode<traits>(token);
        
        if (Headerhelper::verifyToken(decoded))
        {
            if (!Headerhelper::checkRoles(decoded, "tutor_write"))
            {
                throw std::runtime_error("Not rights Role - Tutor_write");
            }

            // 3. Получаем подключение к БД
            auto dbClient = drogon::app().getDbClient();
            TutorService tutor(dbClient);
            
            bool result = tutor.deleteTutor(id_tutor);
            
            if (!result)
            {
                auto resp = HttpResponse::newHttpResponse();
                // 3. Возвращаем 204 No Content
                resp->setStatusCode(k404NotFound);
                callback(resp);
            }
            auto resp = HttpResponse::newHttpResponse();
            // 3. Возвращаем 204 No Content
            resp->setStatusCode(k204NoContent);
            callback(resp);
        }

    }