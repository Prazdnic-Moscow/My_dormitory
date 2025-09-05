#include "ReserveWashMachineRepository.h"

ReserveWashMachine ReserveWashMachineRepository::createReserveWashMachine(
        const int &userId,
        const int &machineId,
        const std::string &date,
        const std::string &startTime,
        float duration)
    {
        // 2. Скользящее окно: проверяем сумму забронированных часов за последние 7 дней
        auto totalResult = db_->execSqlSync(
            "SELECT COALESCE(SUM(duration), 0) AS total_hours "
            "FROM bookings "
            "WHERE user_id=$1 "
            "AND date >= ($2::date - INTERVAL '6 days') "
            "AND date <= $2::date",
            userId, date
        );

        float totalHours = 0.0;
        if (!totalResult.empty())
        {
            totalHours = totalResult[0]["total_hours"].as<float>();
        }

        if (totalHours + duration > 3.0)
        {
            throw std::runtime_error("Превышен недельный лимит брони (3 часа) в скользящем окне");
        }
        // Проверка на пересечение времени
        auto check = db_->execSqlSync(
            "SELECT * FROM bookings "
            "WHERE machine_id=$1 AND date=$2 "
            "AND (start_time, start_time + interval '1 hour' * duration) "
            "OVERLAPS ($3::time, ($3::time + interval '1 hour' * $4)) ",
            machineId, date, startTime, duration);

        if (!check.empty())
        {
            throw std::runtime_error("Машина уже забронирована на это время");
        }

        auto result = db_->execSqlSync(
            "INSERT INTO bookings (user_id, machine_id, date, start_time, duration) "
            "VALUES ($1, $2, $3, $4, $5) "
            "RETURNING id, user_id, machine_id, date, start_time, duration",
            userId, machineId, date, startTime, duration);

        ReserveWashMachine r;
        r.FromDB(result[0]);
        return r;
    }

    // Получить все брони
    std::list<ReserveWashMachine> ReserveWashMachineRepository::getReserveWashMachines()
    {

        auto result = db_->execSqlSync(
            "SELECT * FROM bookings ");

        std::list<ReserveWashMachine> reservations;
        for (auto &row : result)
        {
            ReserveWashMachine r;
            r.FromDB(row);
            reservations.push_back(r);
        }
        return reservations;
    }

    // Удалить бронь
    bool ReserveWashMachineRepository::deleteReserveWashMachine(int id)
    {
        auto result = db_->execSqlSync("DELETE FROM bookings WHERE id=$1", id);
        return result.affectedRows() > 0;
    }