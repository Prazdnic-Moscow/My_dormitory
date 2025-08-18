#pragma once
#include <drogon/HttpController.h>
#include "../service/UserService.h"
#include "../model/User.h"
#include "../repository/UserRepository.h"
#include <drogon/orm/DbClient.h>
#include <drogon/drogon.h>
#include <json/json.h>

using namespace drogon;

class NewsController : public HttpController<NewsController>
{
public:
    METHOD_LIST_BEGIN
        ADD_METHOD_TO(NewsController::viewNews, "/news", Get);
        ADD_METHOD_TO(NewsController::createNews, "/news", Post);
    METHOD_LIST_END

    void viewNews(const HttpRequestPtr& req,
                 std::function<void(const HttpResponsePtr&)>&& callback);
    
    void createNews(const HttpRequestPtr& req,
                   std::function<void(const HttpResponsePtr&)>&& callback);
};