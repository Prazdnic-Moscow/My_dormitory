#pragma once
#include <string>
#include <list>
#include <memory>
#include "../model/Tutor.h"
#include "../repository/TutorRepository.h"
#include <bcrypt/BCrypt.hpp>
#include <jwt-cpp/jwt.h>
#include <drogon/drogon.h>
#include <stdexcept>
#include <iostream>
#include "jwt-cpp/traits/open-source-parsers-jsoncpp/traits.h"
using traits = jwt::traits::open_source_parsers_jsoncpp;
using claim = jwt::basic_claim<traits>;

class TutorService
{
public:
    // Конструктор
    explicit TutorService(const drogon::orm::DbClientPtr& dbClient);

    Tutor createTutor
    (
        std::string header,
        std::string body,
        std::string date,
        std::string image_path
    );

    bool deleteTutor(int id_tutor);

    std::list<Tutor> getTutor();

private:
    std::shared_ptr<TutorRepository> repository; // Доступ к БД
};