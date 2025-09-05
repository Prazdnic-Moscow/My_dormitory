#include "ThingService.h"
// Конструктор
ThingService::ThingService(const drogon::orm::DbClientPtr& dbClient)
    {
        repository = std::make_shared<ThingRepository>(dbClient);
    }
Thing ThingService::createThing
(
    std::string type,
    std::string body,
    std::string date,
    std::list<std::string> file_path
)
{
    return repository->createThing(type, body, date, file_path);
}


bool ThingService::deleteThing(int id_thing)
{
    return repository->deleteThing(id_thing);
}

std::list<Thing> ThingService::getThings()
{
    return repository->getThings();
}