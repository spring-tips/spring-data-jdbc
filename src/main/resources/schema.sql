-- minions and persons
create table if not exists person
(
    id   serial primary key,
    name varchar(255)
);

create table if not exists minion
(
    id          serial primary key,
    name        varchar(255),
    evil_master bigint,
    constraint fk_minion_person foreign key (evil_master) references person
);
-- schema
create table if not exists cat
(
    id   serial primary key,
    name varchar(255) not null
);
-- internal
create table if not exists customer
(
    id   serial primary key,
    name text
);

create table if not exists  customer_orders
(
    id serial primary key ,
    customer bigint not null references customer (id),
    name     varchar(255)
);