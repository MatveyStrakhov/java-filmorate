# java-filmorate
Template repository for Filmorate project.

![Filmorate-ER-diagram](https://github.com/MatveyStrakhov/java-filmorate/assets/126389529/40bb7e08-6582-42d4-ba00-edede878ae10)


Пример запроса всех жанров фильма #1

SELECT g.genre  
FROM genre AS g
WHERE g.genre_id IN (
SELECT fg.genre_id
FROM films AS f
JOIN film_genre AS fg ON f.id=fg.film_id
WHERE f.id=1 );
