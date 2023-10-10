# java-filmorate
Template repository for Filmorate project.


![Untitled (4)](https://github.com/MatveyStrakhov/java-filmorate/assets/126389529/ffef9572-2fe1-41b3-a67a-3edcbbe2e567)

Пример запроса всех жанров фильма #1


SELECT g.genre  
FROM genre AS g
WHERE g.genre_id IN (
	SELECT fg.genre_id
	FROM films AS f 
	JOIN film_genre AS fg ON f.id=fg.film_id
	WHERE f.id=1
);
