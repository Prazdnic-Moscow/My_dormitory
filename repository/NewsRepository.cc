#include "NewsRepository.h"
    News NewsRepository::createNews(
        const std::string header, 
        const std::string body, 
        const std::string author,
        const std::string date, 
        const std::string date_start, 
        const std::string date_end,
        const std::string image_path
    )
    {
        auto result = db_->execSqlSync
        (
            "INSERT INTO news (header, body, author, date, date_start, date_end, image_path) "
            "VALUES ($1, $2, $3, $4, $5, $6, $7) "
            "RETURNING id, header, body, author, date, date_start, date_end",
            header, body, author, date, date_start, date_end, image_path
        );
        News news;
        news.fromDb(result[0]);
        return news;
    }

    bool NewsRepository::deleteNews(int id)
    {
        //для начала нужно удалить фотку из minio
        S3Service s3service("mydormitory");
        auto result = db_->execSqlSync(
            "SELECT * FROM news "
            "WHERE id = $1 ", id
        );
        News news;
        news.fromDb(result[0]);
        std::string image_path = news.getImagePath();
        s3service.deleteFile(image_path);

        auto result_2 = db_->execSqlSync
        (
            "DELETE FROM news "
            "WHERE id = ($1) ", id
        );

        if (result_2.affectedRows() == 0)
        {
            return false;
        }
        return true;
    }

    std::list<News> NewsRepository::getNews(int limit)
    {
        try
        {
            std::string limit_str = std::to_string(limit);
            auto result = db_->execSqlSync(
            "SELECT * FROM news "
            "ORDER BY date DESC "
            "LIMIT $1 ",limit_str
            );

            std::list<News> news_all;
            for (int i =0; i < result.size(); i++)
            {
                News news;
                news.fromDb(result[i]);
                news_all.push_back(news);
            }

            return news_all;
        }
        catch(const std::exception& e)
        {
            std::cerr << e.what() << '\n';
        }
        
    }