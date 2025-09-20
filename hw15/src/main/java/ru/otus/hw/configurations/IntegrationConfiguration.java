package ru.otus.hw.configurations;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.integration.IntegrationMessageHeaderAccessor;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.messaging.MessageChannel;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
@RequiredArgsConstructor
public class IntegrationConfiguration {

    private final TaskExecutor integrationFlowTaskExecutor;


    @Bean
    public MessageChannel receiveTextChannel() {
        return MessageChannels.executor(integrationFlowTaskExecutor).getObject();
    }

    @Bean
    public MessageChannel returnResultChannel() {
        return MessageChannels.executor(integrationFlowTaskExecutor).getObject();
    }

    // Filtering of empty messages before aggregation is assumed. Additionally, we
    // override the default release strategy to make the flow robust against filtering.
    @Bean
    public IntegrationFlow wordsCountFlow() {
        return IntegrationFlow.from(receiveTextChannel())
                .log(LoggingHandler.Level.INFO, "Stage 1: Splitting sentence into words")
                .<String, String[]>transform(snt -> snt.split(StringUtils.SPACE))
                .log(LoggingHandler.Level.INFO, "Stage 2: Sanitizing, converting, and filtering each word")
                .split()// Splits the array into individual messages, one for each word
                .<String, String>transform(wrd -> wrd.replaceAll("\\p{Punct}", StringUtils.EMPTY).toLowerCase())
                .<String>filter(StringUtils::isNotBlank)
                .log(LoggingHandler.Level.INFO, "Stage 3: Aggregating processed words")
                // override the default release strategy
                .aggregate(argSpc -> argSpc.releaseStrategy(msgGrp ->
                        msgGrp.getMessages().stream()
                                .anyMatch(msg -> {
                                    var sequenceNumber = msg.getHeaders()
                                            .get(IntegrationMessageHeaderAccessor.SEQUENCE_NUMBER, Integer.class);
                                    var sequenceSize = msg.getHeaders()
                                            .get(IntegrationMessageHeaderAccessor.SEQUENCE_SIZE, Integer.class);
                                    return sequenceNumber != null && sequenceNumber.equals(sequenceSize);
                                })))
                .log(LoggingHandler.Level.INFO, "Stage 4: Counting words")
                .<List<String>, Map<String, Long>>transform(wrdLst -> wrdLst.stream()
                                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting())))
                .channel(returnResultChannel())
                .get();
    }
}