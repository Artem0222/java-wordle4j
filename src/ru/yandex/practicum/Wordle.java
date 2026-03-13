package ru.yandex.practicum;

import ru.yandex.practicum.exceptions.DictionaryLoadException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/*
в главном классе нам нужно:
    создать лог-файл (он должен передаваться во все классы)
    создать загрузчик словарей WordleDictionaryLoader
    загрузить словарь WordleDictionary с помощью класса WordleDictionaryLoader
    затем создать игру WordleGame и передать ей словарь
    вызвать игровой метод в котором в цикле опрашивать пользователя и передавать информацию в игру
    вывести состояние игры и конечный результат
 */
public class Wordle {

    public static void main(String[] args) {
        PrintWriter log = null;

        try {
            log = createLogFile();
            log.println("Запуск игры");

            WordleDictionaryLoader loader = new WordleDictionaryLoader(log);
            WordleDictionary dictionary = loader.loadDictionay("words_ru.txt");

            WordleGame game = new WordleGame(dictionary, log);

            playGame(game, log);
        } catch (DictionaryLoadException e) {
            System.err.println("Ошибка загрузки словаря " + e.getMessage());
            if (log != null) {
                log.println("Критическая ошибка " + e.getMessage());
                e.printStackTrace(log);
            }
        } catch (Exception e) {
            System.err.println("Неожиданная ошибка " + e.getMessage());
            if (log != null) {
                log.println("Неожиданная ошибка " + e.getMessage());
                e.printStackTrace(log);
            }
        } finally {
            if (log != null) {
                log.println("Завершение работы программы");
                log.close();
            }
        }
    }

    private static PrintWriter createLogFile() throws IOException {
        String logFileName = "wordle_log_" + System.currentTimeMillis() + " .txt";
        FileWriter fileWriter = new FileWriter(logFileName, StandardCharsets.UTF_8);
        return new PrintWriter(fileWriter, true);
    }

    private static void playGame(WordleGame game, PrintWriter log) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("Отгадайте слово из 5 букв за 6 попыток");
            System.out.println("Команды:");
            System.out.println("  Enter - подсказка (осталось: " + game.getRemainingHints() + ")");
            System.out.println("  'стоп' - выйти из игры");
            log.println("Начало серии");

            while (!game.isGameOver()) {
                System.out.println("Введите слово");
                String input = scanner.nextLine().trim();

                if (game.isExitCommand(input)) {
                    System.out.println("Игра остановлена");
                    log.println("Пользователь прервал игру командой: " + input);
                    break;

                    try {
                        if (input.isEmpty()) {
                            String hint = game.getHint();
                            System.out.println("Подсказка - " + hint);
                            log.println("Игрок запросил подсказку " + hint);
                            continue;
                        }
                        String result = game.makeGuess(input);

                        System.out.println("Результат " + result);
                        System.out.println("осталось попыток " + game.getRemainingAttempts());

                        if (game.isWon()) {
                            System.out.println("Поздравляем. Вы отгадали слово " + game.getAnswer());
                            log.println("Пользователь победил. Загаданное слово - " + game.getAnswer());
                            break;
                        } else if (game.isGameOver()) {
                            System.out.println("Игра окончена. Загаданное слово " + game.getAnswer());
                            log.println("Пользователь проиграл. Загаданное слово - " + game.getAnswer());
                            break;
                        }
                    } catch (ru.yandex.practicum.exceptions.InvalidWordException e) {
                        System.out.println("Ошибка: " + e.getMessage());
                        log.println("Игрок ввел некорректное слово: " + input);
                    } catch (ru.yandex.practicum.exceptions.WordNotFoundException e) {
                        System.out.println("Ошибка: " + e.getMessage());
                        log.println("Пользователь ввёл слово, которого нет в словаре - " + input);
                    }
                }
            }
        }
    }
