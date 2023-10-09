# java-filmorate
Template repository for Filmorate project.

![Untitled (2)](https://github.com/MatveyStrakhov/java-filmorate/assets/126389529/5abfc0f9-fd06-4f36-95c6-13b20f496d8f)

Пример запроса всех жанров фильма #1


SELECT g.genre  
FROM genre AS g
WHERE g.genre_id IN (
	SELECT fg.genre_id
	FROM films AS f 
	JOIN film_genre AS fg ON f.id=fg.film_id
	WHERE f.id=1
);
