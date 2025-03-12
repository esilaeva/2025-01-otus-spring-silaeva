package ru.otus.hw.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static ru.otus.hw.service.TestServiceImpl.*;

@ExtendWith(MockitoExtension.class)
class TestServiceImplTest {

    private static final String TEST_QUESTION = "What's the capital of Russia?";
    private static final String MOSCOW = "Moscow";
    private static final String PARIS = "Paris";
    public static final String LONDON = "London";

    private static final List<Answer> ANSWER_LIST = List.of(
            new Answer(MOSCOW, true),
            new Answer(PARIS, false),
            new Answer(LONDON, false));

    private static final Question QUESTION =
            new Question(TEST_QUESTION, ANSWER_LIST);

    private static final String STUDENT_FIRST_NAME = "FIRST_NAME";
    private static final String STUDENT_LAST_NAME = "LAST_NAME";
    private static final Student STUDENT = new Student(STUDENT_FIRST_NAME, STUDENT_LAST_NAME);
    private static final String RIGHT_ANSWER = "1";
    public static final String INVALID_STRING = "aB#";
    public static final String OUT_MIN = "0";
    public static final String OUT_MAX = "4";

    @Mock
    private IOService ioService;

    @Mock
    private QuestionDao questionDao;

    @InjectMocks
    private TestServiceImpl testService;


    @BeforeEach
    public void setUp() {
        when(questionDao.findAll()).thenReturn(List.of(QUESTION));
    }

    @Test
    void executeTestFor_shouldDisplayQuestionAndAnswersCorrectly() {

        when(ioService.readStringWithPrompt(any())).thenReturn(RIGHT_ANSWER);
        testService.executeTestFor(STUDENT);

        verify(ioService).printFormattedLine(eq(QUESTION_TEMPLATE), eq(QUESTION.text()));

        ArgumentCaptor<Short> counterCaptor = ArgumentCaptor.forClass(Short.class);
        ArgumentCaptor<String> answerCaptor = ArgumentCaptor.forClass(String.class);

        verify(ioService, times(3)).printFormattedLine(eq(ANSWER_TEMPLATE),
                counterCaptor.capture(), answerCaptor.capture());

        assertThat(counterCaptor.getAllValues()).containsExactly((short) 1, (short) 2, (short) 3);

    }

    @Test
    void executeTestFor_shouldHandleValidAnswerCorrectly_whenUserProvidesValidInput() {

        when(ioService.readStringWithPrompt(any())).thenReturn(RIGHT_ANSWER);
        TestResult result = testService.executeTestFor(STUDENT);

        assertThat(result.getRightAnswersCount()).isEqualTo(1);
        assertThat(result.getAnsweredQuestions()).containsExactly(QUESTION);
    }


    private static Stream<Arguments> provideIncorrectUserInput() {
        return Stream.of(
                Arguments.of(Named.of("Below Minimum Value", OUT_MIN)),
                Arguments.of(Named.of("Above Maximum Value", OUT_MAX)),
                Arguments.of(Named.of("Non-Numeric Input", INVALID_STRING))
        );
    }

    @ParameterizedTest
    @MethodSource(value = "provideIncorrectUserInput")
    void executeTestFor_shouldHandleInvalidAnswerCorrectly_whenUserProvidesInvalidInput(String input) {
        when(ioService.readStringWithPrompt(any()))
                .thenReturn(input)
                .thenReturn(RIGHT_ANSWER);

        TestResult result = testService.executeTestFor(STUDENT);

        verify(ioService, times(1)).printLine(EMPTY);
        verify(ioService, times(1)).printFormattedLine(BANNER);
        verify(ioService, times(1)).printFormattedLine(eq(QUESTION_TEMPLATE), eq(QUESTION.text()));
        verify(ioService, times(3)).printFormattedLine(eq(ANSWER_TEMPLATE), anyShort(), anyString());
        verify(ioService, times(2)).readStringWithPrompt(eq(ANSWER_PROMPT));
        verify(ioService, times(1)).printFormattedLine(eq(INVALID_ANSWER_BANNER), eq(ANSWER_LIST.size()));

        assertThat(result.getRightAnswersCount()).isEqualTo(1);
        assertThat(result.getAnsweredQuestions()).containsExactly(QUESTION);
    }
}