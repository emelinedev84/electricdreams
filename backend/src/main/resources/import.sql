INSERT INTO tb_user (username, first_name, last_name, email, password) VALUES ('rafaelo', 'Rafaelo', 'Cat', 'rafaelo@gmail.com', '$2a$10$uoFp5t1WZGXhT78R.ctmkeAlwSenUA5a95M3zoo8EKbEOoq0axc8S');
INSERT INTO tb_user (username, first_name, last_name, email, password) VALUES ('fernando', 'Fernando', 'Cat', 'fernando@gmail.com', '$2a$10$uoFp5t1WZGXhT78R.ctmkeAlwSenUA5a95M3zoo8EKbEOoq0axc8S');
INSERT INTO tb_user (username, first_name, last_name, email, password) VALUES ('kaka', 'Kaka', 'Dog', 'kaka@gmail.com', '$2a$10$uoFp5t1WZGXhT78R.ctmkeAlwSenUA5a95M3zoo8EKbEOoq0axc8S');

INSERT INTO tb_role (authority) VALUES ('ROLE_WRITER');
INSERT INTO tb_role (authority) VALUES ('ROLE_ADMIN');

INSERT INTO tb_user_role (user_id, role_id) VALUES (1, 2);
INSERT INTO tb_user_role (user_id, role_id) VALUES (1, 1);
INSERT INTO tb_user_role (user_id, role_id) VALUES (2, 2);
INSERT INTO tb_user_role (user_id, role_id) VALUES (3, 1);

INSERT INTO tb_category (name, language) VALUES ('Books', 'EN');
INSERT INTO tb_category (name, language) VALUES ('Livros', 'PT');
INSERT INTO tb_category (name, language) VALUES ('Games', 'EN');
INSERT INTO tb_category (name, language) VALUES ('Jogos', 'PT');
INSERT INTO tb_category (name, language) VALUES ('Dystopia', 'EN');
INSERT INTO tb_category (name, language) VALUES ('Distopia', 'PT');
INSERT INTO tb_category (name, language) VALUES ('Science Fiction', 'EN');
INSERT INTO tb_category (name, language) VALUES ('Ficção Científica', 'PT');

INSERT INTO tb_tag (name, language) VALUES ('Fiction', 'EN');
INSERT INTO tb_tag (name, language) VALUES ('Ficção', 'PT');
INSERT INTO tb_tag (name, language) VALUES ('Technology', 'EN');
INSERT INTO tb_tag (name, language) VALUES ('Tecnologia', 'PT');
INSERT INTO tb_tag (name, language) VALUES ('Cyberpunk', 'EN');
INSERT INTO tb_tag (name, language) VALUES ('Cyberpunk', 'PT');

INSERT INTO tb_post (image_url, created_at, updated_at, user_id) VALUES ('https://picsum.photos/seed/p1/600/400', NOW(), NOW(), 1);
INSERT INTO tb_post (image_url, created_at, updated_at, user_id) VALUES ('https://picsum.photos/seed/p2/600/400', NOW(), NOW(), 1);
INSERT INTO tb_post (image_url, created_at, updated_at, user_id) VALUES ('https://picsum.photos/seed/p3/600/400', NOW(), NOW(), 1);
INSERT INTO tb_post (image_url, created_at, updated_at, user_id) VALUES ('https://picsum.photos/seed/p4/600/400', NOW(), NOW(), 1);
INSERT INTO tb_post (image_url, created_at, updated_at, user_id) VALUES ('https://picsum.photos/seed/p5/600/400', NOW(), NOW(), 2);

INSERT INTO tb_content (language, url_handle, title, content, meta_description, is_draft, post_id) VALUES ('EN', 'blade-runner-and-electric-dreams', 'Blade Runner and Electric Dreams', '<p>Exploring the dystopia of Blade Runner.</p>', 'Explore Blade Runner themes', FALSE, 1);
INSERT INTO tb_content (language, url_handle, title, content, meta_description, is_draft, post_id) VALUES ('PT', 'blade-runner-e-sonhos-eletricos', 'Blade Runner e Sonhos Elétricos', '<p>Explorando a distopia de Blade Runner.</p>', 'Explore os temas de Blade Runner', FALSE, 1);
INSERT INTO tb_content (language, url_handle, title, content, meta_description, is_draft, post_id) VALUES ('EN', 'future-of-ai', 'The Future of AI', '<p>Discussing artificial intelligence and society.</p>', 'AI and its impact', FALSE, 2);
INSERT INTO tb_content (language, url_handle, title, content, meta_description, is_draft, post_id) VALUES ('EN', 'digital-utopia', 'Digital Utopia', '<p>Can technology build a perfect world?</p>', 'Tech-driven utopias and their paradoxes', FALSE, 3);
INSERT INTO tb_content (language, url_handle, title, content, meta_description, is_draft, post_id) VALUES ('PT', 'utopia-digital', 'Utopia Digital', '<p>A tecnologia pode construir um mundo perfeito?</p>', 'Utopias tecnológicas e seus paradoxos', TRUE, 3);
INSERT INTO tb_content (language, url_handle, title, content, meta_description, is_draft, post_id) VALUES ('PT', 'inteligencia-artificial-no-cotidiano', 'Inteligência Artificial no Cotidiano', '<p>Como a IA já está presente nas nossas rotinas.</p>', 'Aplicações práticas da IA no dia a dia', FALSE, 4);
INSERT INTO tb_content (language, url_handle, title, content, meta_description, is_draft, post_id) VALUES ('EN', 'english-only-post', 'English Only Post', '<p>Testing English-only content.</p>', 'English only', FALSE, 5);
        
INSERT INTO tb_content_category (content_id, category_id) VALUES (1, 5);
INSERT INTO tb_content_category (content_id, category_id) VALUES (2, 6);
INSERT INTO tb_content_category (content_id, category_id) VALUES (3, 3);
INSERT INTO tb_content_category (content_id, category_id) VALUES (4, 1);
INSERT INTO tb_content_category (content_id, category_id) VALUES (5, 2);
INSERT INTO tb_content_category (content_id, category_id) VALUES (6, 4);
INSERT INTO tb_content_category (content_id, category_id) VALUES (7, 1);

INSERT INTO tb_content_tag (content_id, tag_id) VALUES (1, 1);
INSERT INTO tb_content_tag (content_id, tag_id) VALUES (2, 2);
INSERT INTO tb_content_tag (content_id, tag_id) VALUES (3, 3);
INSERT INTO tb_content_tag (content_id, tag_id) VALUES (4, 1);
INSERT INTO tb_content_tag (content_id, tag_id) VALUES (5, 2);
INSERT INTO tb_content_tag (content_id, tag_id) VALUES (6, 4);
INSERT INTO tb_content_tag (content_id, tag_id) VALUES (1, 3); 
INSERT INTO tb_content_tag (content_id, tag_id) VALUES (2, 4); 
INSERT INTO tb_content_tag (content_id, tag_id) VALUES (7, 3);