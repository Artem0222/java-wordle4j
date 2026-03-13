package ru.yandex.practicum;

import org.junit.jupiter.api.*;
import ru.yandex.practicum.exceptions.InvalidWordException;
import ru.yandex.practicum.exceptions.WordNotFoundException;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
        game = new WordleGame(testDictionary, testLog, "герой");

        String result = game.makeGuess("герой");

        assertEquals("+++++", result);
        assertTrue(game.isWon());
        assertTrue(game.isGameOver());
        assertEquals(5, game.getRemainingAttempts());
    }

    @Test
    void testWrongGuess() throws InvalidWordException, WordNotFoundException {
        game = new WordleGame(testDictionary, testLog, "герой");

        String result = game.makeGuess("кошка");

        assertNotEquals("+++++", result);
        assertFalse(game.isWon());
        assertFalse(game.isGameOver());
        assertEquals(5, game.getRemainingAttempts());
    }

    @Test
    void testGameOverAfterSixAttempts() throws InvalidWordException, WordNotFoundException {
        game = new WordleGame(testDictionary, testLog, "герой");

        for (int i = 0; i < 6; i++) {
            game.makeGuess("кошка");

            if (i < 5) {
                assertFalse(game.isGameOver(), "Игра должна продолжаться после " + (i + 1) + " попытки");
            } else {
                assertTrue(game.isGameOver(), "Игра должна окончиться после 6 попыток");
                assertFalse(game.isWon(), "Игрок не должен победить");
            }
        }

        assertEquals(0, game.getRemainingAttempts());
    }

    @Test
    void testInvalidWordException() {
        game = new WordleGame(testDictionary, testLog, "герой");

        assertThrows(InvalidWordException.class, () -> {
            game.makeGuess("дом");
        });

        assertThrows(InvalidWordException.class, () -> {
            game.makeGuess("hello");
        });
    }

    @Test
    void testWordNotFoundException() {
        game = new WordleGame(testDictionary, testLog, "герой");

        assertThrows(WordNotFoundException.class, () -> {
            game.makeGuess("арбуз");
        });
    }

    @Test
    void testContainsWord() {
        assertTrue(testDictionary.containsWord("герой"));
        assertTrue(testDictionary.containsWord("ГЕРОЙ"));
        assertFalse(testDictionary.containsWord("арбуз"));
    }

    @Test
    void testGetHint() throws InvalidWordException, WordNotFoundException {
        game = new WordleGame(testDictionary, testLog, "герой");

        assertEquals(3, game.getRemainingHints());

        String hint = game.getHint();
        assertNotNull(hint);
        assertEquals(2, game.getRemainingHints());
    }
}