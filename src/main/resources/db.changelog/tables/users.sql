--liquibase formatted sql
--changeset spaceship-management:users  runOnChange:true

CREATE TABLE IF NOT EXISTS users
(
    id         BIGINT PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY,
    email      VARCHAR(255)          NOT NULL UNIQUE,
    password   VARCHAR(255)          NOT NULL,
    first_name VARCHAR(255)          NOT NULL,
    last_name  VARCHAR(255)          NOT NULL,
    is_deleted BOOLEAN DEFAULT FALSE NOT NULL
);