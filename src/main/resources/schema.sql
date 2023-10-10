
 CREATE TABLE IF NOT EXISTS "friends" (
  "following_user_id" integer,
  "followed_user_id" integer,
  "confirmed" integer,
  PRIMARY KEY ("following_user_id", "followed_user_id")
);

 CREATE TABLE IF NOT EXISTS "users" (
  "id" integer PRIMARY KEY,
  "email" varchar,
  "login" varchar,
  "name" varchar,
  "birthday" date
);

 CREATE TABLE IF NOT EXISTS "films" (
  "id" integer PRIMARY KEY,
  "name" varchar,
  "description" text,
  "release_date" date,
  "duration" integer,
  "rating" varchar
);

 CREATE TABLE IF NOT EXISTS "genre" (
  "genre_id" integer PRIMARY KEY,
  "genre" varchar
);

 CREATE TABLE IF NOT EXISTS "likes" (
  "user_id" integer,
  "film_id" integer,
  PRIMARY KEY ("user_id", "film_id")
);

CREATE TABLE IF NOT EXISTS "film_genre" (
  "film_id" integer,
  "genre_id" integer,
  PRIMARY KEY ("film_id", "genre_id")
);

ALTER TABLE "friends" ADD FOREIGN KEY  ("following_user_id") REFERENCES "users" ("id");

ALTER TABLE "friends" ADD FOREIGN KEY  ("followed_user_id") REFERENCES "users" ("id");

ALTER TABLE "likes" ADD FOREIGN KEY  ("user_id") REFERENCES "users" ("id");

ALTER TABLE "likes" ADD FOREIGN KEY  ("film_id") REFERENCES "films" ("id");
