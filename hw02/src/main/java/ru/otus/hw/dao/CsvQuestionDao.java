package ru.otus.hw.dao;

import com.opencsv.bean.CsvToBeanBuilder;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.dao.dto.QuestionDto;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;


@Repository
@RequiredArgsConstructor
public class CsvQuestionDao implements QuestionDao {

    public static final String ERROR_MESSAGE = "Error reading questions";

    private static final char SEPARATOR = ';';

    private final TestFileNameProvider fileNameProvider;

    @Override
    public List<Question> findAll() {
        // Использовать CsvToBean
        // https://opencsv.sourceforge.net/#collection_based_bean_fields_one_to_many_mappings
        // Использовать QuestionReadException
        // Про ресурсы: https://mkyong.com/java/java-read-a-file-from-resources-folder/
        try (Reader reader = new InputStreamReader(getFile(fileNameProvider.getTestFileName()))) {
            var questionDtoList = new CsvToBeanBuilder<QuestionDto>(reader)
                    .withType(QuestionDto.class)
                    .withSkipLines(1)
                    .withSeparator(SEPARATOR)
                    .build()
                    .parse();

            return questionDtoList.stream()
                    .map(QuestionDto::toDomainObject)
                    .toList();

        } catch (Exception e) {
            throw new QuestionReadException(ERROR_MESSAGE, e);
        }
    }

    private InputStream getFile(String fileName) throws IOException {

        return new ClassPathResource(fileName).getInputStream();
    }
}
