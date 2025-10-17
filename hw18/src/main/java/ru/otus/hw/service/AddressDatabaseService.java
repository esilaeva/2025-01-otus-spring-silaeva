package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.entity.Address;
import ru.otus.hw.repository.AddressRepository;

@Service
@RequiredArgsConstructor
public class AddressDatabaseService {

    private final AddressRepository addressRepository;

    @Transactional
    public void saveAddress(Address address) {
        addressRepository.deleteAll();
        addressRepository.flush();
        addressRepository.save(address);
    }

    @Transactional(readOnly = true)
    public Address getAddressFromDb() {
        return addressRepository.findTopBy().orElse(null);
    }
}
