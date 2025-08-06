CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

-- Table des thèmes (correspondant au modèle Java)
CREATE TABLE IF NOT EXISTS themes (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS posts (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    author_id BIGINT,
    theme_id BIGINT,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (author_id) REFERENCES users(id),
    FOREIGN KEY (theme_id) REFERENCES themes(id)
);

CREATE TABLE IF NOT EXISTS comments (
    id BIGSERIAL PRIMARY KEY,
    content TEXT NOT NULL,
    author_id BIGINT,
    post_id BIGINT,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (author_id) REFERENCES users(id),
    FOREIGN KEY (post_id) REFERENCES posts(id)
);

CREATE TABLE IF NOT EXISTS subscriptions (
    user_id BIGINT,
    theme_id BIGINT,
    PRIMARY KEY (user_id, theme_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (theme_id) REFERENCES themes(id)
);

CREATE TABLE IF NOT EXISTS notifications (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    message VARCHAR(255) NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Insertion des thèmes IT/Développement (évite les doublons grâce à ON CONFLICT)
INSERT INTO themes (title, description) VALUES 
('Java', 'Discussions autour du langage Java et ses frameworks'),
('Spring Boot', 'Développement d''applications avec Spring Boot'),
('Angular', 'Framework JavaScript pour applications web'),
('React', 'Bibliothèque JavaScript pour interfaces utilisateur'),
('Python', 'Langage de programmation Python et ses applications'),
('DevOps', 'Pratiques DevOps, CI/CD et automatisation'),
('Base de données', 'Gestion de données, SQL, NoSQL'),
('Sécurité', 'Cybersécurité et bonnes pratiques'),
('Intelligence Artificielle', 'IA, Machine Learning, Deep Learning'),
('Cloud Computing', 'Services cloud AWS, Azure, GCP'),
('Microservices', 'Architecture en microservices'),
('Développement Mobile', 'Applications iOS, Android, React Native')
ON CONFLICT (title) DO NOTHING;
