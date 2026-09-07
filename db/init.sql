-- Очищаем, если таблица уже была (для чистого старта в учебных целях)
DROP TABLE IF EXISTS users CASCADE;

-- Создаём таблицу с автоинкрементом id — это решает проблему ошибки "Не указан id"
CREATE TABLE users (
                       id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                       name VARCHAR(100) NOT NULL,
                       email VARCHAR(255) NOT NULL UNIQUE
);
