package ru.otus.hw.service;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;

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
  
  private final LocalizedIOService ioService;
  
  private final QuestionDao questionDao;
  
  @Override
  public TestResult executeTestFor(Student student) {
    ioService.printLine(EMPTY);
    ioService.printLineLocalized("TestService.answer.the.questions");
    ioService.printLine("");
    
    var questions = questionDao.findAll();
    var testResult = new TestResult(student);
    for (var question : questions) {
      var isAnswerValid = false;
      displayQuestionWithAnswers(question, ioService);
      int chosenAnswer = ioService.readIntForRangeWithPromptLocalized(INIT_COUNTER,
          question.answers().size(),
          "TestService.choose.answer.banner",
          "TestService.answer.invalid.banner");
      isAnswerValid = question.answers().get(chosenAnswer - INIT_COUNTER).isCorrect();
      testResult.applyAnswer(question, isAnswerValid);
    }
    return testResult;
  }
  
  private static void displayQuestionWithAnswers(Question question, IOService ioService) {
    ioService.printFormattedLine(QUESTION_TEMPLATE, question.text());
    question.answers().forEach(addCounter((counter, answer) ->
        ioService.printFormattedLine(ANSWER_TEMPLATE, counter, answer.text())));
  }
  
  private static <T> Consumer<T> addCounter(BiConsumer<Short, T> biConsumer) {
    Short[] counter = {INIT_COUNTER};
    return element -> biConsumer.accept(counter[0]++, element);
  }
  
}
