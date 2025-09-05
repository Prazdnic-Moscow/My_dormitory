#pragma once
#include <drogon/HttpController.h>
#include "../service/S3Service.h"
#include <drogon/orm/DbClient.h>
#include <drogon/drogon.h>
#include <json/json.h>
#include "jwt-cpp/traits/open-source-parsers-jsoncpp/traits.h"
#include <string>
#include <jwt-cpp/jwt.h>
using namespace drogon;
using traits = jwt::traits::open_source_parsers_jsoncpp;
using claim = jwt::basic_claim<traits>;

class Headerhelper : public HttpController<Headerhelper>
{
    public:
        static std::string getTokenFromHeaders (const HttpRequestPtr& req);

        static bool verifyToken (jwt::decoded_jwt<traits> decoded);

        static std::string getTokenType(jwt::decoded_jwt<traits> decoded);

        static bool checkRoles (jwt::decoded_jwt<traits> decoded, 
                                std::string role);
        
        static std::string getExtension(drogon::ContentType contentType);
};