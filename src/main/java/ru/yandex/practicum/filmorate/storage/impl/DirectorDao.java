package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.IdNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorMapper;

import java.util.List;

@Component
public class DirectorDao {
    private final JdbcTemplate jdbcTemplate;
    private final DirectorMapper directorMapper;

    public DirectorDao(JdbcTemplate jdbcTemplate, DirectorMapper directorMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.directorMapper = directorMapper;
    }

    public Director createDirector(Director director){
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("directors")
                .usingGeneratedKeyColumns("director_id");
        director.setDirectorId(simpleJdbcInsert.executeAndReturnKey(director.toMap()).intValue());
        return director;
    }
    public Director updateDirector(Director director){
        if(isValidDirector(director.getDirectorId())){
            String sql = "UPDATE directors SET director=? WHERE director_id=?";
            jdbcTemplate.update(sql,director.getDirectorName(),director.getDirectorId());
            return director;
        }
        else throw new IdNotFoundException("Incorrect director id!");

    }
    public List<Director> getAllDirectors(){
        return jdbcTemplate.query("SELECT * FROM directors", directorMapper);

    }
    public Director getDirectorById(int directorId){
        if(isValidDirector(directorId)){
            String sql = "SELECT * FROM directors WHERE director_id="+directorId;
            return jdbcTemplate.queryForObject(sql,directorMapper);
        }
        else throw new IdNotFoundException("Incorrect director id!");

    }

    public void deleteDirector(int directorId){
        if(isValidDirector(directorId)){
            String sql = "delete from directors where director_id=? UNION ALL delete from film_director WHERE director_id=?;";
            jdbcTemplate.update(sql,directorId,directorId);
        }
        else throw new IdNotFoundException("Director not found!");
    }
    public boolean isValidDirector(int directorId){
        return jdbcTemplate.queryForRowSet("SELECT director_id FROM director WHERE director_id=?", directorId).next();

    }
}
