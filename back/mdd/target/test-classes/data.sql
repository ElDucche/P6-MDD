-- Script d'initialisation des données de test pour H2
-- Ce script est exécuté automatiquement par Spring Boot pour les tests

-- Insertion de thèmes de test
INSERT INTO themes (id, title, description, created_at, updated_at) VALUES
(1, 'Java', 'Discussions sur le langage Java et ses frameworks', NOW(), NOW()),
(2, 'Angular', 'Frontend moderne avec Angular', NOW(), NOW()),
(3, 'Spring Boot', 'Développement backend avec Spring Boot', NOW(), NOW()),
(4, 'Base de données', 'SQL, NoSQL et optimisation des performances', NOW(), NOW());

-- Insertion d'utilisateurs de test
INSERT INTO users (id, email, username, password, created_at, updated_at) VALUES
(1, 'usertest@example.com', 'usertest', '$2a$10$0YJZt5qFZwqw5r6X8xDa0OmtZhY4qZL5K8xY6K8Y6K8Y6K8Y6K8Y6O', NOW(), NOW()),
(2, 'john.doe@example.com', 'johndoe', '$2a$10$0YJZt5qFZwqw5r6X8xDa0OmtZhY4qZL5K8xY6K8Y6K8Y6K8Y6K8Y6O', NOW(), NOW()),
(3, 'jane.smith@example.com', 'janesmith', '$2a$10$0YJZt5qFZwqw5r6X8xDa0OmtZhY4qZL5K8xY6K8Y6K8Y6K8Y6K8Y6O', NOW(), NOW());

-- Insertion d'abonnements de test
INSERT INTO subscriptions (user_id, theme_id, subscribed_at) VALUES
(1, 1, NOW()),
(1, 2, NOW()),
(2, 1, NOW()),
(2, 3, NOW()),
(3, 2, NOW()),
(3, 4, NOW());

-- Insertion de posts de test
INSERT INTO posts (id, title, content, author_id, theme_id, created_at, updated_at) VALUES
(1, 'Introduction à Spring Boot', 'Spring Boot simplifie le développement Java...', 1, 3, NOW(), NOW()),
(2, 'Les nouveautés d''Angular 17', 'Angular 17 apporte de nombreuses améliorations...', 2, 2, NOW(), NOW()),
(3, 'Optimisation des requêtes SQL', 'Quelques astuces pour optimiser vos requêtes...', 3, 4, NOW(), NOW());

-- Insertion de commentaires de test
INSERT INTO comments (id, content, author_id, post_id, created_at, updated_at) VALUES
(1, 'Excellent article ! Merci pour le partage.', 2, 1, NOW(), NOW()),
(2, 'J''ai une question sur l''injection de dépendances...', 3, 1, NOW(), NOW()),
(3, 'Très utile, j''ai appris beaucoup de choses.', 1, 2, NOW(), NOW());
