CREATE TABLE IF NOT EXISTS song_entity (
  id              VARCHAR(255) NOT NULL,
  name            VARCHAR(255) NOT NULL,
  name_lower      VARCHAR(255) NOT NULL,
  link            VARCHAR(255) NOT NULL,
  lyrics          varchar(max),
  lyrics_by       VARCHAR(255),
  PRIMARY KEY (id)
);
