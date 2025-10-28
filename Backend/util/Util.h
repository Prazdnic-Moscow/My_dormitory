#pragma once
#include <HttpController.h>
#include "S3Service.h"
#include <DbClient.h>
#include <drogon.h>
#include <json/json.h>
#include "traits.h"
#include <string>
#include <jwt-cpp/jwt.h>
using namespace drogon;
using traits = jwt::traits::open_source_parsers_jsoncpp;

class Headerhelper : public HttpController<Headerhelper>
{
    public:
        static std::string getTokenFromHeaders (const HttpRequestPtr& req);

        static bool verifyToken (jwt::decoded_jwt<traits> decoded);

        static std::string getTokenType(jwt::decoded_jwt<traits> decoded);

        static bool checkRoles (jwt::decoded_jwt<traits> decoded, 
                                std::string role);
        
        static std::string getExtension(drogon::ContentType contentType);

        static void responseCheckJson(const std::function<void(const HttpResponsePtr&)>& callback);

        static void responseCheckToken(const std::function<void(const HttpResponsePtr&)>& callback);

        static void responseCheckRoles(const std::function<void(const HttpResponsePtr&)>& callback);
};