#pragma once
#include <drogon/HttpController.h>
#include <drogon/orm/DbClient.h>
#include <drogon/drogon.h>
#include <json/json.h>
#include "../model/File.h"
#include "../Util/Util.h"
#include "../service/S3Service.h"
#include "../service/FileService.h"
#include <string>
#include <filesystem>
#include <jwt-cpp/jwt.h>
#include "jwt-cpp/traits/open-source-parsers-jsoncpp/traits.h"
using traits = jwt::traits::open_source_parsers_jsoncpp;
using claim = jwt::basic_claim<traits>;

using namespace drogon;

class FileController : public HttpController<FileController>
{
public:
    METHOD_LIST_BEGIN
        ADD_METHOD_TO(FileController::postFiles, "/file", Post);
        ADD_METHOD_TO(FileController::getFiles, "/file", Get);
        ADD_METHOD_TO(FileController::deleteFile, "/file/{1}", Delete);

    METHOD_LIST_END

    void postFiles(const HttpRequestPtr& req,
                            std::function<void(const HttpResponsePtr&)>&& callback);

    void getFiles(const HttpRequestPtr& req,
                            std::function<void(const HttpResponsePtr&)>&& callback);

    void deleteFile(const HttpRequestPtr& req,
                            std::function<void(const HttpResponsePtr&)>&& callback, 
                            int id_file);
};