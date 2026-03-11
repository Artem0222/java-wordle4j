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
    private int steps;
    private WordleDictionary dictionary;
    private PrintWriter log;
    private List<String> previousGuesses;
    private boolean isGameOver;
    private boolean isWon;
    private static final int MAX_ATTEMPTS = 6;

    public WordleGame (WordleDictionary dictionary, PrintWriter log) {
        this.dictionary = dictionary;
        this.log = log;
        this.answer = dictionary.getRandomWord();
        this.remainingAttempts = MAX_ATTEMPTS;
        this.previousGuesses = new ArrayList<>();
        this.isGameOver = false;
        this.isWon = false;
    }


}
