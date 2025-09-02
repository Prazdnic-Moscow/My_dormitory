#pragma once
#include <drogon/orm/DbClient.h> // Подключение к PostgreSQL
#include <string>
#include "../controller/S3Controller.h"
#include "../model/Tutor.h"
#include <list>
#include "bcrypt/BCrypt.hpp"
class TutorRepository 
{
public:
    // Конструктор принимает подключение к БД
    TutorRepository(const drogon::orm::DbClientPtr &dbClient) : db_(dbClient) {}

    // Создать пользователя в БД
    Tutor createTutor(
        const std::string header, 
        const std::string body, 
        const std::string date, 
        const std::list<std::string> image_path
    );
    
    // Удаление
    bool deleteTutor(int id_tutor);

    std::list<Tutor> getTutor();

private:
    drogon::orm::DbClientPtr db_; // Подключение к PostgreSQL
};