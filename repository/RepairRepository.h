#pragma once
#include <drogon/orm/DbClient.h> // Подключение к PostgreSQL
#include <string>
#include "../controller/S3Controller.h"
#include "../model/Repair.h"
#include <list>
#include "bcrypt/BCrypt.hpp"
class RepairRepository 
{
public:
    // Конструктор принимает подключение к БД
    RepairRepository(const drogon::orm::DbClientPtr &dbClient) : db_(dbClient) {}

    // Создать пользователя в БД
    Repair createRepair(
        const std::string type, 
        const std::string body, 
        const std::string date,
        std::list<std::string> repair_paths
    );
    
    // Удаление
    bool deleteRepair(int id_repair);

    std::list<Repair> getRepairs();

private:
    drogon::orm::DbClientPtr db_; // Подключение к PostgreSQL
};