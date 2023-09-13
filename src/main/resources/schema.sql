 -- Для тестов
drop table IF EXISTS users, items, bookings, comments, requests
;

-- Таблица с пользователями
create TABLE IF NOT EXISTS users
(
    id         BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name       VARCHAR(255)                            NOT NULL,
    email      VARCHAR(512)                            NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (id),
    CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

create TABLE IF NOT EXISTS items
(
  id          BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  name        VARCHAR(255)                            NOT NULL,
  description VARCHAR(255)                            NOT NULL,
  available   BOOLEAN,
  owner       BIGINT,
  requestId   BIGINT,
  CONSTRAINT  pk_item PRIMARY KEY (id)
);

create TABLE IF NOT EXISTS bookings
(
  id            BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  start_booking TIMESTAMP,
  end_booking   TIMESTAMP,
  status        VARCHAR(100),
  booker_id     BIGINT                                  NOT NULL,
  item_id       BIGINT                                  NOT NULL,
  item_name     VARCHAR(200),
  item_owner    BIGINT                                  NOT NULL,
  CONSTRAINT    pk_booking PRIMARY KEY (id)
);

create TABLE IF NOT EXISTS comments
(
  id         BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  text       VARCHAR(500),
  author     VARCHAR(100),
  created    TIMESTAMP                               NOT NULL,
  item_id    BIGINT                                  NOT NULL,
  CONSTRAINT pk_comment PRIMARY KEY (id)
);

create TABLE IF NOT EXISTS requests
(
  id           BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  description  VARCHAR(500),
  created      TIMESTAMP,
  requester_id BIGINT                                  NOT NULL,
  CONSTRAINT   pk_request PRIMARY KEY (id)
);


