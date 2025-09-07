cmake_minimum_required(VERSION 3.16)
project(myapp LANGUAGES CXX)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(SERVICE_COMPONENTS s3)

find_package(AWSSDK REQUIRED COMPONENTS s3 core)
find_package(Drogon REQUIRED COMPONENTS postgres)
find_package(Trantor REQUIRED)
find_package(PostgreSQL REQUIRED)
find_path(BCRYPT_INCLUDE_DIR NAMES bcrypt/BCrypt.hpp PATHS /usr/local/include)

# Находим конкретные пути для Drogon
find_path(DROGON_ORM_DIR NAMES DbClient.h PATHS
    /usr/local/include/drogon/orm
    /usr/include/drogon/orm
    ${DROGON_INCLUDE_DIRS}/orm
    PATH_SUFFIXES drogon/orm
)

find_path(DROGON_MAIN_DIR NAMES HttpController.h PATHS
    /usr/local/include/drogon
    /usr/include/drogon
    ${DROGON_INCLUDE_DIRS}
    PATH_SUFFIXES drogon
)

# Находим путь для jwt-cpp
find_path(JWT_CPP_INCLUDE_DIR NAMES jwt-cpp/jwt.h PATHS
    /usr/local/include
    /usr/include
)

find_path(JWT_CPP_TRAITS_DIR NAMES traits/open-source-parsers-jsoncpp/traits.h PATHS
    /usr/local/include/jwt-cpp
    /usr/include/jwt-cpp
    ${JWT_CPP_BASE_DIR}/jwt-cpp
)

# Ключевое: находим ПРЯМОЙ путь к директории с traits.h
find_path(JWT_TRAITS_DIR NAMES traits.h PATHS
    /usr/local/include/jwt-cpp/traits/open-source-parsers-jsoncpp
    /usr/include/jwt-cpp/traits/open-source-parsers-jsoncpp
    /usr/local/include
    /usr/include
    PATH_SUFFIXES jwt-cpp/traits/open-source-parsers-jsoncpp
)

find_path(JWT_CPP_BASE_DIR NAMES jwt.h PATHS
    /usr/local/include/jwt-cpp
    /usr/include/jwt-cpp
    /usr/local/include
    /usr/include
    PATH_SUFFIXES jwt-cpp
)


# Находим пути для AWS SDK
find_path(AWS_CORE_DIR NAMES Aws.h PATHS
    /usr/local/include/aws/core
    /usr/include/aws/core
    ${AWSSDK_INCLUDE_DIR}/aws/core
)

find_path(AWS_S3_DIR NAMES S3Client.h PATHS
    /usr/local/include/aws/s3
    /usr/include/aws/s3
    ${AWSSDK_INCLUDE_DIR}/aws/s3
)

find_path(AWS_S3_MODEL_DIR NAMES PutObjectRequest.h PATHS
    /usr/local/include/aws/s3/model
    /usr/include/aws/s3/model
    ${AWSSDK_INCLUDE_DIR}/aws/s3/model
)

find_path(AWS_UTILS_DIR NAMES AWSStringStream.h PATHS
    /usr/local/include/aws/core/utils/memory/stl
    /usr/include/aws/core/utils/memory/stl
    ${AWSSDK_INCLUDE_DIR}/aws/core/utils/memory/stl
)

find_path(AWS_AUTH_DIR NAMES AWSCredentials.h PATHS
    /usr/local/include/aws/core/auth
    /usr/include/aws/core/auth
    ${AWSSDK_INCLUDE_DIR}/aws/core/auth
)


file(GLOB SOURCES
    "main.cpp"
    "models/*.cc"
    "controller/*.cc"
    "service/*.cc"
    "repository/*.cc"
    "Util/*.cc"
)

add_executable(${PROJECT_NAME} ${SOURCES})

# Ключевое: добавляем все директории в include path
target_include_directories(${PROJECT_NAME} PRIVATE
    ${AWS_CORE_DIR}                   
    ${AWS_S3_DIR}                     
    ${AWS_S3_MODEL_DIR}               
    ${AWS_UTILS_DIR}                  
    ${AWS_AUTH_DIR}
    ${DROGON_ORM_DIR}                 
    ${DROGON_MAIN_DIR}                
    ${JWT_CPP_BASE_DIR}               
    ${JWT_CPP_TRAITS_DIR}   
    ${JWT_TRAITS_DIR}     
    ${JWT_CPP_BASE_DIR}
    ${CMAKE_SOURCE_DIR}/model   
    ${CMAKE_SOURCE_DIR}/controller  
    ${CMAKE_SOURCE_DIR}/service   
    ${CMAKE_SOURCE_DIR}/repository
    ${CMAKE_SOURCE_DIR}/Util      
    ${BCRYPT_INCLUDE_DIR}
)

target_link_libraries(${PROJECT_NAME}
    PRIVATE
    Drogon::Drogon
    Trantor::Trantor
    PostgreSQL::PostgreSQL
    ${AWSSDK_LINK_LIBRARIES}
    bcrypt
)
target_link_libraries(myapp PRIVATE bcrypt)