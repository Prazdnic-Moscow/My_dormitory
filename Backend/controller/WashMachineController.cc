#include "WashMachineController.h"
void WashMachineController::addWashMachine(const HttpRequestPtr& req,
                                           std::function<void(const HttpResponsePtr&)>&& callback)
{
         std::string token = Headerhelper::getTokenFromHeaders(req);
        auto decode = jwt::decode<traits>(token);
        if (!Headerhelper::verifyToken(decode))
        {
            Headerhelper::responseCheckToken(callback);
            return;
        }
        
        if (!Headerhelper::checkRoles(decode, "wash_machine_write"))
        {
            Headerhelper::responseCheckRoles(callback);
            return;
        }
        // Получаем JSON данные
        auto json = req->getJsonObject();
        if (!json) 
        {
            throw std::runtime_error("Invalid JSON");
        }
        
        std::string name = json->get("name", "").asString();
        // 3. Получаем подключение к БД
        auto dbClient = drogon::app().getDbClient();
        WashMachineService washmachine(dbClient);
        washmachine.addWashMachine(name);
        
        auto resp = HttpResponse::newHttpResponse();
        resp->setStatusCode(k201Created);
        callback(resp);
}

void WashMachineController::getWashMachines(const HttpRequestPtr& req,
                                            std::function<void(const HttpResponsePtr&)>&& callback)
{
    std::string token = Headerhelper::getTokenFromHeaders(req);
    auto decode = jwt::decode<traits>(token);
    if (!Headerhelper::verifyToken(decode))
    {
        Headerhelper::responseCheckToken(callback);
        return;
    }
    
    if (!Headerhelper::checkRoles(decode, "wash_machine_read"))
    {
        Headerhelper::responseCheckRoles(callback);
        return;
    }
    // 3. Получаем подключение к БД
    auto dbClient = drogon::app().getDbClient();
    WashMachineService machine (dbClient);
    auto machines = machine.getWashMachines();
    Json::Value respJson;
    for (auto &m : machines)
    {
        Json::Value machineJson;
        machineJson["id"] = m.getId();
        machineJson["name"] = m.getName();
        respJson.append(machineJson);
    }
    callback(HttpResponse::newHttpJsonResponse(respJson));
}




void WashMachineController::deleteWashMachine(const HttpRequestPtr& req,
                                              std::function<void(const HttpResponsePtr&)>&& callback, 
                                              int id)
{
    std::string token = Headerhelper::getTokenFromHeaders(req);
    auto decode = jwt::decode<traits>(token);
    if (!Headerhelper::verifyToken(decode))
    {
        Headerhelper::responseCheckToken(callback);
        return;
    }
    
    if (!Headerhelper::checkRoles(decode, "wash_machine_write"))
    {
        Headerhelper::responseCheckRoles(callback);
        return;
    }
    // 3. Получаем подключение к БД
    auto dbClient = drogon::app().getDbClient();
    WashMachineService machine (dbClient);
    auto result = machine.deleteWashMachine(id);
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