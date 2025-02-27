package ru.otus.hw.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;

import java.util.List;

import static org.mockito.Mockito.*;
import static ru.otus.hw.service.TestServiceImpl.ANSWER_TEMPLATE;
import static ru.otus.hw.service.TestServiceImpl.QUESTION_TEMPLATE;


@ExtendWith(MockitoExtension.class)
class TestServiceImplTest {
  
  private static final String TEST_QUESTION = "What's the capital of Russia?";
  private static final String MOSCOW = "Moscow";
  private static final String PARIS = "Paris";
  private static final List<Answer> ANSWER_LIST = List.of(
      new Answer(MOSCOW, true),
      new Answer(PARIS, false));
  private static final List<Question> QUESTION_LIST = List.of(
      new Question(TEST_QUESTION, ANSWER_LIST));
  
  
  @Mock
  private IOService ioService;
  
  @Mock
  private QuestionDao questionDao;
  
  private TestService testService;
  
  @BeforeEach
  public void setUp() {
    testService = new TestServiceImpl(ioService, questionDao);
  }
  
  @Test
  void executeTest() {
    
    when(questionDao.findAll()).thenReturn(QUESTION_LIST);
    
    testService.executeTest();
    
    verify(ioService, times(2)).printLine(anyString());
    verify(ioService, times(1)).printFormattedLine(anyString());
    verify(ioService, times(1)).printFormattedLine(eq(QUESTION_TEMPLATE),anyShort(), anyString());
    verify(ioService, times(2)).printFormattedLine(eq(ANSWER_TEMPLATE), anyShort(), anyString());
  }
}