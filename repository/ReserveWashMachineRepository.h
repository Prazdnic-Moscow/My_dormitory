#pragma once
#include <DbClient.h>
#include "ReserveWashMachine.h"
#include <list>
#include <chrono>
class ReserveWashMachineRepository 
{
    public:
        explicit ReserveWashMachineRepository(const drogon::orm::DbClientPtr& dbClient) 
            : db_(dbClient) {}
        
        ReserveWashMachine createReserveWashMachine(const int& userId,
                                                    const int& machineId,
                                                    const std::string &date,
                                                    const std::string &startTime,
                                                    float duration);

        std::list<ReserveWashMachine> getReserveWashMachines();
        
        bool deleteReserveWashMachine(int id);

    private:
        drogon::orm::DbClientPtr db_;
};