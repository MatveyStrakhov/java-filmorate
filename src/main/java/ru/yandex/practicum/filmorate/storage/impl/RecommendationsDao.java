package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.LikesMapper;
import ru.yandex.practicum.filmorate.storage.RecommendationsStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j

public class RecommendationsDao implements RecommendationsStorage {
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final LikesMapper likesMapper;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Film> getRecommendedFilms(int userId) {
        HashMap<Integer, HashMap<Film, Double>> initialData = new HashMap<>();
        Set<Film> films = filmStorage.returnAllFilms().stream().sorted(Comparator.comparing(Film::getId, Integer::compareTo)).collect(Collectors.toCollection(LinkedHashSet::new));
        Set<Integer> users = userStorage.returnAllUsers().stream().map(User::getId).collect(Collectors.toSet());
        List<Like> likes = getLikes();
        likes.forEach((like -> log.info(like.getUserId().toString() + like.getFilmId().toString())));
        for (Integer id : users) {
            HashMap<Film, Double> filmMarkMap = new HashMap<>();
            initialData.put(id, filmMarkMap);
            for (Film film : films) {
                for (Like like : likes) {
                    if ((like.getFilmId() == film.getId()) && (id == like.getUserId())) {
                        Double mark = 10.0;
                        filmMarkMap.put(film, mark);
                        initialData.put(id, filmMarkMap);
                    }
                }
            }
        }
        SlopeOne slopeOne = new SlopeOne();
        return slopeOne.executeSlopeOneAlg(initialData, films, userId);
    }

    private List<Like> getLikes() {
        String sql = "SELECT * FROM likes";
        List<Like> likes = jdbcTemplate.query(sql, likesMapper);
        return likes;
    }

    private class SlopeOne {

        private Map<Film, Map<Film, Double>> diff = new HashMap<>();
        private Map<Film, Map<Film, Integer>> freq = new HashMap<>();
        private Map<Integer, HashMap<Film, Double>> outputData = new HashMap<>();
        private Set<Film> films;

        public List<Film> executeSlopeOneAlg(Map<Integer, HashMap<Film, Double>> inputData, Set<Film> inputFilms, Integer userId) {
            films = inputFilms;
            diff = new HashMap<>();
            freq = new HashMap<>();

            buildDifferencesMatrix(inputData);
            Map<Integer, HashMap<Film, Double>> predictionMatrix = predict(inputData);
            return getRecommendedFilms(inputData, predictionMatrix, userId);
        }

        private void buildDifferencesMatrix(Map<Integer, HashMap<Film, Double>> data) {
            for (HashMap<Film, Double> user : data.values()) {
                for (Entry<Film, Double> entry : user.entrySet()) {
                    buildMatrix(entry, user);
                }
            }
            for (Film film : diff.keySet()) {
                for (Film film2 : diff.get(film).keySet()) {
                    double oldValue = diff.get(film).get(film2);
                    int count = freq.get(film).get(film2);
                    diff.get(film).put(film2, oldValue / count);
                }
            }
        }

        private Map<Integer, HashMap<Film, Double>> predict(Map<Integer, HashMap<Film, Double>> data) {
            HashMap<Film, Double> uPred = new HashMap<>();
            HashMap<Film, Integer> uFreq = new HashMap<>();
            for (Film film : diff.keySet()) {
                uFreq.put(film, 0);
                uPred.put(film, 0.0);
            }
            for (Entry<Integer, HashMap<Film, Double>> entry : data.entrySet()) {
                buildUMatrix(entry, uPred, uFreq);
                Map<Film, Double> clean = buildCleanMap(entry, uPred, uFreq);
                outputData.put(entry.getKey(), (HashMap<Film, Double>) clean);
            }
            return outputData;
        }

        private void buildMatrix(Entry<Film, Double> entry, HashMap<Film, Double> user) {
            if (!diff.containsKey(entry.getKey())) {
                diff.put(entry.getKey(), new HashMap<>());
                freq.put(entry.getKey(), new HashMap<>());
            }
            for (Entry<Film, Double> entry2 : user.entrySet()) {
                int oldCount = 0;
                if (freq.get(entry.getKey()).containsKey(entry2.getKey())) {
                    oldCount = freq.get(entry.getKey()).get(entry2.getKey());
                }
                double oldDiff = 0.0;
                if (diff.get(entry.getKey()).containsKey(entry2.getKey())) {
                    oldDiff = diff.get(entry.getKey()).get(entry2.getKey());
                }
                double observedDiff = entry.getValue() - entry2.getValue();
                freq.get(entry.getKey()).put(entry2.getKey(), oldCount + 1);
                diff.get(entry.getKey()).put(entry2.getKey(), oldDiff + observedDiff);
            }
        }

        private void buildUMatrix(Entry<Integer, HashMap<Film, Double>> entry, HashMap<Film, Double> uPred, HashMap<Film, Integer> uFreq) {
            for (Film film : entry.getValue().keySet()) {
                for (Film film2 : diff.keySet()) {
                    try {
                        double predictedValue = diff.get(film2).get(film) + entry.getValue().get(film);
                        double finalValue = predictedValue * freq.get(film2).get(film);
                        uPred.put(film2, uPred.get(film2) + finalValue);
                        uFreq.put(film2, uFreq.get(film2) + freq.get(film2).get(film));
                    } catch (NullPointerException ignored) {
                    }
                }
            }
        }

        private Map<Film, Double> buildCleanMap(Entry<Integer, HashMap<Film, Double>> entry, HashMap<Film, Double> uPred, HashMap<Film, Integer> uFreq) {
            HashMap<Film, Double> clean = new HashMap<>();
            for (Film film : uPred.keySet()) {
                if (uFreq.get(film) > 0) {
                    clean.put(film, uPred.get(film) / uFreq.get(film));
                }
            }
            for (Film film : films) {
                if (entry.getValue().containsKey(film)) {
                    clean.put(film, entry.getValue().get(film));
                } else if (!clean.containsKey(film)) {
                    clean.put(film, -1.0);
                }
            }
            return clean;
        }

        private List<Film> getRecommendedFilms(Map<Integer, HashMap<Film, Double>> originalData, Map<Integer, HashMap<Film, Double>> predictedData, int userId) {
            Map<Film, Double> listOfRated = originalData.get(userId);
            Map<Film, Double> listWithPredicted = predictedData.get(userId);
            Set<Film> recommendedFilms = new LinkedHashSet<>();
            for (Film film : listWithPredicted.keySet()) {
                if ((listOfRated.get(film) == null) && (listWithPredicted.get(film) > 0)) {
                    recommendedFilms.add(film);
                }
            }
            log.info("recommendation: " + recommendedFilms);
            List<Film> output = recommendedFilms.stream().sorted(Comparator.comparing(Film::getId, Integer::compareTo)).collect(Collectors.toList());
            return output;
        }
    }
}
