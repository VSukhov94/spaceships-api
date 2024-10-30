--liquibase formatted sql
--changeset spaceship-management:roles_init runOnChange:true

INSERT INTO roles (role_name) VALUES ('ADMIN') ON CONFLICT DO NOTHING;
INSERT INTO roles (role_name) VALUES ('USER') ON CONFLICT DO NOTHING;