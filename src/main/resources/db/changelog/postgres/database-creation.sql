CREATE TABLE training_type (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name VARCHAR(45) NOT NULL UNIQUE
);

CREATE TABLE users (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    first_name VARCHAR(45) NOT NULL,
    last_name VARCHAR(45) NOT NULL,
    username VARCHAR(100) NOT NULL,
    password VARCHAR(100) NOT NULL,
    is_active BOOLEAN NOT NULL
);

CREATE UNIQUE INDEX idx_username ON users (username);

CREATE TABLE trainee (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    birthday DATE,
    address VARCHAR(120),
    user_id BIGINT NOT NULL UNIQUE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE trainer (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    user_id BIGINT NOT NULL UNIQUE,
    training_type_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (training_type_id) REFERENCES training_type(id)
);

CREATE TABLE training (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
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

INSERT INTO training_type (name)
VALUES ('FITNESS'), ('YOGA'), ('ZUMBA'), ('STRETCHING'), ('RESISTANCE');
