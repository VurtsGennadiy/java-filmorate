MERGE INTO "genres" USING (
    VALUES  ('комедия'),
            ('драма'),
            ('мультфильм'),
            ('триллер'),
            ('документальный'),
            ('боевик')
    ) AS new_genres ("name")
ON "genres"."name" = new_genres."name"
WHEN NOT MATCHED THEN
    INSERT ("name") VALUES (new_genres."name");

MERGE INTO mpa USING (
    VALUES  ('G'),
            ('PG'),
            ('PG-13'),
            ('R'),
            ('NC-17')
    ) AS new_mpa (name)
ON mpa.name = new_mpa.name
WHEN NOT MATCHED THEN
    INSERT (name) VALUES (new_mpa.name);
