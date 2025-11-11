#include "RepairService.h"
// Конструктор
RepairService::RepairService(const drogon::orm::DbClientPtr& dbClient)
{
    repository = std::make_shared<RepairRepository>(dbClient);
}
Repair RepairService::createRepair(std::string type,
                                   std::string body,
                                   int room,
                                   std::list<std::string> repair_path,
                                   int user_id)
{
    return repository->createRepair(type, 
                                    body, 
                                    room,
                                    repair_path,
                                    user_id);
}


bool RepairService::deleteRepair(int id_repair)
{
    return repository->deleteRepair(id_repair);
}

std::list<Repair> RepairService::getRepairs()
{
    return repository->getRepairs();
}