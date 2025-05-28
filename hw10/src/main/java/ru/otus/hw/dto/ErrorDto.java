package ru.otus.hw.dto;

import java.util.List;

public record ErrorDto(Integer code, List<String> errorMessages) {
}
