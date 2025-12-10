INSERT INTO users (full_name, login, password_hash, salt, phone_number, role_id)
VALUES ('Петров Алексей Владимирович', 'petrov_av', 'hash1', '', '+79991112233', 2),
       ('Смирнов Владимир Петрович', 'smirnov_vp', 'hash2', '', '+79992223344', 2),
       ('Петров Алексей Иванович', 'petrov', 'be8892ab1d75b77d3c5dd316ddfe49691864619901fa6fea51f5d506351e21ce',
        '4dcd313da12445633edfe2c253a96f6fce0e5cf53a0501f9da6c77f019071f4f', '+79991112233', 1),
       ('Новиков Дмитрий Алексеевич', 'novikov', '66544ede7601f7af613c0bbde562d7406da84044bd3d706d5b357ac1d242679d',
        '9197f6453e0c70924fa3fa071c4ab37bcdcf0978841a8cbb33c7224827f0dd9c', '+79993334455', 2)