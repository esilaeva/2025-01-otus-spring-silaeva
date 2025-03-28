package ru.otus.hw.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
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

@SpringBootTest(classes = TestServiceImpl.class)
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

    @MockitoBean
    private LocalizedIOService ioService;

    @MockitoBean
    private QuestionDao questionDao;

    @Autowired
    private TestServiceImpl testService;

    @Captor
    private ArgumentCaptor<Short> counterCaptor;

    @Captor
    private ArgumentCaptor<String> answerCaptor;


    @BeforeEach
    void setUp() {
        Mockito.reset(ioService, questionDao);
        when(questionDao.findAll()).thenReturn(List.of(QUESTION));
    }


    @Test
    @DisplayName("executeTestFor should display question and answers properly")
    void executeTestFor_shouldDisplayQuestionAndAnswersProperly() {

        //Arrange: simulate that the user chooses the right answer.
        when(ioService.readIntForRangeWithPromptLocalized(anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(RIGHT_ANSWER);

        //Act: execute the test for the provided student.
        testService.executeTestFor(STUDENT);

        //Assert: verify that the question is displayed using the proper template.
        verify(ioService, times(1)).printFormattedLine(eq(QUESTION_TEMPLATE), eq(TEST_QUESTION));

        //Assert: verify that each answer is displayed with proper numbering.
        verify(ioService, times(3)).printFormattedLine(
                eq(ANSWER_TEMPLATE),
                counterCaptor.capture(),
                answerCaptor.capture()
        );
        assertThat(counterCaptor.getAllValues()).containsExactly((short) 1, (short) 2, (short) 3);
        assertThat(answerCaptor.getAllValues()).containsExactly(MOSCOW, PARIS, LONDON);
    }


    @Test
    @DisplayName("executeTestFor when correct answer selected should count as correct")
    void executeTestFor_whenCorrectAnswerSelected_shouldCountAsCorrect() {

        //Arrange: simulate that the user chooses the right answer.
        when(ioService.readIntForRangeWithPromptLocalized(anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(RIGHT_ANSWER);

        //Act: execute the test for the provided student.
        var result = testService.executeTestFor(STUDENT);

        //Assert: verify that right answer is correctly counted.
        assertThat(result.getRightAnswersCount()).isEqualTo(1);
        assertThat(result.getAnsweredQuestions()).containsExactly(QUESTION);

    }

    @Test
    @DisplayName("executeTestFor when wrong answer selected should count as incorrect")
    void executeTestFor_whenWrongAnswerSelected_shouldCountAsIncorrect() {

        //Arrange: simulate that the user chooses the wrong answer.
        when(ioService.readIntForRangeWithPromptLocalized(anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(2);

        //Act: execute the test for the provided student
        var result = testService.executeTestFor(STUDENT);

        //Assert: verify that wrong answer to question does not count.
        assertThat(result.getRightAnswersCount()).isZero();
        assertThat(result.getAnsweredQuestions()).containsExactly(QUESTION);
    }
}