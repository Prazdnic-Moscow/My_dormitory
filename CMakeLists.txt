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

file(GLOB SOURCES
    "main.cpp"
    "controllers/*.cc"
    "models/*.cc"
    "controller/*.cc"
    "service/*.cc"
    "repository/*.cc"
    "Util/*.cc"
)

add_executable(${PROJECT_NAME} ${SOURCES})
target_link_libraries(${PROJECT_NAME}
    PRIVATE
    Drogon::Drogon
    Trantor::Trantor
    PostgreSQL::PostgreSQL
    ${AWSSDK_LINK_LIBRARIES}
    bcrypt
)
target_link_libraries(myapp PRIVATE bcrypt)