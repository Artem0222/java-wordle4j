package ru.yandex.practicum;

import ru.yandex.practicum.exceptions.InvalidWordException;
import ru.yandex.practicum.exceptions.WordNotFoundException;

import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;

/*
в этом классе хранится словарь и состояние игры
    текущий шаг
    всё что пользователь вводил
    правильный ответ

в этом классе нужны методы, которые
    проанализируют совпадение слова с ответом
    предложат слово-подсказку с учётом всего, что вводил пользователь ранее

не забудьте про специальные типы исключений для игровых и неигровых ошибок
 */
public class WordleGame {

    private String answer;
    private int remainingAttempts;

    private WordleDictionary dictionary;
    private PrintWriter log;
    private List<String> previousGuesses;
    private List<String> previousResults;
    private boolean isGameOver;
    private boolean isWon;
    private Set<String> usedHints;
    private static final int MAX_ATTEMPTS = 6;

    private int remainingHints;
    private static final int MAX_HINTS = 3;

    public WordleGame(WordleDictionary dictionary, PrintWriter log) {
        this.dictionary = dictionary;
        this.log = log;
        this.answer = dictionary.getRandomWord();
        this.remainingAttempts = MAX_ATTEMPTS;
        this.previousGuesses = new ArrayList<>();
        this.previousResults = new ArrayList<>();
        this.usedHints = new HashSet<>();
        this.isGameOver = false;
        this.isWon = false;
        this.remainingHints = MAX_HINTS;

        log.println("Игра создана. Загадано слово - " + answer);
    }

    public WordleGame(WordleDictionary dictionary, PrintWriter log, String forcedAnswer) {
        this.dictionary = dictionary;
        this.log = log;
        this.answer = forcedAnswer;  // Используем переданное слово
        this.remainingAttempts = MAX_ATTEMPTS;
        this.previousGuesses = new ArrayList<>();
        this.previousResults = new ArrayList<>();
        this.usedHints = new HashSet<>();
        this.isGameOver = false;
        this.isWon = false;
        this.remainingHints = MAX_HINTS;

        log.println("Игра создана с заданным ответом. Загадано слово - " + answer);
        log.println("Максимум подсказок: " + MAX_HINTS);
    }

    public String makeGuess(String guess) throws InvalidWordException, WordNotFoundException {
        String normalizedGuess = WordUtils.normalize(guess);

        if (!WordUtils.isValidWord(normalizedGuess)) {
            throw new InvalidWordException("Слово должно состоять из 5 букв");
        }
        if (!dictionary.containsWord(normalizedGuess)) {
            throw new WordNotFoundException("Слово '" + normalizedGuess + "' не найдено в словаре");
        }
        if (isGameOver) {
            throw new IllegalStateException("Игра уже завершщена");
        }
        previousGuesses.add(normalizedGuess);

        String result = WordleDictionary.analyzeGuess(normalizedGuess, answer);
        previousResults.add(result);

        remainingAttempts--;

        if (normalizedGuess.equals(answer)) {
            isWon = true;
            isGameOver = true;
        } else if (remainingAttempts == 0) {
            isGameOver = true;
        }
        log.println("Ход: слово='" + normalizedGuess + "', результат='" + result +
                "', осталось попыток=" + remainingAttempts);
        return result;
    }

    public String getHint() {
        if (isGameOver) {
            return "Игра уже завершена. Загаданное слово - " + answer;
        }


        if (remainingHints <= 0) {
            return "больше нет подсказок!";
        }

        List<String> possibleWords = dictionary.getWords();


        for (int i = 0; i < previousGuesses.size(); i++) {
            String guess = previousGuesses.get(i);
            String result = previousResults.get(i);
            possibleWords = filterWordsByResult(possibleWords, guess, result);
        }


        possibleWords.removeAll(usedHints);

        String hint;
        if (possibleWords.isEmpty()) {
            hint = dictionary.getRandomWord();
        } else {
            Random random = new Random();
            hint = possibleWords.get(random.nextInt(possibleWords.size()));
        }


        remainingHints--;
        usedHints.add(hint);

        log.println("Сгенерирована подсказка: " + hint +
                " (осталось подсказок: " + remainingHints +
                ", вариантов: " + possibleWords.size() + ")");

        return hint + " (осталось подсказок: " + remainingHints + ")";
    }


    public boolean isExitCommand(String input) {
        String normalized = WordUtils.normalize(input);
        return normalized.equals("стоп");
    }


    public int getRemainingHints() {
        return remainingHints;
    }

    private List<String> filterWordsByResult(List<String> words, String guess, String result) {
        return words.stream()
                .filter(word -> matchesPattern(word, guess, result))
                .collect(Collectors.toList());
    }

    private boolean matchesPattern(String word, String guess, String result) {
        if (word.length() != guess.length()) {
            return false;
        }

        for (int i = 0; i < result.length(); i++) {
            if (result.charAt(i) == '+') {
                if (word.charAt(i) != guess.charAt(i)) {
                    return false;
                }
            }
        }


        for (int i = 0; i < result.length(); i++) {
            if (result.charAt(i) == '^') {
                char c = guess.charAt(i);
                if (word.charAt(i) == c || word.indexOf(c) == -1) {
                    return false;
                }
            }
        }

        for (int i = 0; i < result.length(); i++) {
            if (result.charAt(i) == '-') {
                char c = guess.charAt(i);

                boolean foundElsewhere = false;
                for (int j = 0; j < result.length(); j++) {
                    if (j != i && (result.charAt(j) == '+' || result.charAt(j) == '^') &&
                            guess.charAt(j) == c) {
                        foundElsewhere = true;
                        break;
                    }
                }
                if (!foundElsewhere && word.indexOf(c) != -1) {
                    return false;
                }
            }
        }

        return true;
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    public boolean isWon() {
        return isWon;
    }

    public int getRemainingAttempts() {
        return remainingAttempts;
    }

    public String getAnswer() {
        return answer;
    }

    public List<String> getPreviousGuesses() {
        return new ArrayList<>(previousGuesses);
    }

    public List<String> getPreviousResults() {
        return new ArrayList<>(previousResults);
    }
}

