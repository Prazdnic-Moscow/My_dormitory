#include "NewsController.h"
// void NewsController::viewNews(const HttpRequestPtr& req,
//               std::function<void(const HttpResponsePtr&)>&& callback)
//     {
//         std::string token = Headerhelper::getTokenFromHeaders(req);
//         auto decoded = jwt::decode(token);
//         jwt::verify()
//             .allow_algorithm(jwt::algorithm::hs256{"your_secret_key"})
//             .verify(decoded);
        
//     }



// void createNews(const HttpRequestPtr& req,
//                    std::function<void(const HttpResponsePtr&)>&& callback)
//     {
//         std::string token = Headerhelper::getTokenFromHeaders(req);
//         auto decoded = jwt::decode(token);
//         jwt::verify()
//             .allow_algorithm(jwt::algorithm::hs256{"your_secret_key"})
//             .verify(decoded);

//         auto role_id = decoded.get_header_claim("role");
//         auto role = std::stoi(role_id.as_string());

//         if (role == 1)
//         {
//             //Вызов метода создания
//         }
        
//         else
//         {
//             //throw вывод ошибки
//         }
//     }