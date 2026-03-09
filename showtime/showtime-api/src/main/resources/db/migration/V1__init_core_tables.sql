CREATE TABLE members (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    height_cm INTEGER NOT NULL CHECK (height_cm BETWEEN 130 AND 240),
    skill_level VARCHAR(20) NOT NULL,
    position VARCHAR(20) NOT NULL,
    style VARCHAR(20) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE attendances (
    id BIGSERIAL PRIMARY KEY,
    attendance_date DATE NOT NULL,
    member_id UUID NOT NULL REFERENCES members(id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (attendance_date, member_id)
);

CREATE INDEX idx_attendances_date ON attendances(attendance_date);

CREATE TABLE match_results (
    id UUID PRIMARY KEY,
    played_at DATE NOT NULL,
    memo TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_match_results_played_at ON match_results(played_at DESC);

CREATE TABLE match_team_scores (
    id BIGSERIAL PRIMARY KEY,
    match_id UUID NOT NULL REFERENCES match_results(id) ON DELETE CASCADE,
    team_name VARCHAR(50) NOT NULL,
    score INTEGER NOT NULL CHECK (score >= 0)
);

CREATE TABLE match_videos (
    id UUID PRIMARY KEY,
    match_id UUID NOT NULL REFERENCES match_results(id) ON DELETE CASCADE,
    file_name VARCHAR(255) NOT NULL,
    content_type VARCHAR(150) NOT NULL,
    size_bytes BIGINT NOT NULL,
    file_path VARCHAR(1000) NOT NULL,
    uploaded_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_match_videos_match_id ON match_videos(match_id);
