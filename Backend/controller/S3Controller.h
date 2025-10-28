#pragma once
#include <HttpController.h>
#include <DbClient.h>
#include <drogon.h>
#include <json/json.h>
#include "Util.h"
#include "S3Service.h"
#include <string>
#include <filesystem>
#include <jwt-cpp/jwt.h>
#include <uuid/uuid.h>
#include "traits.h"
using traits = jwt::traits::open_source_parsers_jsoncpp;
using namespace drogon;

class S3Controller : public HttpController<S3Controller>
{
    public:
        METHOD_LIST_BEGIN
            ADD_METHOD_TO(S3Controller::postFile, "/file/{1}", Post);
            ADD_METHOD_TO(S3Controller::getFile, "/file/{1}/{2}", Get);
            ADD_METHOD_TO(S3Controller::deleteFile, "/file/{1}/{2}", Delete);

        METHOD_LIST_END

        void postFile(const HttpRequestPtr& req,
                    std::function<void(const HttpResponsePtr&)>&& callback,
                    std::string folder);

        void getFile(const HttpRequestPtr& req,
                                std::function<void(const HttpResponsePtr&)>&& callback,
                                std::string folder, std::string file_path);

        void deleteFile(const HttpRequestPtr& req,
                                std::function<void(const HttpResponsePtr&)>&& callback,
                                std::string folder, std::string file_path);
};