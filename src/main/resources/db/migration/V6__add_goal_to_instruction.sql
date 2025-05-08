-- Add goal_id to INSTRUCTION TABLE
ALTER TABLE instruction
    ADD COLUMN goal_id UUID REFERENCES learning_goal (id) ON DELETE SET NULL;