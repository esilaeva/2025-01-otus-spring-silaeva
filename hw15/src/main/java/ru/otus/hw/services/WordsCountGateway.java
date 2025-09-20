package ru.otus.hw.services;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;

import java.util.Map;

@MessagingGateway
public interface WordsCountGateway {

    @Gateway(requestChannel = "receiveTextChannel", replyChannel = "returnResultChannel")
    Map<String, Long> countWords(String inputText);

}
