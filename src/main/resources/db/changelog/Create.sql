create table users
(
    user_id     bigserial primary key,
    email       varchar(255) not null unique,
    password    varchar(255) not null,
    status      varchar(255),
    first_name  varchar(255) not null,
    last_name   varchar(255) not null,
    middle_name varchar(255),
    telephone   varchar(255),
    birth_date  date         not null
);