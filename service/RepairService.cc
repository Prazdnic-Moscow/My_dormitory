#include "RepairService.h"
// Конструктор
RepairService::RepairService(const drogon::orm::DbClientPtr& dbClient)
{
    repository = std::make_shared<RepairRepository>(dbClient);
}
Repair RepairService::createRepair(std::string type,
                                   std::string body,
                                   std::string date,
                                   std::list<std::string> repair_path)
{
    return repository->createRepair(type ,body, date, repair_path);
}


bool RepairService::deleteRepair(int id_repair)
{
    return repository->deleteRepair(id_repair);
}

std::list<Repair> RepairService::getRepairs()
{
    return repository->getRepairs();
}