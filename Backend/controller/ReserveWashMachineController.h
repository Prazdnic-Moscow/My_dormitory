#pragma once
#include <HttpController.h>
#include <DbClient.h>
#include <drogon.h>
#include <json/json.h>
#include "ReserveWashMachine.h"
#include "Util.h"
#include "ReserveWashMachineService.h"
#include <string>
#include <jwt-cpp/jwt.h>
#include "traits.h"
using traits = jwt::traits::open_source_parsers_jsoncpp;
using namespace drogon;

class ReserveWashMachineController : public HttpController<ReserveWashMachineController>
{
    public:
        METHOD_LIST_BEGIN
            ADD_METHOD_TO(ReserveWashMachineController::postReserveWashMachine, "/reserve", Post);
            ADD_METHOD_TO(ReserveWashMachineController::getReserveWashMachines, "/reserve", Get);
            ADD_METHOD_TO(ReserveWashMachineController::deleteReserveWashMachine,"/reserve/{}/{}", Delete);
        METHOD_LIST_END
        
        void postReserveWashMachine(const HttpRequestPtr &req,
                    std::function<void(const HttpResponsePtr&)>&& callback);

        void getReserveWashMachines(const HttpRequestPtr &req,
                    std::function<void(const HttpResponsePtr&)>&& callback);

        void deleteReserveWashMachine(const HttpRequestPtr &req,
                    std::function<void(const HttpResponsePtr&)>&& callback,
                    int id, int user_id);
};