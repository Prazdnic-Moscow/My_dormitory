#pragma once
#include <HttpController.h>
#include <DbClient.h>
#include <drogon.h>
#include <json/json.h>
#include "Thing.h"
#include "Util.h"
#include "S3Service.h"
#include "ThingService.h"
#include <string>
#include <filesystem>
#include <jwt-cpp/jwt.h>
#include "traits.h"
using traits = jwt::traits::open_source_parsers_jsoncpp;
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