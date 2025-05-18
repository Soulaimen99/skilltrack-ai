-- QUIZ TABLE
CREATE TABLE quiz
(
    id         UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id    UUID NOT NULL REFERENCES app_user (id) ON DELETE CASCADE,
    goal_id    UUID NOT NULL REFERENCES learning_goal (id) ON DELETE CASCADE,
    score      INT,
    duration   INT,
    feedback   TEXT,
    completed  BOOLEAN,
    started_at TIMESTAMP        DEFAULT CURRENT_TIMESTAMP,
    ended_at   TIMESTAMP        DEFAULT CURRENT_TIMESTAMP
);

-- QUIZ QUESTION TABLE
CREATE TABLE quiz_question
(
    id             UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    quiz_id        UUID NOT NULL REFERENCES quiz (id) ON DELETE CASCADE,
    type           TEXT,
    question       TEXT,
    options        TEXT,
    correct_answer TEXT,
    score          INT,
    duration       INT,
    created_at     TIMESTAMP        DEFAULT CURRENT_TIMESTAMP
);

-- QUIZ ANSWER TABLE
CREATE TABLE quiz_answer
(
    id               UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    quiz_question_id UUID NOT NULL REFERENCES quiz_question (id) ON DELETE CASCADE,
    answer           TEXT,
    score            INT,
    correct          BOOLEAN,
    attempted_at     TIMESTAMP        DEFAULT CURRENT_TIMESTAMP
);