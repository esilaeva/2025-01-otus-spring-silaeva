package ru.otus.hw01.dao;

import com.opencsv.bean.CsvToBeanBuilder;
import lombok.RequiredArgsConstructor;
import ru.otus.hw01.config.TestFileNameProvider;
import ru.otus.hw01.dao.dto.QuestionDto;
import ru.otus.hw01.domain.Question;
import ru.otus.hw01.exceptions.QuestionReadException;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

@RequiredArgsConstructor
public class CsvQuestionDao implements QuestionDao {
  private static final char SEPARATOR = ';';
  
  private final TestFileNameProvider fileNameProvider;
  
  @Override
  public List<Question> findAll() {
    // Использовать CsvToBean
    // https://opencsv.sourceforge.net/#collection_based_bean_fields_one_to_many_mappings
    // Использовать QuestionReadException
    // Про ресурсы: https://mkyong.com/java/java-read-a-file-from-resources-folder/
    try (Reader reader = new InputStreamReader(getFileFromResourceAsStream(fileNameProvider.getTestFileName()))) {
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
      throw new QuestionReadException("Error reading questions", e);
    }
  }
  
  private InputStream getFileFromResourceAsStream(String fileName) {
    InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName);
    if (inputStream == null) {
      throw new QuestionReadException("Question file " + fileName + " not found!");
    } else {
      return inputStream;
    }
  }
}
