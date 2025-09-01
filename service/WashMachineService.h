#pragma once
#include <string>
#include <list>
#include <memory>
#include "../model/WashMachine.h"
#include "../repository/WashMachineRepository.h"
#include <bcrypt/BCrypt.hpp>
#include <jwt-cpp/jwt.h>
#include <drogon/drogon.h>
#include <stdexcept>
#include <iostream>
#include "jwt-cpp/traits/open-source-parsers-jsoncpp/traits.h"
using traits = jwt::traits::open_source_parsers_jsoncpp;
using claim = jwt::basic_claim<traits>;

class WashMachineService{
public:
    // Конструктор
    explicit WashMachineService(const drogon::orm::DbClientPtr& dbClient);
        
    std::list<WashMachine> getWashMachines();

    void addWashMachine(const std::string name);

    bool deleteWashMachine(int id);

private:
    std::shared_ptr<WashMachineRepository> repository; // Доступ к БД
};