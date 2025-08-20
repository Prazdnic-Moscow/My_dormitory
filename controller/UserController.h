#pragma once
#include <drogon/HttpController.h>
#include "../service/UserService.h"
#include "../model/User.h"
#include "../repository/UserRepository.h"
#include <drogon/orm/DbClient.h>
#include <drogon/drogon.h>
#include <json/json.h>
#include "../Util/Util.h"

using namespace drogon;

class UserController : public HttpController<UserController>
{
public:
    METHOD_LIST_BEGIN
    ADD_METHOD_TO(UserController::login, "/login", Post);
    ADD_METHOD_TO(UserController::registerUser, "/register", Post);
    ADD_METHOD_TO(UserController::getUsers, "/users", Get);
    ADD_METHOD_TO(UserController::getUser, "/users/{}", Get);
    ADD_METHOD_TO(UserController::deleteUser, "/users/{}", Delete);
    ADD_METHOD_TO(UserController::addRole, "/users/{}/{}",Put);
    METHOD_LIST_END

    void login(const HttpRequestPtr& req,
              std::function<void(const HttpResponsePtr&)>&& callback);

    void registerUser(const HttpRequestPtr& req,
              std::function<void(const HttpResponsePtr&)>&& callback);

    void getUsers(const HttpRequestPtr& req,
              std::function<void(const HttpResponsePtr&)>&& callback);
    
    void getUser(const HttpRequestPtr& req,
              std::function<void(const HttpResponsePtr&)>&& callback, int userId);
    
    void deleteUser(const HttpRequestPtr& req,
              std::function<void(const HttpResponsePtr&)>&& callback, int userId);

    void addRole(const HttpRequestPtr& req,
              std::function<void(const HttpResponsePtr&)>&& callback, int user_id, int role_id);
};
