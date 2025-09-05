#pragma once
#include <drogon/HttpController.h>
#include <drogon/orm/DbClient.h>
#include <drogon/drogon.h>
#include <json/json.h>
#include "../model/ReserveWashMachine.h"
#include "../Util/Util.h"
#include "../service/ReserveWashMachineService.h"
#include <string>
#include <jwt-cpp/jwt.h>
#include "jwt-cpp/traits/open-source-parsers-jsoncpp/traits.h"
using traits = jwt::traits::open_source_parsers_jsoncpp;
using claim = jwt::basic_claim<traits>;

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