-- INSTRUCTION TABLE
CREATE TABLE instruction
(
    id         UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id    UUID         NOT NULL REFERENCES app_user (id) ON DELETE CASCADE,
    advice     TEXT         NOT NULL,
    created_at TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);
