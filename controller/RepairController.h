#pragma once
#include <drogon/HttpController.h>
#include <drogon/orm/DbClient.h>
#include <drogon/drogon.h>
#include <json/json.h>
#include "../model/Repair.h"
#include "../Util/Util.h"
#include "../service/S3Service.h"
#include "../service/RepairService.h"
#include <string>
#include <filesystem>
#include <jwt-cpp/jwt.h>
#include "jwt-cpp/traits/open-source-parsers-jsoncpp/traits.h"
using traits = jwt::traits::open_source_parsers_jsoncpp;
using claim = jwt::basic_claim<traits>;

using namespace drogon;

class RepairController : public HttpController<RepairController>
{
public:
    METHOD_LIST_BEGIN
        ADD_METHOD_TO(RepairController::postRepair, "/repair", Post);
        ADD_METHOD_TO(RepairController::getRepairs, "/repair", Get);
        ADD_METHOD_TO(RepairController::deleteRepair, "/repair/{}/{}", Delete);

    METHOD_LIST_END

    void postRepair(const HttpRequestPtr& req,
                            std::function<void(const HttpResponsePtr&)>&& callback);

    void getRepairs(const HttpRequestPtr& req,
                            std::function<void(const HttpResponsePtr&)>&& callback);

    void deleteRepair(const HttpRequestPtr& req,
                            std::function<void(const HttpResponsePtr&)>&& callback, 
                            int id_repair, int id_user);
};