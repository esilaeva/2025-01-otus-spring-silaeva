package ru.otus.hw.dao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.io.FileNotFoundException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.Mockito.when;
import static ru.otus.hw.dao.CsvQuestionDao.ERROR_MESSAGE;

@SpringBootTest(classes = CsvQuestionDao.class)
class CsvQuestionDaoTest {

    private static final String RIGHT_QUESTION_FILE = "questions_test_file.csv";
    private static final String ABSENT_QUESTION_FILE = "questions_wrong.csv";

    @MockitoBean
    private TestFileNameProvider fileNameProvider;

    @Autowired
    private QuestionDao questionDao;

    @Test
    @DisplayName("Parse valid CSV and return correctly parsed questions")
    void findAll_whenValidFile_returnsCorrectlyParsedQuestions() {
        //Arrange
        when(fileNameProvider.getTestFileName()).thenReturn(RIGHT_QUESTION_FILE);

        //Act
        List<Question> questionList = questionDao.findAll();

        Question question = questionList.get(0);
        List<Answer> answers = question.answers();

        //Assert
        assertThat(questionList).isNotEmpty().hasSize(1);
        assertThat(question.text()).isEqualTo("What is the capital of France?");
        assertThat(answers)
                .extracting(Answer::text, Answer::isCorrect)
                .containsExactly(
                        tuple("Paris", true),
                        tuple("London", false),
                        tuple("Berlin", false));
    }

    @Test
    @DisplayName("Throw exception when question file is missing")
    void findAll_whenFileMissing_throwsQuestionReadException() {
        //Arrange
        when(fileNameProvider.getTestFileName()).thenReturn(ABSENT_QUESTION_FILE);

        //Assert
        assertThatThrownBy(() -> questionDao.findAll())
                .isInstanceOf(QuestionReadException.class)
                .hasMessage(ERROR_MESSAGE)
                .hasCauseInstanceOf(FileNotFoundException.class);
    }
}