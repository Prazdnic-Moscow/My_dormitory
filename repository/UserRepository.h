#pragma once
#include <drogon/orm/DbClient.h> // Подключение к PostgreSQL
#include <string>
#include "../model/User.h"
#include <list>
#include "bcrypt/BCrypt.hpp"
class UserRepository 
{
public:
    // Конструктор принимает подключение к БД
    UserRepository(const drogon::orm::DbClientPtr &dbClient) : db_(dbClient) {}

    // Создать пользователя в БД
    UserData createUser(
        const std::string &phone_number, 
        const std::string &passwordHash, 
        const std::string &name,
        const std::string &last_name, 
        const std::string &surname, 
        const std::string &document
    );
    
    UserData getUserByPhone(const std::string &phone_number);
    
    std::list<UserData> getUsers();

    UserData getUser(int id);
    
    // Удаление
    void deleteUser(int id);

    void addRole(int user_id, int role_id);

private:
    drogon::orm::DbClientPtr db_; // Подключение к PostgreSQL
};