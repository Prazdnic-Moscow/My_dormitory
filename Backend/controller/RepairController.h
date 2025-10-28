#pragma once
#include <HttpController.h>
#include <DbClient.h>
#include <drogon.h>
#include <json/json.h>
#include "Repair.h"
#include "Util.h"
#include "S3Service.h"
#include "RepairService.h"
#include <string>
#include <filesystem>
#include <jwt-cpp/jwt.h>
#include "traits.h"
using traits = jwt::traits::open_source_parsers_jsoncpp;

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