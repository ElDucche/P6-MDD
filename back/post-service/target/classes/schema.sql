CREATE TABLE IF NOT EXISTS USERS (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS THEMES (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS POSTS (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    author_id BIGINT,
    theme_id BIGINT,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (author_id) REFERENCES USERS(id),
    FOREIGN KEY (theme_id) REFERENCES THEMES(id)
);

CREATE TABLE IF NOT EXISTS COMMENTS (
    id BIGSERIAL PRIMARY KEY,
    content TEXT NOT NULL,
    author_id BIGINT,
    post_id BIGINT,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (author_id) REFERENCES USERS(id),
    FOREIGN KEY (post_id) REFERENCES POSTS(id)
);

CREATE TABLE IF NOT EXISTS SUBSCRIPTIONS (
    user_id BIGINT,
    theme_id BIGINT,
    PRIMARY KEY (user_id, theme_id),
    FOREIGN KEY (user_id) REFERENCES USERS(id),
    FOREIGN KEY (theme_id) REFERENCES THEMES(id)
);

-- Insertion des thèmes IT/Développement
INSERT INTO THEMES (title, description, created_at, updated_at) VALUES 
('Développement Web Frontend', 'Technologies et frameworks frontend : React, Angular, Vue.js, HTML5, CSS3, JavaScript', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Développement Web Backend', 'APIs, microservices, bases de données : Spring Boot, Node.js, Python, Java, REST/GraphQL', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Intelligence Artificielle & ML', 'Machine Learning, Deep Learning, IA générative, algorithmes et modèles prédictifs', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('DevOps & Infrastructure', 'CI/CD, Docker, Kubernetes, cloud computing, automatisation et monitoring', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Développement Mobile', 'Applications natives et cross-platform : React Native, Flutter, iOS, Android', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Data Science & Analytics', 'Analyse de données, visualisation, Big Data, Python, R, SQL avancé', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Cybersécurité', 'Sécurité des applications, ethical hacking, cryptographie, protection des données', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Cloud Computing', 'AWS, Azure, GCP, architecture cloud, serverless, solutions distribuées', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Architecture Logicielle', 'Design patterns, microservices, architecture hexagonale, DDD, clean code', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('UI/UX Design', 'Expérience utilisateur, interface design, prototypage, accessibilité web', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Blockchain & Web3', 'Cryptomonnaies, smart contracts, DeFi, NFTs, technologies décentralisées', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Internet des Objets (IoT)', 'Objets connectés, capteurs, protocoles IoT, edge computing, automatisation', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE 
    title = VALUES(title),
    description = VALUES(description),
    updated_at = CURRENT_TIMESTAMP;
