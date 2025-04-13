-- V1__init_schema.sql
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- USER TABLE
CREATE TABLE app_user
(
    id       UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    username VARCHAR(255) UNIQUE NOT NULL,
    email    VARCHAR(255)
);

-- LEARNING LOG TABLE
CREATE TABLE learning_log
(
    id      UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES app_user (id) ON DELETE CASCADE,
    content TEXT,
    tags    VARCHAR(255),
    date    TIMESTAMP        DEFAULT CURRENT_TIMESTAMP
);
