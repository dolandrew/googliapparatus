create table song_entity (
  id              varchar(255) not null,
  name            varchar(255) not null,
  name_lower      varchar(255) not null,
  link            varchar(255) not null,
  lyrics          varchar(10485760),
  lyrics_by       varchar(255),
  primary key (id)
);
