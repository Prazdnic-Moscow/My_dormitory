#pragma once
#include <drogon/HttpController.h>
#include "../service/UserService.h"
#include "../model/User.h"
#include "../repository/UserRepository.h"
#include <drogon/orm/DbClient.h>
#include <drogon/drogon.h>
#include <json/json.h>
#include "../controller/NewsController.h"
#include "jwt-cpp/traits/open-source-parsers-jsoncpp/traits.h"
#include <string>
#include <filesystem>
#include <jwt-cpp/jwt.h>
#include <uuid/uuid.h>
using namespace drogon;
using traits = jwt::traits::open_source_parsers_jsoncpp;
using claim = jwt::basic_claim<traits>;

class Headerhelper : public HttpController<Headerhelper>
{
public:
    static std::string getTokenFromHeaders (const HttpRequestPtr& req);
    static bool verifyToken (jwt::decoded_jwt<traits> decoded);
    static bool checkRoles (jwt::decoded_jwt<traits> decoded, std::string role);
    static std::string getExtension(drogon::ContentType contentType);
};