INSERT INTO tb_user (username, first_name, last_name, email, password) VALUES ('rafaelo', 'Rafaelo', 'Cat', 'rafaelo@gmail.com', '$2a$10$uoFp5t1WZGXhT78R.ctmkeAlwSenUA5a95M3zoo8EKbEOoq0axc8S');
INSERT INTO tb_user (username, first_name, last_name, email, password) VALUES ('fernando', 'Fernando', 'Cat', 'fernando@gmail.com', '$2a$10$uoFp5t1WZGXhT78R.ctmkeAlwSenUA5a95M3zoo8EKbEOoq0axc8S');
INSERT INTO tb_user (username, first_name, last_name, email, password) VALUES ('kaka', 'Kaka', 'Dog', 'kaka@gmail.com', '$2a$10$uoFp5t1WZGXhT78R.ctmkeAlwSenUA5a95M3zoo8EKbEOoq0axc8S');

INSERT INTO tb_role (authority) VALUES ('ROLE_READER');
INSERT INTO tb_role (authority) VALUES ('ROLE_WRITER');
INSERT INTO tb_role (authority) VALUES ('ROLE_ADMIN');

INSERT INTO tb_user_role (user_id, role_id) VALUES (1, 2);
INSERT INTO tb_user_role (user_id, role_id) VALUES (1, 3);
INSERT INTO tb_user_role (user_id, role_id) VALUES (2, 2);
INSERT INTO tb_user_role (user_id, role_id) VALUES (3, 1);

INSERT INTO tb_category (name, created_At) VALUES ('Books', NOW());
INSERT INTO tb_category (name, created_At) VALUES ('Games', NOW());
INSERT INTO tb_category (name, created_At) VALUES ('Dystopia', NOW());