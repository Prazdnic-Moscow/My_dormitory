#pragma once
#include <list>
#include <string>
#include <memory>
#include "../model/User.h"
#include "../Util/Util.h"
#include "../repository/UserRepository.h"
#include <bcrypt/BCrypt.hpp>
#include <jwt-cpp/jwt.h>
#include <drogon/drogon.h>
#include <stdexcept>
#include <iostream>
#include <drogon/HttpResponse.h>
#include <drogon/HttpTypes.h>

class UserService 
{
    public:
        // Конструктор
        explicit UserService(const drogon::orm::DbClientPtr& dbClient);

        UserData registerUser(const std::string &phone_number,
                              const std::string &password,
                              const std::string &name,
                              const std::string &last_name,
                              const std::string &surname,
                              const std::list<std::string> &document);

        std::list<std::string> login(const std::string &phone_number, 
                                     const std::string &password);

        UserData getUser(int id);

        bool deleteUser(int id);
        
        std::list<UserData> getUsers();

        void checkData(const std::string &phone_number, 
                       const std::string &password);
        
        void addRole(int user_id, 
                     int role_id);
        
        bool deleteRole(int user_id, 
                        int role_id);


        bool checkUserExists(std::string phone_number);

        std::list<std::string> refreshTokens(const std::string &refresh_token);

    private:
        std::shared_ptr<UserRepository> repository; // Доступ к БД
};