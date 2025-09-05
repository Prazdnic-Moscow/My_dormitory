#include "RepairController.h"

void RepairController::postRepair(const HttpRequestPtr& req,
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
        std::list<std::string> repair_paths;
        if (json->isMember("repair_paths") && (*json)["repair_paths"].isArray()) 
        {
            const Json::Value& filesArray = (*json)["repair_paths"];
            for (const auto& file : filesArray) 
            {
                std::string path = file.asString();
                if (!path.empty()) 
                {
                    repair_paths.push_back(path);
                }
            }
        }

    // 3. Получаем подключение к БД
    auto dbClient = drogon::app().getDbClient();
    RepairService repair(dbClient);
    
    auto repair_data = repair.createRepair(type, body, date, repair_paths);

    // 3. Формируем JSON-ответ
    Json::Value jsonRepair;
    jsonRepair["id"] = repair_data.getId();
    jsonRepair["type"] = repair_data.getType();
    jsonRepair["body"] = repair_data.getBody();
    jsonRepair["date"] = repair_data.getDate();
    // Добавляем массив изображений
        Json::Value jsonImages(Json::arrayValue);
        for (const auto& repair_path : repair_data.getRepairPaths()) 
        {
            jsonImages.append(repair_path);
        }
        jsonRepair["repair_path"] = jsonImages;

    // 4. Создаем и настраиваем ответ
    auto resp = HttpResponse::newHttpJsonResponse(jsonRepair);
    callback(resp);
}


void RepairController::getRepairs(const HttpRequestPtr& req,
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
        RepairService repair(dbClient);
        auto repair_all = repair.getRepairs();
        // 3. Формируем JSON-ответ
        Json::Value jsonRepairs;
        for (const auto& repairs : repair_all)
        {
            Json::Value jsonRepair;
            jsonRepair["id"] = repairs.getId();
            jsonRepair["body"] = repairs.getBody();
            jsonRepair["date"] = repairs.getDate();
            // Добавляем массив изображений
            Json::Value jsonImages(Json::arrayValue);
            for (const auto& repair_path : repairs.getRepairPaths()) 
            {
                jsonImages.append(repair_path);
            }

            jsonRepair["repair_path"] = jsonImages; // исправлено имя поля
            jsonRepairs.append(jsonRepair);
        }
        // 4. Создаем и настраиваем ответ
        auto resp = HttpResponse::newHttpJsonResponse(jsonRepairs);
        callback(resp);
    }

void RepairController::deleteRepair(const HttpRequestPtr& req,
                        std::function<void(const HttpResponsePtr&)>&& callback, 
                        int id_repair, int id_user)
    {
        std::string token = Headerhelper::getTokenFromHeaders(req);
        auto decoded = jwt::decode<traits>(token);
        
        if (Headerhelper::verifyToken(decoded))
        {
            //мб сделать чтобы свои записи можно было удалять
            if (!Headerhelper::checkRoles(decoded, "repair_write") && id_user != decoded.get_payload_claim("id").as_integer())
            {
                throw std::runtime_error("Not rights Role - Repair_write");
            }

            // 3. Получаем подключение к БД
            auto dbClient = drogon::app().getDbClient();
            RepairService repair(dbClient);
            
            bool result = repair.deleteRepair(id_repair);
            
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