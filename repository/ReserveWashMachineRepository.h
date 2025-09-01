#pragma once
#include <drogon/orm/DbClient.h>
#include "../model/ReserveWashMachine.h"
#include <list>
#include <chrono>
class ReserveWashMachineRepository {
public:
    explicit ReserveWashMachineRepository(const drogon::orm::DbClientPtr& dbClient) 
        : db_(dbClient) {}
    ReserveWashMachine createReserveWashMachine(
            const std::string &userId,
            const std::string &machineId,
            const std::string &date,
            const std::string &startTime,
            float duration);

    std::list<ReserveWashMachine> getReserveWashMachines();
    bool deleteReserveWashMachine(int id);

    private:
    drogon::orm::DbClientPtr db_;
};