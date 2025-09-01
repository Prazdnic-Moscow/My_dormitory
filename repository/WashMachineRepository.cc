#include "WashMachineRepository.h"

std::list<WashMachine> WashMachineRepository::getWashMachines()
    {
        auto result = db_->execSqlSync("SELECT * FROM machines");

        std::list<WashMachine> machines;
        for (auto &i : result)
        {
            WashMachine washmachine;
            washmachine.FromDB(i);
            machines.push_back(washmachine);
        }
        return machines;
    }

    bool WashMachineRepository::deleteWashMachine(int id)
    {
        auto result = db_->execSqlSync("DELETE FROM machines WHERE id=$1", id);
        return result.affectedRows() > 0;
    }

    void WashMachineRepository::addWashMachine(const std::string &name)
    {
        db_->execSqlSync("INSERT INTO machines(name) VALUES($1)", name);
    }