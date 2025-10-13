package ru.otus.hw.model;

import ru.otus.hw.dto.AddressDto;

import java.util.List;

public record ApiResponse(

        String status,

        Integer code,

        String locale,

        String seed,

        Integer total,

        List<AddressDto> data
) {
}
