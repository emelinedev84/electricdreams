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

INSERT INTO tb_tag (name) VALUES ('Fiction');
INSERT INTO tb_tag (name) VALUES ('Technology');

INSERT INTO tb_post (image_url, created_at, updated_at, user_id) VALUES ('https://picsum.photos/seed/p1/600/400', NOW(), NOW(), 1);
INSERT INTO tb_post (image_url, created_at, updated_at, user_id) VALUES ('https://picsum.photos/seed/p2/600/400', NOW(), NOW(), 1);
INSERT INTO tb_post (image_url, created_at, updated_at, user_id) VALUES ('https://picsum.photos/seed/p3/600/400', NOW(), NOW(), 1);
INSERT INTO tb_post (image_url, created_at, updated_at, user_id) VALUES ('https://picsum.photos/seed/p4/600/400', NOW(), NOW(), 1);

INSERT INTO tb_post_category (post_id, category_id) VALUES (1, 3); -- Dystopia
INSERT INTO tb_post_category (post_id, category_id) VALUES (2, 2); -- Games
INSERT INTO tb_post_category (post_id, category_id) VALUES (3, 1); -- Books
INSERT INTO tb_post_category (post_id, category_id) VALUES (4, 1); -- Books

INSERT INTO tb_post_tag (post_id, tag_id) VALUES (1, 1); -- Fiction
INSERT INTO tb_post_tag (post_id, tag_id) VALUES (2, 2); -- Technology
INSERT INTO tb_post_tag (post_id, tag_id) VALUES (3, 1); -- Fiction
INSERT INTO tb_post_tag (post_id, tag_id) VALUES (4, 2); -- Technology

INSERT INTO tb_content (language, url_handle, title, content, meta_description, is_draft, post_id) VALUES ('EN', 'blade-runner-and-electric-dreams', 'Blade Runner and Electric Dreams', '<p>Exploring the dystopia of Blade Runner.</p>', 'Explore Blade Runner themes', FALSE, 1);
INSERT INTO tb_content (language, url_handle, title, content, meta_description, is_draft, post_id) VALUES ('PT', 'blade-runner-e-sonhos-eletricos', 'Blade Runner e Sonhos Elétricos', '<p>Explorando a distopia de Blade Runner.</p>', 'Explore os temas de Blade Runner', FALSE, 1);
INSERT INTO tb_content (language, url_handle, title, content, meta_description, is_draft, post_id) VALUES ('EN', 'future-of-ai', 'The Future of AI', '<p>Discussing artificial intelligence and society.</p>', 'AI and its impact', FALSE, 2);
INSERT INTO tb_content (language, url_handle, title, content, meta_description, is_draft, post_id) VALUES ('EN', 'digital-utopia', 'Digital Utopia', '<p>Can technology build a perfect world?</p>', 'Tech-driven utopias and their paradoxes', FALSE, 3);
INSERT INTO tb_content (language, url_handle, title, content, meta_description, is_draft, post_id) VALUES ('PT', 'utopia-digital', 'Utopia Digital', '<p>A tecnologia pode construir um mundo perfeito?</p>', 'Utopias tecnológicas e seus paradoxos', TRUE, 3);
INSERT INTO tb_content (language, url_handle, title, content, meta_description, is_draft, post_id) VALUES ('PT', 'inteligencia-artificial-no-cotidiano', 'Inteligência Artificial no Cotidiano', '<p>Como a IA já está presente nas nossas rotinas.</p>', 'Aplicações práticas da IA no dia a dia', FALSE, 4);
        
INSERT INTO tb_comment (content, created_at, is_visible, is_from_author, language, user_id, post_id) VALUES ('Great post! Blade Runner is my favorite movie.', NOW(), TRUE, FALSE, 'EN', 2, 1);
INSERT INTO tb_comment (content, created_at, is_visible, is_from_author, language, user_id, post_id) VALUES ('Adorei esse post! Muito bem escrito.', NOW(), TRUE, FALSE, 'PT', 3, 1);
INSERT INTO tb_comment (content, created_at, is_visible, is_from_author, language, user_id, post_id) VALUES ('Thanks! I also love the philosophical side of it.', NOW(), TRUE, TRUE, 'EN', 1, 1);
INSERT INTO tb_comment (content, created_at, is_visible, is_from_author, language, user_id, post_id) VALUES ('This is nonsense!', NOW(), FALSE, FALSE, 'EN', 3, 2);
INSERT INTO tb_comment (content, created_at, is_visible, is_from_author, language, user_id, post_id) VALUES ('Muito interessante, uso IA no trabalho todo dia.', NOW(), TRUE, FALSE, 'PT', 2, 4);