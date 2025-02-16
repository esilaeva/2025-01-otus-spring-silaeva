package ru.otus.hw01.service;

import lombok.RequiredArgsConstructor;
import ru.otus.hw01.dao.QuestionDao;

@RequiredArgsConstructor
public class TestServiceImpl implements TestService {
    
    private static final String LINE_SEPARATOR = "------------------------";
    
    private static final String ANSWER_TEMPLATE = "- %s";
    
    private final IOService ioService;
    
    private final QuestionDao questionDao;
    
    @Override
    public void executeTest() {
        ioService.printLine("");
        ioService.printFormattedLine("Please answer the questions below%n");
        // Получить вопросы из дао и вывести их с вариантами ответов
        questionDao.findAll().forEach(question -> {
            ioService.printLine(question.text());
            question.answers().forEach(answer -> ioService.printFormattedLine(ANSWER_TEMPLATE, answer.text()));
            ioService.printLine(LINE_SEPARATOR);
        });
    }
}