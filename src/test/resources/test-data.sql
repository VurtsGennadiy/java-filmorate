INSERT INTO users (email, login, name, birthday)
VALUES  ('tester1@practicum.ru', 'tester1', 'Иван Тестов 1', '1995-04-25'),
        ('tester2@practicum.ru', 'tester2', 'Иван Тестов 2', '1996-04-25'),
        ('tester3@practicum.ru', 'tester3', 'Иван Тестов 3', '1997-04-26');

INSERT INTO friendship (user_id, friend_id)
VALUES (1, 2);

INSERT INTO films (name, description, release, duration, mpa_id)
VALUES  ('Джависты', 'Начинающий джавист пытается протестировать jdbc репозиторий', '2025-04-26', 100, 3),
        ('Путь к IT', 'Очередной вкатун проходит курс программирования', '2025-04-26', 10000, 5);

INSERT INTO likes (film_id, user_id)
VALUES (1, 1);

INSERT INTO film_genre (film_id, genre_id)
VALUES  (2, 1),
        (2, 2),
        (1, 1);