package ru.otus.hw.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;


@DisplayName("Our count words gateway: ")
@SpringBootTest
class WordsCountGatewayTest {

    private static final String TEST_SENTENCE_1 = "Beware of dissipating your powers; strive constantly to concentrate them. " +
            "Genius thinks it can do whatever it sees others doing, but is sure to repent of every ill judged outlay.";

    private static final Map<String, Long> EXPECTED_COUNT_MAP_1 = Map.ofEntries(
            Map.entry("beware", 1L),
            Map.entry("of", 2L),
            Map.entry("dissipating", 1L),
            Map.entry("your", 1L),
            Map.entry("powers", 1L),
            Map.entry("strive", 1L),
            Map.entry("constantly", 1L),
            Map.entry("to", 2L),
            Map.entry("concentrate", 1L),
            Map.entry("them", 1L),
            Map.entry("genius", 1L),
            Map.entry("thinks", 1L),
            Map.entry("it", 2L),
            Map.entry("can", 1L),
            Map.entry("do", 1L),
            Map.entry("whatever", 1L),
            Map.entry("sees", 1L),
            Map.entry("others", 1L),
            Map.entry("doing", 1L),
            Map.entry("but", 1L),
            Map.entry("is", 1L),
            Map.entry("sure", 1L),
            Map.entry("repent", 1L),
            Map.entry("every", 1L),
            Map.entry("ill", 1L),
            Map.entry("judged", 1L),
            Map.entry("outlay", 1L)
    );

    private static final String TEST_SENTENCE_2 = "Science arose from poetry - when times change the two can meet again" +
            " on a higher level as friends.";

    private static final Map<String, Long> EXPECTED_COUNT_MAP_2 = Map.ofEntries(
            Map.entry("science", 1L),
            Map.entry("arose", 1L),
            Map.entry("from", 1L),
            Map.entry("poetry", 1L),
            Map.entry("when", 1L),
            Map.entry("times", 1L),
            Map.entry("change", 1L),
            Map.entry("the", 1L),
            Map.entry("two", 1L),
            Map.entry("can", 1L),
            Map.entry("meet", 1L),
            Map.entry("again", 1L),
            Map.entry("on", 1L),
            Map.entry("a", 1L),
            Map.entry("higher", 1L),
            Map.entry("level", 1L),
            Map.entry("as", 1L),
            Map.entry("friends", 1L)
    );

    private static final String TEST_SENTENCE_3 = "Extends the Spring programming model to support the well-known " +
            "Enterprise Integration Patterns.";

    private static final Map<String, Long> EXPECTED_COUNT_MAP_3 = Map.ofEntries(
            Map.entry("extends", 1L),
            Map.entry("the", 2L),
            Map.entry("spring", 1L),
            Map.entry("programming", 1L),
            Map.entry("model", 1L),
            Map.entry("to", 1L),
            Map.entry("support", 1L),
            Map.entry("wellknown", 1L),
            Map.entry("enterprise", 1L),
            Map.entry("integration", 1L),
            Map.entry("patterns", 1L)
    );

    @Autowired
    private WordsCountGateway wordsCountGateway;

    private static Stream<Arguments> provideTestSentenceAndExpectedWordsMap() {
        return Stream.of(
                Arguments.of(TEST_SENTENCE_1, EXPECTED_COUNT_MAP_1),
                Arguments.of(TEST_SENTENCE_2, EXPECTED_COUNT_MAP_2),
                Arguments.of(TEST_SENTENCE_3, EXPECTED_COUNT_MAP_3)
        );
    }

    @DisplayName("should correct counts words in sentence:")
    @ParameterizedTest(name = "{0}")
    @MethodSource("provideTestSentenceAndExpectedWordsMap")
    void countWordsInGivenSentence(String testSentence, Map<String, Long> expectedCountMap) {

        assertThat(wordsCountGateway.countWords(testSentence))
                .isNotEmpty()
                .hasSize(expectedCountMap.size())
                .containsExactlyInAnyOrderEntriesOf(expectedCountMap);
    }
}