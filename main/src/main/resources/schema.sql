DROP TABLE IF EXISTS comments, compilation_events, compilations, participation_requests, events, locations, categories, users CASCADE;

CREATE TABLE users(
    id BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(512) NOT NULL UNIQUE,
    CONSTRAINT user_pk PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS categories(
    id BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    name VARCHAR(50) NOT NULL UNIQUE,
    CONSTRAINT categories_pk PRIMARY KEY (id)
);

CREATE TABLE locations(
    id BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    lat FLOAT NOT NULL,
    lon FLOAT NOT NULL,
    CONSTRAINT locations_pk PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS events(
    annotation VARCHAR(2000) NOT NULL,
    category_id BIGINT NOT NULL,
    confirmed_requests INTEGER,
    creation_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    description VARCHAR(7000),
    event_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    id BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    initiator_id BIGINT NOT NULL,
    location_id BIGINT NOT NULL,
    is_paid BOOLEAN NOT NULL,
    participants_limit INTEGER,
    published_on TIMESTAMP WITHOUT TIME ZONE,
    request_moderation BOOLEAN,
    state VARCHAR(10),
    title VARCHAR(120) NOT NULL,
    CONSTRAINT events_pk PRIMARY KEY (id),
    FOREIGN KEY (category_id) REFERENCES categories (id) ON DELETE RESTRICT,
    FOREIGN KEY (initiator_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (location_id) REFERENCES locations (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS participation_requests(
    id BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    created TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    event_id BIGINT NOT NULL,
    requester_id BIGINT NOT NULL,
    status VARCHAR(50),
    CONSTRAINT participation_request_pk PRIMARY KEY (id),
    FOREIGN KEY (event_id) REFERENCES events (id) ON DELETE CASCADE,
    FOREIGN KEY (requester_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS compilations(
    id BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    pinned BOOLEAN NOT NULL,
    title VARCHAR(50) NOT NULL UNIQUE,
    CONSTRAINT compilations_pk PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS compilation_events(
    id BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    compilation_id BIGINT NOT NULL,
    event_id BIGINT NOT NULL,
    CONSTRAINT compilation_events_pk PRIMARY KEY (id),
    FOREIGN KEY (compilation_id) REFERENCES compilations (id) ON DELETE CASCADE,
    FOREIGN KEY (event_id) REFERENCES events (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS comments(
    id BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    text VARCHAR(2000) NOT NULL,
    author_id BIGINT NOT NULL,
    created TIMESTAMP NOT NULL,
    event_id BIGINT NOT NULL,
    CONSTRAINT comments_pk PRIMARY KEY (id),
    FOREIGN KEY (author_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (event_id) REFERENCES events (id) ON DELETE CASCADE
);