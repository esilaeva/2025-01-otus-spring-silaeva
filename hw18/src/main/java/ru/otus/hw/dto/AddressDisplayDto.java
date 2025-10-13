package ru.otus.hw.dto;

public record AddressDisplayDto(

        String street,

        String streetName,

        String buildingNumber,

        String city,

        String zipcode,

        String country,

        String countryCode,

        Double latitude,

        Double longitude
) {
}
