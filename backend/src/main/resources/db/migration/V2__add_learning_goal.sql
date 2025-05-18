-- LEARNING GOAL TABLE
CREATE TABLE learning_goal
(
    id          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id     UUID         NOT NULL REFERENCES app_user (id) ON DELETE CASCADE,
    username    VARCHAR(255) NOT NULL,
    title       VARCHAR(255) NOT NULL,
    description TEXT,
    created_at  TIMESTAMP        DEFAULT CURRENT_TIMESTAMP
);

-- Add goal_id to LEARNING LOG TABLE
ALTER TABLE learning_log
    ADD COLUMN goal_id UUID REFERENCES learning_goal (id) ON DELETE SET NULL;
