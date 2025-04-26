-- Remove redundant username columns

ALTER TABLE learning_log
    DROP COLUMN username;

ALTER TABLE summary
    DROP COLUMN username;

ALTER TABLE summary_usage
    DROP COLUMN username;
