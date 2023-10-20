# java-filmorate
Template repository for Filmorate project.

![Untitled (6)](https://github.com/MatveyStrakhov/java-filmorate/assets/126389529/af3c9420-647f-473e-8dbd-b27cadbac599)


Пример запроса всех жанров фильма #1


SELECT g.genre  
FROM genre AS g
WHERE g.genre_id IN (
	SELECT fg.genre_id
	FROM films AS f 
	JOIN film_genre AS fg ON f.id=fg.film_id
	WHERE f.id=1
);
