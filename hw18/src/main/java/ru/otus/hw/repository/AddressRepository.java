package ru.otus.hw.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.hw.entity.Address;

import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Long> {
    Optional<Address> findTopBy();
}
