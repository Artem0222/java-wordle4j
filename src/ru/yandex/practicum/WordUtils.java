package ru.yandex.practicum;

public final class WordUtils {
    private static final String RUSSIAN_LETTERS_PATTERN = "[а-я]{5}";
    private static final int WORD_LENGTH = 5;

    private WordUtils() {

    }

    public static String normalize(String word) {
        if (word == null) {
            return "";
        }
        return word.toLowerCase().replace('ё', 'е').trim();
    }

    public static boolean isValidWord(String word) {
        if (word == null) {
            return false;
        }
        String normalized = normalize(word);
        return normalized.length() == WORD_LENGTH && normalized.matches(RUSSIAN_LETTERS_PATTERN);
    }

    public static int getWordLength() {
        return WORD_LENGTH;
    }
}
