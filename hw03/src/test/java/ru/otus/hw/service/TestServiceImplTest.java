package ru.otus.hw.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static ru.otus.hw.service.TestServiceImpl.ANSWER_TEMPLATE;
import static ru.otus.hw.service.TestServiceImpl.QUESTION_TEMPLATE;

@ExtendWith(MockitoExtension.class)
class TestServiceImplTest {

    private static final String TEST_QUESTION = "What's the capital of Russia?";
    private static final String MOSCOW = "Moscow";
    private static final String PARIS = "Paris";
    private static final String LONDON = "London";

    private static final List<Answer> ANSWER_LIST = List.of(
            new Answer(MOSCOW, true),
            new Answer(PARIS, false),
            new Answer(LONDON, false));

    private static final Question QUESTION =
            new Question(TEST_QUESTION, ANSWER_LIST);

    private static final String STUDENT_FIRST_NAME = "FIRST_NAME";
    private static final String STUDENT_LAST_NAME = "LAST_NAME";
    private static final Student STUDENT = new Student(STUDENT_FIRST_NAME, STUDENT_LAST_NAME);
    private static final int RIGHT_ANSWER = 1;

    @Mock
    private LocalizedIOService ioService;

    @Mock
    private QuestionDao questionDao;

    @InjectMocks
    private TestServiceImpl testService;

    @Captor
    private ArgumentCaptor<Short> counterCaptor;

    @Captor
    private ArgumentCaptor<String> answerCaptor;


    @BeforeEach
    void setUp() {
        when(questionDao.findAll()).thenReturn(List.of(QUESTION));
    }


    @Test
    void executeTestFor_shouldDisplayQuestionAndAnswersProperly() {

        //Arrange
        when(ioService.readIntForRangeWithPromptLocalized(anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(RIGHT_ANSWER);

        //Act
        testService.executeTestFor(STUDENT);

        //Assert question display
        verify(ioService, times(1)).printFormattedLine(eq(QUESTION_TEMPLATE), eq(TEST_QUESTION));

        //Assert answers display with proper numbering
        verify(ioService, times(3)).printFormattedLine(
                eq(ANSWER_TEMPLATE),
                counterCaptor.capture(),
                answerCaptor.capture()
        );
        assertThat(counterCaptor.getAllValues()).containsExactly((short) 1, (short) 2, (short) 3);
        assertThat(answerCaptor.getAllValues()).containsExactly(MOSCOW, PARIS, LONDON);
    }


    @Test
    void executeTestFor_whenCorrectAnswerSelected_shouldCountAsCorrect() {

        //Arrange
        when(ioService.readIntForRangeWithPromptLocalized(anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(RIGHT_ANSWER);

        //Act
        var result = testService.executeTestFor(STUDENT);

        //Assert
        assertThat(result.getRightAnswersCount()).isEqualTo(1);
        assertThat(result.getAnsweredQuestions()).containsExactly(QUESTION);

    }

    @Test
    void executeTestFor_whenWrongAnswerSelected_shouldCountAsIncorrect() {

        //Arrange
        when(ioService.readIntForRangeWithPromptLocalized(anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(2); //Wrong answer

        //Act
        var result = testService.executeTestFor(STUDENT);

        //Assert
        assertThat(result.getRightAnswersCount()).isZero();
        assertThat(result.getAnsweredQuestions()).containsExactly(QUESTION);
    }
}