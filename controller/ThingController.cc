#include "ThingController.h"

void ThingController::postThing(const HttpRequestPtr& req,
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
    // Получаем JSON данные
    auto json = req->getJsonObject();
    if (!json) 
    {
        throw std::runtime_error("Invalid JSON");
    }
    // Извлекаем данные из JSON
    std::string type = json->get("type", "").asString();
    std::string body = json->get("body", "").asString();
    std::string date = json->get("date", "").asString();
    // Получаем массив файлов
        std::list<std::string> thing_paths;
        if (json->isMember("thing_paths") && (*json)["thing_paths"].isArray()) 
        {
            const Json::Value& filesArray = (*json)["thing_paths"];
            for (const auto& file : filesArray) 
            {
                std::string path = file.asString();
                if (!path.empty()) 
                {
                    thing_paths.push_back(path);
                }
            }
        }

    // 3. Получаем подключение к БД
    auto dbClient = drogon::app().getDbClient();
    ThingService thing(dbClient);
    
    auto thing_data = thing.createThing(type, body, date, thing_paths);

    // 3. Формируем JSON-ответ
    Json::Value jsonThing;
    jsonThing["id"] = thing_data.getId();
    jsonThing["type"] = thing_data.getType();
    jsonThing["body"] = thing_data.getBody();
    jsonThing["date"] = thing_data.getDate();
    // Добавляем массив изображений
        Json::Value jsonImages(Json::arrayValue);
        for (const auto& image_path : thing_data.getFilePaths()) 
        {
            jsonImages.append(image_path);
        }
        jsonThing["thing_path"] = jsonImages;

    // 4. Создаем и настраиваем ответ
    auto resp = HttpResponse::newHttpJsonResponse(jsonThing);
    callback(resp);
}


void ThingController::getThings(const HttpRequestPtr& req,
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
        ThingService thing(dbClient);
        auto thing_all = thing.getThings();
        // 3. Формируем JSON-ответ
        Json::Value jsonThings;
        for (const auto& files : thing_all)
        {
            Json::Value jsonThing;
            jsonThing["id"] = files.getId();
            jsonThing["body"] = files.getBody();
            jsonThing["date"] = files.getDate();
            // Добавляем массив изображений
            Json::Value jsonImages(Json::arrayValue);
            for (const auto& image_path : files.getFilePaths()) 
            {
                jsonImages.append(image_path);
            }

            jsonThing["files_path"] = jsonImages; // исправлено имя поля
            jsonThings.append(jsonThing);
        }
        // 4. Создаем и настраиваем ответ
        auto resp = HttpResponse::newHttpJsonResponse(jsonThings);
        callback(resp);
    }

void ThingController::deleteThing(const HttpRequestPtr& req,
                        std::function<void(const HttpResponsePtr&)>&& callback, 
                        int id_thing)
    {
        std::string token = Headerhelper::getTokenFromHeaders(req);
        auto decoded = jwt::decode<traits>(token);
        
        if (Headerhelper::verifyToken(decoded))
        {
            if (!Headerhelper::checkRoles(decoded, "thing_write"))
            {
                throw std::runtime_error("Not rights Role - Thing_write");
            }

            // 3. Получаем подключение к БД
            auto dbClient = drogon::app().getDbClient();
            ThingService thing(dbClient);
            
            bool result = thing.deleteThing(id_thing);
            
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