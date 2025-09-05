#pragma once
#include <string>
#include <list>
#include <memory>
#include "../model/ReserveWashMachine.h"
#include "../repository/ReserveWashMachineRepository.h"
#include <bcrypt/BCrypt.hpp>
#include <jwt-cpp/jwt.h>
#include <drogon/drogon.h>
#include <stdexcept>
#include <iostream>
#include "jwt-cpp/traits/open-source-parsers-jsoncpp/traits.h"
using traits = jwt::traits::open_source_parsers_jsoncpp;
using claim = jwt::basic_claim<traits>;

class ReserveWashMachineService{
public:
    // Конструктор
    explicit ReserveWashMachineService(const drogon::orm::DbClientPtr& dbClient);
    
    ReserveWashMachine createReserveWashMachine(
        int userId,
        int machineId,
        std::string date,
        std::string start_time,
        float duration);

    std::list<ReserveWashMachine> getReserveWashMachines();

    bool deleteReserveWashMachine(int id);

private:
    std::shared_ptr<ReserveWashMachineRepository> repository; // Доступ к БД
};