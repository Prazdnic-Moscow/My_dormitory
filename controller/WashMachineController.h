#pragma once
#include <drogon/HttpController.h>
#include <drogon/orm/DbClient.h>
#include <drogon/drogon.h>
#include <json/json.h>
#include "../model/WashMachine.h"
#include "../Util/Util.h"
#include "../service/WashMachineService.h"
#include <string>
#include <jwt-cpp/jwt.h>
#include "jwt-cpp/traits/open-source-parsers-jsoncpp/traits.h"
using traits = jwt::traits::open_source_parsers_jsoncpp;
using claim = jwt::basic_claim<traits>;

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
