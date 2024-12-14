CREATE TABLE resources
(
    id           UUID PRIMARY KEY,
    name         VARCHAR(255) NOT NULL,
    type         VARCHAR(50)  NOT NULL,
    url          VARCHAR(255),
    license_type VARCHAR(50)  NOT NULL,
    format       VARCHAR(100),
    size         BIGINT,
    created_at   TIMESTAMP    NOT NULL,
    updated_at   TIMESTAMP    NOT NULL,
    created_by   VARCHAR(255),
    modified_by  VARCHAR(255),
    deleted      BOOLEAN      NOT NULL DEFAULT FALSE
);