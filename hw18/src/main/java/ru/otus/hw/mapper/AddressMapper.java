package ru.otus.hw.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.otus.hw.dto.AddressDisplayDto;
import ru.otus.hw.dto.AddressDto;
import ru.otus.hw.entity.Address;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    AddressDto toAddressDto(Address address);

    @Mapping(target = "id", ignore = true)
    Address toAddress(AddressDto addressDto);

    AddressDisplayDto toAddressDisplayDto(Address address);
}
