CREATE TABLE IF NOT EXISTS genres (
    genre_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(30) NOT NULL,
    CONSTRAINT uq_genre_name UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS mpa (
    mpa_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(5),
    CONSTRAINT uq_mpa_name UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS films (
    film_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(200),
    release DATE,
    duration INT,
    mpa_id INT,
    FOREIGN KEY (mpa_id) REFERENCES mpa(mpa_id)
);

CREATE TABLE IF NOT EXISTS users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(255) NOT NULL,
    login VARCHAR(30) NOT NULL,
    name VARCHAR(255),
    birthday DATE,
    CONSTRAINT uq_user_email UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS friendship (
    user_id INT NOT NULL,
    friend_id INT NOT NULL,
    PRIMARY KEY (user_id, friend_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (friend_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS film_genre (
    film_id INT NOT NULL,
    genre_id INT NOT NULL,
    PRIMARY KEY (film_id, genre_id),
    FOREIGN KEY (film_id) REFERENCES films(film_id) ON DELETE CASCADE,
    FOREIGN KEY (genre_id) REFERENCES genres(genre_id)
);

CREATE TABLE IF NOT EXISTS likes (
    film_id INT NOT NULL,
    user_id INT NOT NULL,
    PRIMARY KEY (film_id, user_id),
    FOREIGN KEY (film_id) REFERENCES films(film_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- ==========================================
-- Таблица отзывов
-- ==========================================
-- Хранит информацию об отзывах пользователей на фильмы:
--   - id отзыва
--   - содержание отзыва
--   - характеристика отзыва (положительный/негативный)
--   - id пользователя
--   - id фильма
-- ------------------------------------------
CREATE TABLE IF NOT EXISTS reviews (
    reviews_id INT PRIMARY KEY AUTO_INCREMENT,
    content VARCHAR(500) NOT NULL,
    is_Positive BOOLEAN NOT NULL,
    user_id INT NOT NULL,
    film_id INT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (film_id) REFERENCES films(film_id) ON DELETE CASCADE
);

-- ==========================================
-- Оценки пользователей на отзыв
-- ==========================================
-- Хранит информацию об оценке(лайк или дизлайк) на отзыв:
--   - id отзыва
--   - id пользователя
--   - оценка (1 если лайк и -1 для дизлайка)
-- ------------------------------------------
CREATE TABLE IF NOT EXISTS likes_reviews (
    reviews_id INT NOT NULL,
    user_id INT NOT NULL,
    grade INT NOT NULL,
    PRIMARY KEY(reviews_id, user_id),
    FOREIGN KEY (reviews_id) REFERENCES reviews(reviews_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);