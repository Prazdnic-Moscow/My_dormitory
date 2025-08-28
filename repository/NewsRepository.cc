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
        auto result = db_->execSqlSync
        (
            "DELETE FROM news "
            "WHERE id = ($1) ", id
        );

        if (result.affectedRows() == 0)
        {
            return false;
        }
        return true;
    }

    std::list<News> NewsRepository::getNews()
    {
        try
        {
            auto result = db_->execSqlSync
            (
                "SELECT * FROM news "
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