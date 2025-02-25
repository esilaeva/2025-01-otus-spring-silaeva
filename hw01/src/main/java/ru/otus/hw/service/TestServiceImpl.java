package ru.otus.hw.service;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import ru.otus.hw.dao.QuestionDao;

@RequiredArgsConstructor
public class TestServiceImpl implements TestService {
    
    public static final String ANSWER_TEMPLATE = "%5s. %s";
    
    public static final String QUESTION_TEMPLATE = "%s. %s";
    
    private static final String LINE_SEPARATOR = "------------------------";
    
    private static final int INIT_COUNTER = 1;
    
    private final IOService ioService;
    
    private final QuestionDao questionDao;
    
    private static <T> Consumer<T> addCounter(BiConsumer<Short, T> biConsumer) {
        Short[] counter = {INIT_COUNTER};
        return element -> biConsumer.accept(counter[0]++, element);
    }
    
    @Override
    public void executeTest() {
        ioService.printLine("");
        ioService.printFormattedLine("Please answer the questions below:%n");
        // Получить вопросы из дао и вывести их с вариантами ответов
        questionDao.findAll().forEach(addCounter((answerCounter, question) -> {
            ioService.printFormattedLine(QUESTION_TEMPLATE, answerCounter, question.text());
            question.answers().forEach(addCounter((questionCounter, answer) ->
                ioService.printFormattedLine(ANSWER_TEMPLATE, questionCounter, answer.text())));
            ioService.printLine(LINE_SEPARATOR);
        }));
    }
}