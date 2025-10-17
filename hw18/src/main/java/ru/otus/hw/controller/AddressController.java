package ru.otus.hw.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.hw.dto.AddressDisplayDto;
import ru.otus.hw.service.AddressService;

@RestController
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @GetMapping(value = "/getRandomAddress", produces = "application/json;charset=UTF-8")
    public ResponseEntity<AddressDisplayDto> getRandomAddress() {
        return ResponseEntity.ok(addressService.getCurrentAddress());
    }
}
