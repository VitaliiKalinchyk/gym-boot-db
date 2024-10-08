CREATE TABLE training_type (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(45) NOT NULL UNIQUE
);

CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(45) NOT NULL,
    last_name VARCHAR(45) NOT NULL,
    username VARCHAR(100) NOT NULL,
    password VARCHAR(100) NOT NULL,
    is_active TINYINT NOT NULL
);

CREATE UNIQUE INDEX idx_username ON users (username);

CREATE TABLE role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(40) NOT NULL UNIQUE
);

CREATE TABLE users_has_role (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (role_id) REFERENCES role(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE trainee (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    birthday DATE,
    address VARCHAR(120),
    user_id BIGINT NOT NULL UNIQUE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE trainer (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL UNIQUE,
    training_type_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (training_type_id) REFERENCES training_type(id)
);

CREATE TABLE training (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(45) NOT NULL,
    date DATE NOT NULL,
    duration INT NOT NULL,
    trainer_id BIGINT NOT NULL,
    trainee_id BIGINT NOT NULL,
    training_type_id BIGINT NOT NULL,
    FOREIGN KEY (trainer_id) REFERENCES trainer(id),
    FOREIGN KEY (trainee_id) REFERENCES trainee(id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (training_type_id) REFERENCES training_type(id)
);

CREATE TABLE trainee_has_trainer (
    trainee_id BIGINT NOT NULL,
    trainer_id BIGINT NOT NULL,
    PRIMARY KEY (trainee_id, trainer_id),
    FOREIGN KEY (trainee_id) REFERENCES trainee(id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (trainer_id) REFERENCES trainer(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE jwt_blacklist (
    token VARCHAR(512) PRIMARY KEY ,
    expiration_time TIMESTAMP NOT NULL
);

INSERT INTO training_type (name)
VALUES ('FITNESS'), ('YOGA'), ('ZUMBA');

INSERT INTO role (name)
VALUES ('ROLE_TRAINER'), ('ROLE_TRAINEE'), ('ROLE_ADMIN');

INSERT INTO users (first_name, last_name, username, password, is_active) VALUES
    ('Joe', 'Doe', 'Joe.Doe', 'password123', 1),
    ('Jane', 'Doe', 'Jane.Doe', 'password456', 1),
    ('Joe', 'Doe', 'Joe.Doe1', 'password123', 1),
    ('Jane', 'Doe', 'Jane.Doe1', 'password456', 0),
    ('Alex', 'Johnson', 'Alex.Johnson', 'password789', 0);

INSERT INTO users_has_role (user_id, role_id) VALUES
    ((SELECT id FROM users WHERE username = 'Joe.Doe'), (SELECT id FROM role WHERE name = 'ROLE_TRAINEE')),
    ((SELECT id FROM users WHERE username = 'Jane.Doe'), (SELECT id FROM role WHERE name = 'ROLE_TRAINEE')),
    ((SELECT id FROM users WHERE username = 'Alex.Johnson'), (SELECT id FROM role WHERE name = 'ROLE_TRAINEE')),
    ((SELECT id FROM users WHERE username = 'Joe.Doe1'), (SELECT id FROM role WHERE name = 'ROLE_TRAINER')),
    ((SELECT id FROM users WHERE username = 'Jane.Doe1'), (SELECT id FROM role WHERE name = 'ROLE_TRAINER'));

INSERT INTO trainee (birthday, address, user_id) VALUES
    ('1990-01-01', '123 Main St', (SELECT id FROM users WHERE username = 'Joe.Doe')),
    ('1995-05-15', '456 Oak Ave', (SELECT id FROM users WHERE username = 'Jane.Doe')),
    ('1988-10-30', '789 Pine Blvd', (SELECT id FROM users WHERE username = 'Alex.Johnson'));

INSERT INTO trainer (user_id, training_type_id) VALUES
    ((SELECT id FROM users WHERE username = 'Joe.Doe1'), 1),
    ((SELECT id FROM users WHERE username = 'Jane.Doe1'), 2);

INSERT INTO trainee_has_trainer (trainee_id, trainer_id) VALUES
    ((SELECT te.id FROM trainee te JOIN users u on u.id = te.user_id WHERE username = 'Joe.Doe'),
     (SELECT tr.id FROM trainer tr JOIN users u on u.id = tr.user_id WHERE username = 'Joe.Doe1')),
    ((SELECT te.id FROM trainee te JOIN users u on u.id = te.user_id WHERE username = 'Jane.Doe'),
     (SELECT tr.id FROM trainer tr JOIN users u on u.id = tr.user_id WHERE username = 'Jane.Doe1'));

INSERT INTO training (name, date, duration, trainer_id, trainee_id, training_type_id) VALUES
    ('First training', '2024-10-09', 45,
     (SELECT tr.id FROM trainer tr JOIN users u on u.id = tr.user_id WHERE username = 'Joe.Doe1'),
     (SELECT te.id FROM trainee te JOIN users u on u.id = te.user_id WHERE username = 'Joe.Doe'),
     (SELECT tr.training_type_id FROM trainer tr JOIN users u on u.id = tr.user_id WHERE username = 'Joe.Doe1')),
    ('Long training', '2024-10-12', 90,
     (SELECT tr.id FROM trainer tr JOIN users u on u.id = tr.user_id WHERE username = 'Jane.Doe1'),
     (SELECT te.id FROM trainee te JOIN users u on u.id = te.user_id WHERE username = 'Jane.Doe'),
     (SELECT tr.training_type_id FROM trainer tr JOIN users u on u.id = tr.user_id WHERE username = 'Jane.Doe1'));
