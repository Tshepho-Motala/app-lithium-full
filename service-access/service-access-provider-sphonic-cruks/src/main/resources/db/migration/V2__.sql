ALTER TABLE failed_attempt
    DROP COLUMN last_failure_stacktrace,
    ADD COLUMN first_failed_attempt datetime DEFAULT NULL,
    ADD COLUMN first_failed_message varchar(255) NOT NULL,
    ALGORITHM INPLACE,
    LOCK NONE;
