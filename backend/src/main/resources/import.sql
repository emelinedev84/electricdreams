INSERT INTO tb_user (username, first_name, last_name, email, password, bio, image_url) VALUES ('rafaelo', 'Rafaelo', 'Cat', 'rafaelo@gmail.com', '$2a$10$TfrNLXTqbr6S69Xeu4n19OkPQXcxu/ap3v9iHmJaRdEg.CL/qiUf2', 'I''m the cutest cat', 'rafaelo.jpg');

INSERT INTO tb_role (authority) VALUES ('ROLE_ADMIN');

INSERT INTO tb_user_role (user_id, role_id) VALUES (1, 1);

INSERT INTO tb_category (code, name_en, name_pt) VALUES ('books', 'Books', 'Livros');
INSERT INTO tb_category (code, name_en, name_pt) VALUES ('games', 'Games', 'Jogos');
INSERT INTO tb_category (code, name_en, name_pt) VALUES ('dystopia', 'Dystopia', 'Distopia');
INSERT INTO tb_category (code, name_en, name_pt) VALUES ('science-fiction', 'Science Fiction', 'Ficcao Cientifica');

INSERT INTO tb_tag (code, name_en, name_pt) VALUES ('fiction', 'Fiction', 'Ficcao');
INSERT INTO tb_tag (code, name_en, name_pt) VALUES ('technology', 'Technology', 'Tecnologia');
INSERT INTO tb_tag (code, name_en, name_pt) VALUES ('cyberpunk', 'Cyberpunk', 'Cyberpunk');

INSERT INTO tb_post (image_url, created_at, updated_at, user_id) VALUES ('https://picsum.photos/seed/p1/600/400', NOW(), NOW(), 1);
INSERT INTO tb_post (image_url, created_at, updated_at, user_id) VALUES ('https://picsum.photos/seed/p2/600/400', NOW(), NOW(), 1);
INSERT INTO tb_post (image_url, created_at, updated_at, user_id) VALUES ('https://picsum.photos/seed/p3/600/400', NOW(), NOW(), 1);
INSERT INTO tb_post (image_url, created_at, updated_at, user_id) VALUES ('https://picsum.photos/seed/p4/600/400', NOW(), NOW(), 1);
INSERT INTO tb_post (image_url, created_at, updated_at, user_id) VALUES ('https://picsum.photos/seed/p5/600/400', NOW(), NOW(), 1);

INSERT INTO tb_post_category (post_id, category_id) VALUES (1, 3);
INSERT INTO tb_post_category (post_id, category_id) VALUES (2, 3);
INSERT INTO tb_post_category (post_id, category_id) VALUES (3, 2);
INSERT INTO tb_post_category (post_id, category_id) VALUES (4, 1);
INSERT INTO tb_post_category (post_id, category_id) VALUES (5, 1);

INSERT INTO tb_post_tag (post_id, tag_id) VALUES (1, 1);
INSERT INTO tb_post_tag (post_id, tag_id) VALUES (1, 2);
INSERT INTO tb_post_tag (post_id, tag_id) VALUES (2, 1);
INSERT INTO tb_post_tag (post_id, tag_id) VALUES (2, 2);
INSERT INTO tb_post_tag (post_id, tag_id) VALUES (3, 1);
INSERT INTO tb_post_tag (post_id, tag_id) VALUES (4, 1);
INSERT INTO tb_post_tag (post_id, tag_id) VALUES (4, 2);
INSERT INTO tb_post_tag (post_id, tag_id) VALUES (5, 2);

INSERT INTO tb_content (language, url_handle, title, content, meta_description, status, post_id) VALUES ('EN', 'blade-runner-and-electric-dreams', 'Blade Runner and Electric Dreams', '<p>Exploring the dystopia of Blade Runner.</p>', 'Explore Blade Runner themes and the meaning of electric dreams.', 'PUBLISHED', 1);
INSERT INTO tb_content (language, url_handle, title, content, meta_description, status, post_id) VALUES ('PT', 'blade-runner-e-sonhos-eletricos', 'Blade Runner e Sonhos Eletricos', '<p>Explorando a distopia de Blade Runner.</p>', 'Explore os temas de Blade Runner e os sonhos eletricos.', 'PUBLISHED', 1);
INSERT INTO tb_content (language, url_handle, title, content, meta_description, status, post_id) VALUES ('EN', 'future-of-ai', 'The Future of AI', '<p>Discussing artificial intelligence and society.</p>', 'Discussing AI impact on society and daily life in the near future.', 'PUBLISHED', 2);
INSERT INTO tb_content (language, url_handle, title, content, meta_description, status, post_id) VALUES ('EN', 'digital-utopia', 'Digital Utopia', '<p>Can technology build a perfect world?</p>', 'Can technology build a perfect world or create new contradictions?', 'PUBLISHED', 3);
INSERT INTO tb_content (language, url_handle, title, content, meta_description, status, post_id) VALUES ('PT', 'utopia-digital', 'Utopia Digital', '<p>A tecnologia pode construir um mundo perfeito?</p>', 'A tecnologia pode criar um mundo perfeito ou novas contradicoes?', 'DRAFT', 3);
INSERT INTO tb_content (language, url_handle, title, content, meta_description, status, post_id) VALUES ('EN', 'artificial-intelligence-in-daily-life', 'Artificial Intelligence in Daily Life', '<p>How AI is present in our daily routines.</p>', 'Practical applicatins of AI in daily life and modern businesses.', 'PUBLISHED', 4);
INSERT INTO tb_content (language, url_handle, title, content, meta_description, status, post_id) VALUES ('PT', 'inteligencia-artificial-no-cotidiano', 'Inteligencia Artificial no Cotidiano', '<p>Como a IA esta presente nas nossas rotinas.</p>', 'Aplicacoes praticas de IA no dia a dia e nos negocios modernos.', 'PUBLISHED', 4);
INSERT INTO tb_content (language, url_handle, title, content, meta_description, status, post_id) VALUES ('EN', 'english-only-post', 'English Only Post', '<p>Testing English-only content.</p>', 'Short post to verify content available only in English language.', 'PUBLISHED', 5);
