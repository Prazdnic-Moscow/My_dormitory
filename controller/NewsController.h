#pragma once
#include <drogon/HttpController.h>
#include "../service/UserService.h"
#include "../model/User.h"
#include "../repository/UserRepository.h"
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

class NewsController : public HttpController<NewsController>
{
public:
    METHOD_LIST_BEGIN
        ADD_METHOD_TO(NewsController::getNews, "/news", Get);
        ADD_METHOD_TO(NewsController::deleteNews, "/news/{}", Delete);
        ADD_METHOD_TO(NewsController::createNews, "/news", Post);
        ADD_METHOD_TO(NewsController::getImage, "/news/image/{}", Get);
        METHOD_LIST_END

    void getNews(const HttpRequestPtr& req,
                 std::function<void(const HttpResponsePtr&)>&& callback);

    void deleteNews(const HttpRequestPtr& req,
                 std::function<void(const HttpResponsePtr&)>&& callback, int id_news);
    
    void createNews(const HttpRequestPtr& req,
                   std::function<void(const HttpResponsePtr&)>&& callback);

    void getImage(const HttpRequestPtr& req,
                 std::function<void(const HttpResponsePtr&)>&& callback,
                 const std::string& filename);
};