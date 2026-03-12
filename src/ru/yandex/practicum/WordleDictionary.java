package ru.yandex.practicum;

import java.util.List;
import java.io.PrintWriter;
import java.util.*;
/*
этот класс содержит в себе список слов List<String>
    его методы похожи на методы списка, но учитывают особенности игры
    также этот класс может содержать рутинные функции по сравнению слов, букв и т.д.
 */
public class WordleDictionary {

    private List<String> words;
    private final PrintWriter log;
    private final Random random = new Random();

    public WordleDictionary(List<String> words, PrintWriter log) {
        this.words = new ArrayList<>(words);
        this.log = log;
        log.println("Словарь. Количество слов: " + words.size());
    }
public String getRandomWord() {
        if (words.isEmpty()) {
            throw new IllegalStateException("Словарь пуст");
        }
        String word = words.get(random.nextInt(words.size()));
        log.println("Выбрано случайное слвоо: " + word);
        return word;
}
public boolean containsWord(String word) {
        if (word == null) {
            return false;
        }
        String normalized = WordUtils.normalize(word);
        boolean contains = words.contains(normalized);
        log.println("Проверка наличия слов '" + normalized + "' в словаре: " + contains);
        return contains;
}
public List<String> getWords() {

        return new ArrayList<>(words);
}
    public List<String> getWordsCopy() {
        return new ArrayList<>(words);
    }
public static String analyzeGuess(String guess, String answer) {
    if (guess == null || answer == null || guess.length() != answer.length()) {
        throw new IllegalArgumentException("Слова должны быть с одинаковой длинной");
    }
    char[] result = new char[answer.length()];
    boolean[] answerUsed = new boolean[answer.length()];
    for (int i = 0; i < guess.length(); i++) {
        if (guess.charAt(i) == answer.charAt(i)) {
            result[i] = '+';
            answerUsed[i] = true;
        }
    }
    for (int i = 0; i < guess.length(); i++) {
        if (result[i] == '+') {
            continue;
        }
        char guessChar = guess.charAt(i);
        boolean found = false;

        for (int j = 0; j < answer.length(); j++) {
            if (!answerUsed[j] && answer.charAt(j) == guessChar) {
                result[i] = '^';
                answerUsed[j] = true;
                found = true;
                break;
            }
        }
        if (!found) {
            result[i] = '-';
        }
    }
    return new String(result);
}
}
