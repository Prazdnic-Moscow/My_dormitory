#pragma once
#include <drogon/orm/DbClient.h> // Подключение к PostgreSQL
#include <string>
#include "../controller/S3Controller.h"
#include "../model/Thing.h"
#include <list>
#include "bcrypt/BCrypt.hpp"
class ThingRepository 
{
public:
    // Конструктор принимает подключение к БД
    ThingRepository(const drogon::orm::DbClientPtr &dbClient) : db_(dbClient) {}

    // Создать пользователя в БД
    Thing createThing(
        const std::string type, 
        const std::string body, 
        const std::string date,
        std::list<std::string> thing_paths
    );
    
    // Удаление
    bool deleteThing(int id_tutor);

    std::list<Thing> getThings();

private:
    drogon::orm::DbClientPtr db_; // Подключение к PostgreSQL
};