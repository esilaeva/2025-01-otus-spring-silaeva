package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    public static final String QUESTION_TEMPLATE = "%s";

    public static final String ANSWER_TEMPLATE = "%5s. %s";

    public static final String EMPTY = "";

    public static final String BANNER = "Please answer the questions below%n";

    public static final String ANSWER_PROMPT = "Select number of answer, which you think is correct:";

    public static final String INVALID_ANSWER_BANNER =
            "Invalid input! From 1 to %s only are accepted variants for answer. Repeat!";

    private static final Short INIT_COUNTER = 1;

    private final IOService ioService;

    private final QuestionDao questionDao;

    @Override
    public TestResult executeTestFor(Student student) {
        ioService.printLine(EMPTY);
        ioService.printFormattedLine(BANNER);
        var questions = questionDao.findAll();
        var testResult = new TestResult(student);
        for (var question : questions) {
            var isAnswerValid = false;
            var numberOfAnswers = question.answers().size();
            displayQuestionWithAnswers(question);
            int chosenIndex = Stream.generate(() -> {
                        var input = ioService.readStringWithPrompt(ANSWER_PROMPT);
                        Optional<Integer> parsedAnswer = parseInputToIndex(input, numberOfAnswers);
                        parsedAnswer.ifPresentOrElse(answer -> {
                                },
                                () -> ioService.printFormattedLine(INVALID_ANSWER_BANNER, numberOfAnswers));
                        return parsedAnswer;
                    })
                    .flatMap(Optional::stream)
                    .findFirst()
                    .orElseThrow();
            isAnswerValid = question.answers().get(chosenIndex).isCorrect();
            testResult.applyAnswer(question, isAnswerValid);
        }
        return testResult;
    }

    private void displayQuestionWithAnswers(Question question) {
        ioService.printFormattedLine(QUESTION_TEMPLATE, question.text());
        question.answers().forEach(addCounter((counter, answer) ->
                ioService.printFormattedLine(ANSWER_TEMPLATE, counter, answer.text())));
    }

    private static <T> Consumer<T> addCounter(BiConsumer<Short, T> biConsumer) {
        Short[] counter = {INIT_COUNTER};
        return element -> biConsumer.accept(counter[0]++, element);
    }

    private static Optional<Integer> parseInputToIndex(String input, int numberOfAnswers) {
        try {
            int index = Integer.parseInt(input) - 1;
            return (index >= 0 && index < numberOfAnswers) ? Optional.of(index) : Optional.empty();
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }
}
