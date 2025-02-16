package ru.otus.hw01.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.hw01.dao.QuestionDao;
import ru.otus.hw01.domain.Answer;
import ru.otus.hw01.domain.Question;

import java.util.List;

import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class TestServiceImplTest {
  
  private static final String TEST_QUESTION = "What is the capital of Russia?";
  private static final String MOSCOW = "Moscow";
  private static final String PARIS = "Paris";
  
  @Mock
  private IOService ioService;
  
  @Mock
  private QuestionDao questionDao;
  
  private TestService testService;
  
  private final List<Answer> answers = List.of(
      new Answer(MOSCOW, true),
      new Answer(PARIS, false));
  
  private final List<Question> questions = List.of(
      new Question(TEST_QUESTION, answers));
  
  @BeforeEach
  public void setUp() {
    testService = new TestServiceImpl(ioService, questionDao);
  }
  
  @Test
  void executeTest() {
    
    when(questionDao.findAll()).thenReturn(questions);
    
    testService.executeTest();
    
    verify(ioService, times(3)).printLine(anyString());
    verify(ioService, times(1)).printFormattedLine(anyString());
    verify(ioService, times(2)).printFormattedLine(anyString(), any());
  }
}