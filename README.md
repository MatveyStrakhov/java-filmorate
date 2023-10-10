# java-filmorate
Template repository for Filmorate project.

![Untitled (3)](https://github.com/MatveyStrakhov/java-filmorate/assets/126389529/bbe05dc7-9673-4e23-bace-054d0f42cefd)

Пример запроса всех жанров фильма #1


SELECT g.genre  
FROM genre AS g
WHERE g.genre_id IN (
	SELECT fg.genre_id
	FROM films AS f 
	JOIN film_genre AS fg ON f.id=fg.film_id
	WHERE f.id=1
);
