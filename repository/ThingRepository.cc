#include "ThingRepository.h"
    // Создать пользователя в БД
    Thing ThingRepository::createThing(
        const std::string type, 
        const std::string body, 
        const std::string date,
        std::list<std::string> thing_paths
    )
    {
        try 
        {
            auto transaction = db_->newTransaction();
            
            // Создаем запись файла
            auto result = transaction->execSqlSync(
                "INSERT INTO thing (type, body, date) "
                "VALUES ($1, $2, $3) "
                "RETURNING id, type, body, date",
                type, body, date
            );
            
            Thing thing;
            thing.fromDb(result[0]);
            int thing_id = thing.getId();
            
            // Добавляем файлы
            if (!thing_paths.empty()) 
            {
                for (const auto& thing_path : thing_paths) 
                {
                    transaction->execSqlSync(
                        "INSERT INTO thing_file (thing_id, image_path) "
                        "VALUES ($1, $2)",
                        thing_id, thing_path
                    );
                }
            }

            thing.setFilePaths(thing_paths);
            return thing;
            
        } 
        catch (const std::exception& e) 
        {
            throw std::runtime_error("Error creating file: " + std::string(e.what()));
        }
    }
    
    // Удаление
    bool ThingRepository::deleteThing(int id_thing)
    {
        // Удаляем файлы из MinIO
        S3Service s3service("mydormitory");
        auto files_result = db_->execSqlSync(
            "SELECT image_path FROM thing_file WHERE thing_id = $1",
            id_thing
        );
        
        for (const auto& row : files_result) 
        {
            std::string thing_path = row["image_path"].as<std::string>();
            s3service.deleteFile(thing_path);
        }
        
        // Удаляем файлы из БД
        db_->execSqlSync(
            "DELETE FROM thing_file WHERE thing_id = $1",
            id_thing
        );
        
        // Удаляем запись файла
        auto result = db_->execSqlSync(
            "DELETE FROM thing WHERE id = $1",
            id_thing
        );
        return result.affectedRows() > 0;
    }

    std::list<Thing> ThingRepository::getThings()
    {
        try
        {
            auto result = db_->execSqlSync(
                "SELECT * FROM thing "
                "ORDER BY date DESC "
            );
            
            std::list<Thing> files_all;
            for (const auto& row : result) 
            {
                Thing thing;
                thing.fromDb(row);
                int thing_id = thing.getId();
                // Получаем файлы
                auto files_result = db_->execSqlSync(
                    "SELECT image_path FROM thing_file WHERE thing_id = $1 ORDER BY thing_id",
                    thing_id
                );
                
                std::list<std::string> thing_paths;
                for (const auto& file_row : files_result) 
                {
                    thing_paths.push_back(file_row["image_path"].as<std::string>());
                }
                thing.setFilePaths(thing_paths);
                
                files_all.push_back(thing);
            }
            return files_all;
            
        } 
        catch (const std::exception& e) 
        {
            throw std::runtime_error("Error getting files: " + std::string(e.what()));
        }
    }