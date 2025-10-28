#pragma once
#include <HttpController.h>
#include <DbClient.h>
#include <drogon.h>
#include <json/json.h>
#include "WashMachine.h"
#include "Util.h"
#include "WashMachineService.h"
#include <string>
#include <jwt-cpp/jwt.h>
#include "traits.h"
using traits = jwt::traits::open_source_parsers_jsoncpp;

using namespace drogon;
class WashMachineController: public HttpController<WashMachineController>
{
    public:
        METHOD_LIST_BEGIN
            ADD_METHOD_TO(WashMachineController::addWashMachine, "/washmachine", Post);
            ADD_METHOD_TO(WashMachineController::getWashMachines,"/washmachine", Get);
            ADD_METHOD_TO(WashMachineController::deleteWashMachine,"/washmachine/{}", Delete);
        METHOD_LIST_END
        
        void addWashMachine(const HttpRequestPtr& req,
                            std::function<void(const HttpResponsePtr&)>&& callback);

        void getWashMachines(const HttpRequestPtr& req,
                             std::function<void(const HttpResponsePtr&)>&& callback);

        void deleteWashMachine(const HttpRequestPtr& req,
                               std::function<void(const HttpResponsePtr&)>&& callback,
                               int id);
};
