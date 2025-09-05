#pragma once
#include <string>
#include <list>
#include <memory>
#include "../model/Thing.h"
#include "../repository/ThingRepository.h"
#include <bcrypt/BCrypt.hpp>
#include <jwt-cpp/jwt.h>
#include <drogon/drogon.h>
#include <stdexcept>
#include <iostream>
#include "jwt-cpp/traits/open-source-parsers-jsoncpp/traits.h"
using traits = jwt::traits::open_source_parsers_jsoncpp;
using claim = jwt::basic_claim<traits>;

class ThingService
{
public:
    // Конструктор
    explicit ThingService(const drogon::orm::DbClientPtr& dbClient);

    Thing createThing
    (
        std::string type,
        std::string body,
        std::string date,
        std::list<std::string> file_path
    );

    bool deleteThing(int id_thing);

    std::list<Thing> getThings();

private:
    std::shared_ptr<ThingRepository> repository; // Доступ к БД
};