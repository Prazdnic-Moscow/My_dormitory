#include "ReserveWashMachineService.h"
// Конструктор
ReserveWashMachineService::ReserveWashMachineService(const drogon::orm::DbClientPtr& dbClient)
{
    repository = std::make_shared<ReserveWashMachineRepository>(dbClient);
}
ReserveWashMachine ReserveWashMachineService::createReserveWashMachine(const int userId,
                                                                       const int machineId,
                                                                       const std::string date,
                                                                       const std::string startTime,
                                                                       float duration)
{
    std::tm tm{};
    tm.tm_isdst = -1;

    std::istringstream ss(date + " " + startTime);
    ss >> std::get_time(&tm, "%Y-%m-%d %H:%M");

    if (ss.fail()) {
        throw std::runtime_error("Неверный формат даты/времени");
    }

    auto tp = std::chrono::system_clock::from_time_t(std::mktime(&tm));
    auto now = std::chrono::system_clock::now();
    auto max_time = now + std::chrono::hours(24 * 7);

    if (tp <= now) {
        throw std::runtime_error("Нельзя бронировать в прошлом");
    }

    if (tp > max_time) {
        throw std::runtime_error("Максимальный срок бронирования - 7 дней");
    }

    return repository->createReserveWashMachine(userId,
                                                machineId,
                                                date,
                                                startTime,
                                                duration);


}
    
std::list<ReserveWashMachine> ReserveWashMachineService::getReserveWashMachines()
{
    return repository->getReserveWashMachines();
}

bool ReserveWashMachineService::deleteReserveWashMachine(int id)
{
    return repository->deleteReserveWashMachine(id);
}