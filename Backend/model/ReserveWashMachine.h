#pragma once
#include <string>
#include <list>
#include <drogon.h>

class ReserveWashMachine 
{
    int id;
    int userId;
    int machineId;
    std::string date;
    std::string startTime;
    float duration;

    public:
        void FromDB(const drogon::orm::Row& result)
        {
            id = result["id"].as<int>();
            userId = result["user_id"].as<int>();
            machineId = result["machine_id"].as<int>();
            date = result["date"].as<std::string>();
            startTime = result["start_time"].as<std::string>();
            duration = result["duration"].as<float>();
        }

        int getId() const
        {
            return id;
        }

        int getUserId() const
        {
            return userId;
        }

        int getMachineId() const
        {
            return machineId;
        }

        std::string getDate() const
        {
            return date;
        }

        std::string getStartTime() const
        {
            return startTime;
        }

        float getDuration() const
        {
            return duration;
        }

        void setUserId(int& userid)
        {
            userId = userid;
        }

        void setMachineId(int& machineid)
        {
            machineId = machineid;
        }

        void setDate(const std::string& datee)
        {
            date = datee;
        }

        void setStartTime(const std::string& starttime)
        {
            startTime = starttime;
        }

        void setDuration(float durationn)
        {
            duration = durationn;
        }
};