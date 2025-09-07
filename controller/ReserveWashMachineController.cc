#include "ReserveWashMachineController.h"

void ReserveWashMachineController::postReserveWashMachine(const HttpRequestPtr &req,
                                                          std::function<void(const HttpResponsePtr&)>&& callback)
{
    std::string token = Headerhelper::getTokenFromHeaders(req);
    auto decode = jwt::decode<traits>(token);
    if (!Headerhelper::verifyToken(decode))
    {
        Headerhelper::responseCheckToken(callback);
        return;
    }
    // Получаем JSON данные
    auto json = req->getJsonObject();
    if (!json) 
    {
        Headerhelper::responseCheckJson(callback);
    }
    
    int userId = decode.get_payload_claim("Id").as_integer();
    int machineId = json->get("machine_id", "").asInt();
    std::string date = json->get("date", "").asString();
    std::string date_start = json->get("date_start", "").asString();
    float duration = json->get("duration", "").asFloat();
    
    // 3. Получаем подключение к БД
    auto dbClient = drogon::app().getDbClient();
    ReserveWashMachineService washmachine(dbClient);
    auto machine = washmachine.createReserveWashMachine(userId,
                                                        machineId,
                                                        date,
                                                        date_start,
                                                        duration);
    
    Json::Value respJson;
    respJson["id"] = machine.getId();
    respJson["user_id"] = machine.getUserId();
    respJson["machine_id"] = machine.getMachineId();
    respJson["date"] = machine.getDate();
    respJson["start_time"] = machine.getStartTime();
    respJson["duration"] = machine.getDuration();

    auto resp = HttpResponse::newHttpJsonResponse(respJson);
    resp->setStatusCode(k201Created);
    callback(resp);
}

void ReserveWashMachineController::getReserveWashMachines(const HttpRequestPtr &req,
                                                          std::function<void(const HttpResponsePtr&)>&& callback)
{
    std::string token = Headerhelper::getTokenFromHeaders(req);
    auto decode = jwt::decode<traits>(token);
    if(!Headerhelper::verifyToken(decode))
    {
        Headerhelper::responseCheckToken(callback);
        return;
    }

    // 3. Получаем подключение к БД
    auto dbClient = drogon::app().getDbClient();
    ReserveWashMachineService washmachine(dbClient);
    auto machine = washmachine.getReserveWashMachines();
    
    Json::Value respJsons;
    for (auto& machines : machine)
    {
    Json::Value respJson;
    respJson["id"] = machines.getId();
    respJson["user_id"] = machines.getUserId();
    respJson["machine_id"] = machines.getMachineId();
    respJson["date"] = machines.getDate();
    respJson["start_time"] = machines.getStartTime();
    respJson["duration"] = machines.getDuration();
    respJsons.append(respJson);
    }
    auto resp = HttpResponse::newHttpJsonResponse(respJsons);
    resp->setStatusCode(k200OK);
    callback(resp);
}

void ReserveWashMachineController::deleteReserveWashMachine(const HttpRequestPtr &req,
                                                            std::function<void(const HttpResponsePtr&)>&& callback,
                                                            int reserve_id, int user_id)
{
    std::string token = Headerhelper::getTokenFromHeaders(req);
    auto decode = jwt::decode<traits>(token);
    if(!Headerhelper::verifyToken(decode))
    {
        Headerhelper::responseCheckToken(callback);
        return;
    }

    if (!Headerhelper::checkRoles(decode, "reserve_wash_machine_write") && user_id != decode.get_payload_claim("id").as_integer())
    {
        Headerhelper::responseCheckRoles(callback);
        return;
    }

    // 3. Получаем подключение к БД
    auto dbClient = drogon::app().getDbClient();
    ReserveWashMachineService washmachine(dbClient);
    auto result = washmachine.deleteReserveWashMachine(reserve_id);
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