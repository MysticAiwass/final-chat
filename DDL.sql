create table roles
(
    role_id   serial primary key,
    name_role varchar(50) unique not null
);

create table users
(
    user_id  serial primary key,
    login    varchar(50) unique not null,
    password varchar(255)       not null,
    username varchar(50)        not null
);

create table user_role
(
    user_role_id serial primary key,
    user_id      int references users (user_id),
    role_id      int references roles (role_id)
);