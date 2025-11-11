#include "RepairController.h"

void RepairController::postRepair(const HttpRequestPtr& req,
                                  std::function<void(const HttpResponsePtr&)>&& callback)
{
    std::string token = Headerhelper::getTokenFromHeaders(req);
    auto decoded = jwt::decode<traits>(token);
    if (!Headerhelper::verifyToken(decoded)) 
    {
        Headerhelper::responseCheckToken(callback);
        return;
    }
    int user_id = decoded.get_payload_claim("Id").as_integer();
    // Получаем JSON данные
    auto json = req->getJsonObject();
    if (!json) 
    {
        Headerhelper::responseCheckJson(callback);
        return;
    }
    // Извлекаем данные из JSON
    std::string type = json->get("type", "").asString();
    std::string body = json->get("body", "").asString();
    int room = json->get("room", "").asInt();
    // Получаем массив файлов
    std::list<std::string> repair_paths;
    if (!json->isMember("repair_paths") || !(*json)["repair_paths"].isArray()) 
    {
        Headerhelper::responseCheckJson(callback);
        return;
    }
    const Json::Value& filesArray = (*json)["repair_paths"];
    for (const auto& file : filesArray) 
    {
        std::string path = file.asString();
        if (!path.empty()) 
        {
            repair_paths.push_back(path);
        }
    }
    if (type != "plumber" && type != "carpenter" && type != "electrician")
    {
        auto resp = HttpResponse::newHttpResponse();
        resp->setStatusCode(k400BadRequest);
        callback(resp);
    }

    // 3. Получаем подключение к БД
    auto dbClient = drogon::app().getDbClient();
    RepairService repair(dbClient);
    auto repair_data = repair.createRepair(type, 
                                           body, 
                                           room,
                                           repair_paths,
                                           user_id);

    // 3. Формируем JSON-ответ
    Json::Value jsonRepair;
    jsonRepair["id"] = repair_data.getId();
    jsonRepair["type"] = repair_data.getType();
    jsonRepair["body"] = repair_data.getBody();
    jsonRepair["room"] = repair_data.getRoom();
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
    resp->setStatusCode(k201Created);
    callback(resp);
}


void RepairController::getRepairs(const HttpRequestPtr& req,
                                  std::function<void(const HttpResponsePtr&)>&& callback)
{
    std::string token = Headerhelper::getTokenFromHeaders(req);
    auto decoded = jwt::decode<traits>(token);
    if (!Headerhelper::verifyToken(decoded)) 
    {
        Headerhelper::responseCheckToken(callback);
        return;
    }
    // 3. Получаем подключение к БД
    auto dbClient = drogon::app().getDbClient();
    RepairService repair(dbClient);
    auto repair_all = repair.getRepairs();
    // 3. Формируем JSON-ответ
    Json::Value jsonRepairs;
    for (auto repairs : repair_all)
    {
        Json::Value jsonRepair;
        jsonRepair["id"] = repairs.getId();
        jsonRepair["body"] = repairs.getBody();
        jsonRepair["room"] = repairs.getRoom();
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
    resp->setStatusCode(k200OK);
    callback(resp);
}

void RepairController::deleteRepair(const HttpRequestPtr& req,
                                    std::function<void(const HttpResponsePtr&)>&& callback, 
                                    int id_repair, int id_user)
{
    std::string token = Headerhelper::getTokenFromHeaders(req);
    auto decoded = jwt::decode<traits>(token);
    
    if (!Headerhelper::verifyToken(decoded))
    {
        Headerhelper::responseCheckToken(callback);
        return;
    }
    
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
        resp->setStatusCode(k404NotFound);
        callback(resp);
    }
    auto resp = HttpResponse::newHttpResponse();
    // 3. Возвращаем 204 No Content
    resp->setStatusCode(k204NoContent);
    callback(resp);
}