package ru.yandex.practicum;


import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import ru.yandex.practicum.exceptions.InvalidWordException;
import ru.yandex.practicum.exceptions.WordNotFoundException;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class WordleTest {
    private PrintWriter testLog;
    private WordleDictionary testDictionary;
    private WordleGame game;

    @BeforeEach
    void setUp() {
        testLog = new PrintWriter(System.out, true);

        List<String> words = Arrays.asList("герой", "кошка");
        testDictionary = new WordleDictionary(words, testLog);
    }

    @Test
    void testNormalize() {

        assertEquals("герой", WordUtils.normalize("ГЕРОЙ"));

        assertEquals("ежик", WordUtils.normalize("Ёжик"));

        assertEquals("кот", WordUtils.normalize(" кот "));

        assertEquals("", WordUtils.normalize(null));
    }

    @Test
    void testIsValidWord() {

        assertTrue(WordUtils.isValidWord("герой"));

        assertFalse(WordUtils.isValidWord("дом"));

        assertFalse(WordUtils.isValidWord("квартира"));

        assertFalse(WordUtils.isValidWord("hello"));
    }

    @Test
    void testAnalyzeGuess() {

        assertEquals("+++++", WordleDictionary.analyzeGuess("герой", "герой"));

        assertEquals("+^-^-", WordleDictionary.analyzeGuess("гонец", "герой"));

        assertEquals("-----", WordleDictionary.analyzeGuess("абвгд", "клмнп"));
    }

    @Test
    void testGameCreation() {
        game = new WordleGame(testDictionary, testLog);

        assertNotNull(game.getAnswer());
        assertEquals(6, game.getRemainingAttempts());
        assertFalse(game.isGameOver());
        assertFalse(game.isWon());
    }

    @Test
    void testSuccessfulGuess() throws InvalidWordException, WordNotFoundException {
        // Создаем игру только с одним словом
        WordleDictionary dict = new WordleDictionary(List.of("герой"), testLog);
        game = new WordleGame(dict, testLog);

        String result = game.makeGuess("герой");

        assertEquals("+++++", result);
        assertTrue(game.isWon());
        assertTrue(game.isGameOver());
        assertEquals(5, game.getRemainingAttempts()); // Было 6, стало 5
    }

    @Test
    void testWrongGuess() throws InvalidWordException, WordNotFoundException {
        game = new WordleGame(testDictionary, testLog);

        String result = game.makeGuess("кошка");

        assertNotEquals("+++++", result);
        assertFalse(game.isWon());
        assertFalse(game.isGameOver());
        assertEquals(5, game.getRemainingAttempts());
    }

    @Test
    void testGameOverAfterSixAttempts() throws InvalidWordException, WordNotFoundException {
        game = new WordleGame(testDictionary, testLog);


        for (int i = 0; i < 6; i++) {
            if (i < 5) {

                game.makeGuess("кошка");
                assertFalse(game.isGameOver());
            } else {
                // 6-я попытка
                game.makeGuess("кошка");
                assertTrue(game.isGameOver());
                assertFalse(game.isWon());
            }
        }

        assertEquals(0, game.getRemainingAttempts());
    }

    @Test
    void testInvalidWordException() {
        game = new WordleGame(testDictionary, testLog);

        assertThrows(InvalidWordException.class, () -> {
            game.makeGuess("дом");
        });

        assertThrows(InvalidWordException.class, () -> {
            game.makeGuess("hello");
        });
    }

    @Test
    void testWordNotFoundException() {
        game = new WordleGame(testDictionary, testLog);

        assertThrows(WordNotFoundException.class, () -> {
            game.makeGuess("арбуз");
        });
    }

    @Test
    void testGetHint() throws InvalidWordException, WordNotFoundException {
        game = new WordleGame(testDictionary, testLog);


        String hint = game.getHint();
        assertNotNull(hint);

        String hintWord = hint.contains(" ") ? hint.split(" ")[0] : hint;
        assertTrue(testDictionary.containsWord(hintWord) || hintWord.length() == 5);
    }

    @Test
    void testContainsWord() {

        assertTrue(testDictionary.containsWord("герой"));
        assertTrue(testDictionary.containsWord("ГЕРОЙ"));
        assertFalse(testDictionary.containsWord("арбуз"));
    }
}

