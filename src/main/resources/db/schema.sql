CREATE TABLE IF NOT EXISTS users
(
	id       BIGSERIAL PRIMARY KEY,
	username VARCHAR(255) UNIQUE NOT NULL,
	password VARCHAR(255)        NOT NULL,
	enabled  BOOLEAN             NOT NULL DEFAULT true
);

CREATE TABLE IF NOT EXISTS user_roles
(
	id      BIGSERIAL PRIMARY KEY,
	user_id BIGINT       NOT NULL REFERENCES users ( id ),
	role    VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS learning_log
(
	id      BIGSERIAL PRIMARY KEY,
	content TEXT,
	tags    TEXT,
	date    DATE,
	user_id BIGINT REFERENCES users ( id )
);