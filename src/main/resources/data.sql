MERGE INTO "genres" USING (
    VALUES
        ('комедия'),
        ('драма'),
        ('мультфильм'),
        ('триллер'),
        ('документальный'),
        ('боевик')
) AS new_genres ("name")
ON "genres"."name" = new_genres."name"
WHEN NOT MATCHED THEN
    INSERT ("name") VALUES (new_genres."name");