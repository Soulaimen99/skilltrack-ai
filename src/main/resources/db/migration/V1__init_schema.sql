CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- USER TABLE
CREATE TABLE app_user
(
    id         UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    username   VARCHAR(255) UNIQUE NOT NULL,
    email      VARCHAR(255),
    created_at TIMESTAMP        DEFAULT CURRENT_TIMESTAMP
);

-- LEARNING LOG TABLE
CREATE TABLE learning_log
(
    id         UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id    UUID         NOT NULL REFERENCES app_user (id) ON DELETE CASCADE,
    username   VARCHAR(255) NOT NULL,
    content    TEXT,
    tags       VARCHAR(255),
    created_at TIMESTAMP        DEFAULT CURRENT_TIMESTAMP
);

-- SUMMARY TABLE (stores generated summaries)
CREATE TABLE summary
(
    id         UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id    UUID         NOT NULL REFERENCES app_user (id) ON DELETE CASCADE,
    username   VARCHAR(255) NOT NULL,
    content    TEXT         NOT NULL,
    created_at TIMESTAMP        DEFAULT CURRENT_TIMESTAMP
);

-- SUMMARY USAGE TABLE (rate limiting)
CREATE TABLE summary_usage
(
    id         UUID PRIMARY KEY      DEFAULT uuid_generate_v4(),
    user_id    UUID         NOT NULL REFERENCES app_user (id) ON DELETE CASCADE,
    username   VARCHAR(255) NOT NULL,
    usage_date DATE         NOT NULL,
    count      INTEGER      NOT NULL DEFAULT 0,
    UNIQUE (username, usage_date)
);
