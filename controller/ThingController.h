#pragma once
#include <drogon/HttpController.h>
#include <drogon/orm/DbClient.h>
#include <drogon/drogon.h>
#include <json/json.h>
#include "../model/Thing.h"
#include "../Util/Util.h"
#include "../service/S3Service.h"
#include "../service/ThingService.h"
#include <string>
#include <filesystem>
#include <jwt-cpp/jwt.h>
#include "jwt-cpp/traits/open-source-parsers-jsoncpp/traits.h"
using traits = jwt::traits::open_source_parsers_jsoncpp;
using claim = jwt::basic_claim<traits>;

using namespace drogon;

class ThingController : public HttpController<ThingController>
{
public:
    METHOD_LIST_BEGIN
        ADD_METHOD_TO(ThingController::postThing, "/thing", Post);
        ADD_METHOD_TO(ThingController::getThings, "/thing", Get);
        ADD_METHOD_TO(ThingController::deleteThing, "/thing/{1}", Delete);

    METHOD_LIST_END

    void postThing(const HttpRequestPtr& req,
                            std::function<void(const HttpResponsePtr&)>&& callback);

    void getThings(const HttpRequestPtr& req,
                            std::function<void(const HttpResponsePtr&)>&& callback);

    void deleteThing(const HttpRequestPtr& req,
                            std::function<void(const HttpResponsePtr&)>&& callback, 
                            int id_thing);
};