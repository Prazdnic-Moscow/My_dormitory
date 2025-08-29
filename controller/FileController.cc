#include "FileController.h"

void FileController::postFiles(const HttpRequestPtr& req,
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
        
    if (!Headerhelper::checkRoles(decoded,"file_write"))
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
    std::string body = json->get("body", "").asString();
    std::string file_path = json->get("file_path", "").asString();

    // 3. Получаем подключение к БД
    auto dbClient = drogon::app().getDbClient();
    FileService file(dbClient);
    
    auto file_data = file.createFile(
        body,
        file_path
    );

    // 3. Формируем JSON-ответ
    Json::Value jsonUser;
    jsonUser["id"] = file_data.getId();
    jsonUser["body"] = file_data.getBody();
    jsonUser["file_path"] = file_data.getFilePath();

    // 4. Создаем и настраиваем ответ
    auto resp = HttpResponse::newHttpJsonResponse(jsonUser);
    callback(resp);
}


void FileController::getFiles(const HttpRequestPtr& req,
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
        FileService file(dbClient);
        auto file_all = file.getFiles();
        // 3. Формируем JSON-ответ
        Json::Value jsonTutors;
        for (const auto& files : file_all)
        {
        Json::Value jsonTutor;
        jsonTutor["id"] = files.getId();
        jsonTutor["body"] = files.getBody();
        jsonTutor["file_path"] = files.getFilePath();
        jsonTutors.append(jsonTutor);   
        }
        // 4. Создаем и настраиваем ответ
        auto resp = HttpResponse::newHttpJsonResponse(jsonTutors);
        callback(resp);
    }

void FileController::deleteFile(const HttpRequestPtr& req,
                        std::function<void(const HttpResponsePtr&)>&& callback, 
                        int id_file)
    {
        std::string token = Headerhelper::getTokenFromHeaders(req);
        auto decoded = jwt::decode<traits>(token);
        
        if (Headerhelper::verifyToken(decoded))
        {
            if (!Headerhelper::checkRoles(decoded, "file_write"))
            {
                throw std::runtime_error("Not rights Role - Tutor_write");
            }

            // 3. Получаем подключение к БД
            auto dbClient = drogon::app().getDbClient();
            FileService file(dbClient);
            
            bool result = file.deleteFile(id_file);
            
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