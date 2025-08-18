#pragma once
#include <drogon/HttpController.h>
#include "../service/UserService.h"
#include "../model/User.h"
#include "../repository/UserRepository.h"
#include <drogon/orm/DbClient.h>
#include <drogon/drogon.h>
#include <json/json.h>
using namespace drogon;

class Headerhelper : public HttpController<Headerhelper>
{
public:
    static std::string getTokenFromHeaders (const HttpRequestPtr& req);
};