#include "NewsService.h"

// Конструктор — инициализируем репозиторий
NewsService::NewsService(const drogon::orm::DbClientPtr& dbClient)
{
    repository = std::make_shared<NewsRepository>(dbClient);
}
News NewsService::createNews
(
    std::string header,
    std::string body,
    std::string author,
    std::string date,
    std::string date_start,
    std::string date_end,
    std::list<std::string> image_paths
)
{
    if (image_paths.size() >= 5)
    {
        throw std::runtime_error("Count files should be < 5");
    }
    return repository->createNews(
        header,
        body,
        author,
        date,
        date_start,
        date_end,
        image_paths
    );
}

bool NewsService::deleteNews(int id_news)
{
    return repository->deleteNews(id_news);
}

std::list<News> NewsService::getNews(int limit)
{
    return repository->getNews(limit);
}
