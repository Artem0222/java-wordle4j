package ru.yandex.practicum;

import ru.yandex.practicum.exceptions.DictionaryLoadException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/*
этот класс содержит в себе всю рутину по работе с файлами словарей и с кодировками
    ему нужны методы по загрузке списка слов из файла по имени файла
    на выходе должен быть класс WordleDictionary
 */
public class WordleDictionaryLoader {
    private final PrintWriter log;

    public WordleDictionaryLoader(PrintWriter log) {

        this.log = log;
    }

    public WordleDictionary loadDictionay(String filename) throws DictionaryLoadException {
        List<String> validWords = new ArrayList<>();

        log.println("Словарь: " + filename);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader
                (new FileInputStream(filename), StandardCharsets.UTF_8)
        )) {
            String line;
            int lineNumber = 0;
            int skippedLines = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                String normalized = WordUtils.normalize(line);

                if (WordUtils.isValidWord(normalized)) {
                    validWords.add(normalized);
                } else if (!normalized.isEmpty()) {
                    skippedLines++;
                    log.println("Пропущена строка " + lineNumber + ": '" + line + "' (не " +
                            "подходит для игры)");
                }
            }
            if (validWords.isEmpty()) {
                throw new DictionaryLoadException("Словарь не содержит подходящих слов (5 букв)");
            }
            log.println("Загружено слов: " + validWords.size() + ", пропущено строк: " +
                    skippedLines);
            return new WordleDictionary(validWords, log);
        } catch (FileNotFoundException e) {
            log.println("Ошибка. Файл словаря не найден: " + filename);
            throw new DictionaryLoadException("Ошибка. Файл словаря не найден: " + filename, e);
        } catch (IOException e) {
            log.println("Ошибка ввода-вывода при чтении файла: " + e.getMessage());
            throw new DictionaryLoadException("Ошибка при чтении файла словаря " + filename, e);
        }
    }
}

