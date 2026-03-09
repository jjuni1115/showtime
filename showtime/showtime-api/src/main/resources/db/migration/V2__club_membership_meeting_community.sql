CREATE TABLE app_users (
    id UUID PRIMARY KEY,
    provider VARCHAR(30) NOT NULL,
    provider_subject VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    display_name VARCHAR(120) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (provider, provider_subject)
);

CREATE TABLE clubs (
    id UUID PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    home_court VARCHAR(255) NOT NULL,
    image_url VARCHAR(1000),
    owner_user_id UUID NOT NULL REFERENCES app_users(id),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE club_memberships (
    id BIGSERIAL PRIMARY KEY,
    club_id UUID NOT NULL REFERENCES clubs(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES app_users(id) ON DELETE CASCADE,
    role VARCHAR(20) NOT NULL,
    joined_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (club_id, user_id)
);

CREATE INDEX idx_club_memberships_user_id ON club_memberships(user_id);

CREATE TABLE club_invites (
    id UUID PRIMARY KEY,
    club_id UUID NOT NULL REFERENCES clubs(id) ON DELETE CASCADE,
    code VARCHAR(40) NOT NULL UNIQUE,
    created_by_user_id UUID NOT NULL REFERENCES app_users(id),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    expires_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE meeting_schedules (
    id BIGSERIAL PRIMARY KEY,
    club_id UUID NOT NULL UNIQUE REFERENCES clubs(id) ON DELETE CASCADE,
    day_of_week VARCHAR(20) NOT NULL,
    start_time TIME NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    updated_by_user_id UUID NOT NULL REFERENCES app_users(id),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE meetings (
    id UUID PRIMARY KEY,
    club_id UUID NOT NULL REFERENCES clubs(id) ON DELETE CASCADE,
    meeting_date DATE NOT NULL,
    start_time TIME NOT NULL,
    note TEXT,
    created_by_user_id UUID NOT NULL REFERENCES app_users(id),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_meetings_club_date ON meetings(club_id, meeting_date DESC);

CREATE TABLE meeting_attendances (
    id BIGSERIAL PRIMARY KEY,
    meeting_id UUID NOT NULL REFERENCES meetings(id) ON DELETE CASCADE,
    user_id UUID REFERENCES app_users(id) ON DELETE SET NULL,
    guest_name VARCHAR(120),
    status VARCHAR(20) NOT NULL,
    source VARCHAR(20) NOT NULL,
    updated_by_user_id UUID NOT NULL REFERENCES app_users(id),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CHECK ((user_id IS NOT NULL AND guest_name IS NULL) OR (user_id IS NULL AND guest_name IS NOT NULL))
);

CREATE UNIQUE INDEX uq_meeting_user_attendance ON meeting_attendances(meeting_id, user_id) WHERE user_id IS NOT NULL;
CREATE UNIQUE INDEX uq_meeting_guest_attendance ON meeting_attendances(meeting_id, guest_name) WHERE guest_name IS NOT NULL;

CREATE TABLE club_posts (
    id UUID PRIMARY KEY,
    club_id UUID NOT NULL REFERENCES clubs(id) ON DELETE CASCADE,
    author_user_id UUID NOT NULL REFERENCES app_users(id),
    content TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_club_posts_club_created_at ON club_posts(club_id, created_at DESC);
