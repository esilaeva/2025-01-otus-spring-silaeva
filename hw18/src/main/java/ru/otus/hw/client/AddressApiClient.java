package ru.otus.hw.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.otus.hw.model.ApiResponse;

@FeignClient(name = "address-api")
public interface AddressApiClient {

    @GetMapping("/api/v2/addresses")
    ApiResponse getAddresses(@RequestParam("_quantity") int quantity);
}
