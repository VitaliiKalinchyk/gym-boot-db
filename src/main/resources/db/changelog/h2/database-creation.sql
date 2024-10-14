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
