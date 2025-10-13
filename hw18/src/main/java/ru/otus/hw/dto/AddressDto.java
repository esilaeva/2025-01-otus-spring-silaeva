package ru.otus.hw.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record AddressDto(

        Long id,

        String street,

        String streetName,

        String buildingNumber,

        String city,

        String zipcode,

        String country,

        @JsonProperty("country_code")
        String countryCode,

        Double latitude,

        Double longitude
) {
}
