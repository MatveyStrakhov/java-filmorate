# java-filmorate
Template repository for Filmorate project.

![Untitled (5)](https://github.com/MatveyStrakhov/java-filmorate/assets/126389529/b8fad68c-3acd-443b-9720-a7bbe633c1f1)



Пример запроса всех жанров фильма #1


SELECT g.genre  
FROM genre AS g
WHERE g.genre_id IN (
	SELECT fg.genre_id
	FROM films AS f 
	JOIN film_genre AS fg ON f.id=fg.film_id
	WHERE f.id=1
);
