package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.otus.hw.adapter.FeignAddressAdapter;
import ru.otus.hw.dto.AddressDisplayDto;
import ru.otus.hw.entity.Address;
import ru.otus.hw.mapper.AddressMapper;
import ru.otus.hw.model.ApiResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final FeignAddressAdapter feignAddressAdapter;

    private final AddressDatabaseService addressDatabaseService;

    private final AddressMapper addressMapper;


    @Override
    public AddressDisplayDto getCurrentAddress() {
        return addressMapper.toAddressDisplayDto(getAddress());
    }

    private Address getAddress() {
        try {
            ApiResponse response = feignAddressAdapter.getAddressFromRemoteServer();
            Address address = addressMapper.toAddress(response.data().get(0));
            addressDatabaseService.saveAddress(address);
            return address;
        } catch (Exception e) {
            log.error("Failed to get current address", e);
            return addressDatabaseService.getAddressFromDb();
        }
    }
}
