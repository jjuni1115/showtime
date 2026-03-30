ALTER TABLE meeting_schedules
    ADD COLUMN place VARCHAR(255);

ALTER TABLE meetings
    ADD COLUMN place VARCHAR(255);
