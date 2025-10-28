#pragma once
#include <drogon.h>
#include <HttpController.h>
#include <DbClient.h>
#include <json/json.h>
#include "File.h"
#include "Util.h"
#include "S3Service.h"
#include "FileService.h"
#include <string>
#include <filesystem>
#include <jwt-cpp/jwt.h>
#include "traits.h"
using traits = jwt::traits::open_source_parsers_jsoncpp;

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