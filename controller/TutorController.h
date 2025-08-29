#pragma once
#include <drogon/HttpController.h>
#include <drogon/orm/DbClient.h>
#include <drogon/drogon.h>
#include <json/json.h>
#include "../model/Tutor.h"
#include "../Util/Util.h"
#include "../service/TutorService.h"
#include <string>
#include <filesystem>
#include <jwt-cpp/jwt.h>
#include <uuid/uuid.h>
#include "jwt-cpp/traits/open-source-parsers-jsoncpp/traits.h"
using traits = jwt::traits::open_source_parsers_jsoncpp;
using claim = jwt::basic_claim<traits>;

using namespace drogon;

class TutorController : public HttpController<TutorController>
{
public:
    METHOD_LIST_BEGIN
        ADD_METHOD_TO(TutorController::postTutor, "/tutor", Post);
        ADD_METHOD_TO(TutorController::getTutor, "/tutor", Get);
        ADD_METHOD_TO(TutorController::deleteTutor, "/tutor/{}", Delete);
    METHOD_LIST_END

    void getTutor(const HttpRequestPtr& req,
                 std::function<void(const HttpResponsePtr&)>&& callback);

    void deleteTutor(const HttpRequestPtr& req,
                 std::function<void(const HttpResponsePtr&)>&& callback, int id_tutor);
    
    void postTutor(const HttpRequestPtr& req,
                   std::function<void(const HttpResponsePtr&)>&& callback);
};