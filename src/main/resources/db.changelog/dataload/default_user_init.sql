--liquibase formatted sql
--changeset spaceship-management:default_user_init runOnChange:true

INSERT INTO users (email, first_name, last_name, password)
VALUES ('admin@gmail.com', 'Admin', 'Admin','$2a$10$YC1Q6i8hqJjUQlwjv4sype058gRS6MSXB5EeQ6eOG5akZDWZpcyQW')
ON CONFLICT DO NOTHING;

INSERT INTO users_roles (user_id, role_id)
VALUES ((SELECT id FROM users WHERE email = 'admin@gmail.com'),
        (SELECT id FROM roles WHERE role_name = 'ADMIN'))
ON CONFLICT DO NOTHING;

INSERT INTO users (email, first_name, last_name, password)
VALUES ('user@gmail.com', 'User', 'User', '$2a$10$YC1Q6i8hqJjUQlwjv4sype058gRS6MSXB5EeQ6eOG5akZDWZpcyQW')
ON CONFLICT DO NOTHING;

INSERT INTO users_roles (user_id, role_id)
VALUES ((SELECT id FROM users WHERE email = 'user@gmail.com'),
        (SELECT id FROM roles WHERE role_name = 'USER'))
ON CONFLICT DO NOTHING;