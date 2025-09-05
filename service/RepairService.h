#pragma once
#include <string>
#include <list>
#include <memory>
#include "../model/Repair.h"
#include "../repository/RepairRepository.h"
#include <bcrypt/BCrypt.hpp>
#include <jwt-cpp/jwt.h>
#include <drogon/drogon.h>
#include <stdexcept>
#include <iostream>
#include "jwt-cpp/traits/open-source-parsers-jsoncpp/traits.h"
using traits = jwt::traits::open_source_parsers_jsoncpp;
using claim = jwt::basic_claim<traits>;

class RepairService
{
public:
    // Конструктор
    explicit RepairService(const drogon::orm::DbClientPtr& dbClient);

    Repair createRepair
    (
        std::string type,
        std::string body,
        std::string date,
        std::list<std::string> repair_path
    );

    bool deleteRepair(int id_repair);

    std::list<Repair> getRepairs();

private:
    std::shared_ptr<RepairRepository> repository; // Доступ к БД
};