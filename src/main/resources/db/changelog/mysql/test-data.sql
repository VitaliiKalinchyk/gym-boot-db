INSERT INTO `user` (first_name, last_name, username, password, is_active) VALUES
    ('Joe', 'Doe', 'Joe.Doe', 'password123', 1),
    ('Jane', 'Doe', 'Jane.Doe', 'password456', 1),
    ('Joe', 'Doe', 'Joe.Doe1', 'password123', 1),
    ('Jane', 'Doe', 'Jane.Doe1', 'password456', 0),
    ('Alex', 'Johnson', 'Alex.Johnson', 'password789', 0);

INSERT INTO trainee (birthday, address, user_id) VALUES
    ('1990-01-01', '123 Main St', (SELECT id FROM `user` WHERE username = 'Joe.Doe')),
    ('1995-05-15', '456 Oak Ave', (SELECT id FROM `user` WHERE username = 'Jane.Doe')),
    ('1988-10-30', '789 Pine Blvd', (SELECT id FROM `user` WHERE username = 'Alex.Johnson'));

INSERT INTO trainer (user_id, training_type_id) VALUES
    ((SELECT id FROM `user` WHERE username = 'Joe.Doe1'), 1),
    ((SELECT id FROM `user` WHERE username = 'Jane.Doe1'), 2);

INSERT INTO trainee_has_trainer (trainee_id, trainer_id) VALUES
    ((SELECT te.id FROM trainee te JOIN `user` u on u.id = te.user_id WHERE username = 'Joe.Doe'),
     (SELECT tr.id FROM trainer tr JOIN `user` u on u.id = tr.user_id WHERE username = 'Joe.Doe1')),
    ((SELECT te.id FROM trainee te JOIN `user` u on u.id = te.user_id WHERE username = 'Jane.Doe'),
     (SELECT tr.id FROM trainer tr JOIN `user` u on u.id = tr.user_id WHERE username = 'Jane.Doe1'));

INSERT INTO training (name, date, duration, trainer_id, trainee_id, training_type_id) VALUES
    ('First training', '2024-10-09', 45,
    (SELECT tr.id FROM trainer tr JOIN `user` u on u.id = tr.user_id WHERE username = 'Joe.Doe1'),
    (SELECT te.id FROM trainee te JOIN `user` u on u.id = te.user_id WHERE username = 'Joe.Doe'),
    (SELECT tr.training_type_id FROM trainer tr JOIN `user` u on u.id = tr.user_id WHERE username = 'Joe.Doe1')),
    ('Long training', '2024-10-12', 90,
     (SELECT tr.id FROM trainer tr JOIN `user` u on u.id = tr.user_id WHERE username = 'Jane.Doe1'),
     (SELECT te.id FROM trainee te JOIN `user` u on u.id = te.user_id WHERE username = 'Jane.Doe'),
     (SELECT tr.training_type_id FROM trainer tr JOIN `user` u on u.id = tr.user_id WHERE username = 'Jane.Doe1'));
