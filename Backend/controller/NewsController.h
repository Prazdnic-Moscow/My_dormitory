#pragma once
#include <HttpController.h>
#include <DbClient.h>
#include <drogon.h>
#include <json/json.h>
#include "News.h"
#include "Util.h"
#include "S3Service.h"
#include "NewsService.h"
#include <string>
#include <filesystem>
#include <jwt-cpp/jwt.h>
#include "traits.h"
using traits = jwt::traits::open_source_parsers_jsoncpp;

using namespace drogon;

class NewsController : public HttpController<NewsController>
{
public:
    METHOD_LIST_BEGIN
        ADD_METHOD_TO(NewsController::postNews, "/news", Post);
        ADD_METHOD_TO(NewsController::getNews, "/news/{}", Get);
        ADD_METHOD_TO(NewsController::deleteNews, "/news/{}/{}", Delete);
    METHOD_LIST_END

    void getNews(const HttpRequestPtr& req,
                 std::function<void(const HttpResponsePtr&)>&& callback, 
                 int limit);

    void deleteNews(const HttpRequestPtr& req,
                    std::function<void(const HttpResponsePtr&)>&& callback, 
                    int id_news,
                    int id_user);
    
    void postNews(const HttpRequestPtr& req,
                   std::function<void(const HttpResponsePtr&)>&& callback);
};