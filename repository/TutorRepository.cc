#include "TutorRepository.h"
// Создать пользователя в БД
    Tutor TutorRepository::createTutor(
        const std::string header, 
        const std::string body, 
        const std::string date, 
        const std::string image_path
    )
    {
        auto result = db_->execSqlSync(
            "INSERT INTO tutor(header, body, date, image_path) "
            "VALUES ($1, $2, $3, $4) "
            "RETURNING id, header, body, date, image_path", header, body, date, image_path
        );
        if (result.empty()) 
        {
            throw std::runtime_error("Failed to create tutor");
        }   
        Tutor tutor;
        tutor.fromDb(result[0]);
        return tutor;
    }


    std::list<Tutor> TutorRepository::getTutor()
    {
        try
        {
            auto result = db_->execSqlSync(
            "SELECT * FROM tutor "
            );
            std::list<Tutor> tutor_all;
            for (int i = 0; i < result.size(); i++)
            {
                Tutor tutor;
                tutor.fromDb(result[i]);
                tutor_all.push_back(tutor);
            }
            return tutor_all;
        }
        catch(const std::exception& e)
        {
            std::cerr << e.what() << '\n';
        }
    }

    // Удаление
    bool TutorRepository::deleteTutor(int id_tutor)
    {
        //для начала нужно удалить фотку из minio
        S3Service s3service("mydormitory");
        auto result = db_->execSqlSync
        (
            "SELECT * FROM tutor "
            "WHERE id = $1 ", id_tutor
        );
        Tutor tutor;
        tutor.fromDb(result[0]);
        std::string image_path = tutor.getImagePath();
        s3service.deleteFile(image_path);

        auto result_2 = db_->execSqlSync
        (
            "DELETE FROM tutor "
            "WHERE id = $1 ", id_tutor
        );
        
        if (result_2.affectedRows() == 0)
        {
            return false;
        }
        return true;
    }