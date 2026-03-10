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
        log.println();
    }

}
