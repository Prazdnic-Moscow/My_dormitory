#include "WashMachineService.h"
// Конструктор
WashMachineService::WashMachineService(const drogon::orm::DbClientPtr& dbClient)
{
    repository = std::make_shared<WashMachineRepository>(dbClient);
}

    std::list<WashMachine> WashMachineService::getWashMachines()
{
    return repository->getWashMachines();
}

void WashMachineService::addWashMachine(const std::string name)
{
    repository->addWashMachine(name);
}

bool WashMachineService::deleteWashMachine(int id)
{
    return repository->deleteWashMachine(id);
}