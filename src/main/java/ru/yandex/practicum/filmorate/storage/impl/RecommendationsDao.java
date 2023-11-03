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
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Map.Entry;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j

public class RecommendationsDao {

    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final LikesMapper likesMapper;
    private final JdbcTemplate jdbcTemplate;

    private List<Like> getLikes() {
        String sql = "SELECT * FROM likes";
        List<Like> likes = jdbcTemplate.query(sql, likesMapper);
        return likes;
    }

    public List<Film> getRecommendedFilms(int userId) {
        HashMap<Integer, HashMap<Film, Double>> initialData = new HashMap<>();
        Set<Film> films = filmStorage.returnAllFilms().stream().sorted(Comparator.comparing(Film::getId, Integer::compareTo)).collect(Collectors.toCollection(LinkedHashSet::new));
        Set<Integer> users = userStorage.returnAllUsers().stream().map(User::getId).collect(Collectors.toSet());
        List<Like> likes = getLikes();
        for (Like like : likes) {
            log.info(like.getUserId().toString() + like.getFilmId().toString());
        }
        for (Integer id : users) {
            HashMap<Film, Double> filmMarkMap = new HashMap<>();
            initialData.put(id, filmMarkMap);
            for (Film film : films) {
                likes.forEach(like -> {
                    if ((like.getFilmId() == film.getId()) && (Objects.equals(id, like.getUserId()))) {
                        Double mark = 10.0;
                        filmMarkMap.put(film, mark);
                        initialData.put(id, filmMarkMap);
                    }
                });
                }
            }
        return SlopeOne.slopeOne(initialData, films, userId);
    }

    private static class SlopeOne {

        private static Map<Film, Map<Film, Double>> diff = new HashMap<>();
        private static Map<Film, Map<Film, Integer>> freq = new HashMap<>();
        private static Map<Integer, HashMap<Film, Double>> outputData = new HashMap<>();
        private static Set<Film> films;

        public static List<Film> slopeOne(Map<Integer, HashMap<Film, Double>> inputData, Set<Film> inputFilms, Integer userId) {
            films = inputFilms;
            diff = new HashMap<>();
            freq = new HashMap<>();

            buildDifferencesMatrix(inputData);
            Map<Integer, HashMap<Film, Double>> predictionMatrix = predict(inputData);
            return getRecommendedFilms(inputData, predictionMatrix, userId);
        }

        private static void buildDifferencesMatrix(Map<Integer, HashMap<Film, Double>> data) {
            for (HashMap<Film, Double> user : data.values()) {
                for (Entry<Film, Double> e : user.entrySet()) {
                    if (!diff.containsKey(e.getKey())) {
                        diff.put(e.getKey(), new HashMap<>());
                        freq.put(e.getKey(), new HashMap<>());
                    }
                    for (Entry<Film, Double> e2 : user.entrySet()) {
                        int oldCount = 0;
                        if (freq.get(e.getKey()).containsKey(e2.getKey())) {
                            oldCount = freq.get(e.getKey()).get(e2.getKey());
                        }
                        double oldDiff = 0.0;
                        if (diff.get(e.getKey()).containsKey(e2.getKey())) {
                            oldDiff = diff.get(e.getKey()).get(e2.getKey());
                        }
                        double observedDiff = e.getValue() - e2.getValue();
                        freq.get(e.getKey()).put(e2.getKey(), oldCount + 1);
                        diff.get(e.getKey()).put(e2.getKey(), oldDiff + observedDiff);
                    }
                }
            }
            for (Film j : diff.keySet()) {
                for (Film i : diff.get(j).keySet()) {
                    double oldValue = diff.get(j).get(i);
                    int count = freq.get(j).get(i);
                    diff.get(j).put(i, oldValue / count);
                }
            }
        }

        private static Map<Integer, HashMap<Film, Double>> predict(Map<Integer, HashMap<Film, Double>> data) {
            HashMap<Film, Double> uPred = new HashMap<>();
            HashMap<Film, Integer> uFreq = new HashMap<>();
            for (Film j : diff.keySet()) {
                uFreq.put(j, 0);
                uPred.put(j, 0.0);
            }
            for (Entry<Integer, HashMap<Film, Double>> e : data.entrySet()) {
                for (Film j : e.getValue().keySet()) {
                    for (Film k : diff.keySet()) {
                        try {
                            double predictedValue = diff.get(k).get(j) + e.getValue().get(j);
                            double finalValue = predictedValue * freq.get(k).get(j);
                            uPred.put(k, uPred.get(k) + finalValue);
                            uFreq.put(k, uFreq.get(k) + freq.get(k).get(j));
                        } catch (NullPointerException ignored) {
                        }
                    }
                }
                HashMap<Film, Double> clean = new HashMap<>();
                for (Film j : uPred.keySet()) {
                    if (uFreq.get(j) > 0) {
                        clean.put(j, uPred.get(j) / uFreq.get(j));
                    }
                }
                for (Film j : films) {
                    if (e.getValue().containsKey(j)) {
                        clean.put(j, e.getValue().get(j));
                    } else if (!clean.containsKey(j)) {
                        clean.put(j, -1.0);
                    }
                }
                outputData.put(e.getKey(), clean);
            }
            return outputData;
        }

        private static List<Film> getRecommendedFilms(Map<Integer, HashMap<Film, Double>> originalData, Map<Integer, HashMap<Film, Double>> predictedData, int userId) {
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
