ALTER TABLE meeting_schedules
    DROP CONSTRAINT IF EXISTS meeting_schedules_club_id_key;

ALTER TABLE meeting_schedules
    ADD COLUMN name VARCHAR(120);

UPDATE meeting_schedules
SET name = '정기 모임'
WHERE name IS NULL;

ALTER TABLE meeting_schedules
    ALTER COLUMN name SET NOT NULL;

ALTER TABLE meetings
    ADD COLUMN schedule_id BIGINT REFERENCES meeting_schedules(id) ON DELETE SET NULL;
