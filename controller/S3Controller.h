#pragma once
#include <drogon/HttpController.h>
#include <drogon/orm/DbClient.h>
#include <drogon/drogon.h>
#include <json/json.h>
#include "../model/News.h"
#include "../Util/Util.h"
#include "../service/S3Service.h"
#include "../service/NewsService.h"
#include <string>
#include <filesystem>
#include <jwt-cpp/jwt.h>
#include <uuid/uuid.h>
#include "jwt-cpp/traits/open-source-parsers-jsoncpp/traits.h"
using traits = jwt::traits::open_source_parsers_jsoncpp;
using claim = jwt::basic_claim<traits>;

using namespace drogon;

class S3Controller : public HttpController<S3Controller>
{
public:
    METHOD_LIST_BEGIN
        ADD_METHOD_TO(S3Controller::upload, "/upload", Post);
    METHOD_LIST_END

    void upload(const HttpRequestPtr& req,
                 std::function<void(const HttpResponsePtr&)>&& callback);
};