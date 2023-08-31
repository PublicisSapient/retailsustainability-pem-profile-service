package com.publicis.sapient.p2p.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product-service")
public interface ProductService {
    @DeleteMapping("/product-service/products/user/{userId}")
    void deleteProducts(@PathVariable String userId, @CookieValue("refreshToken") String refreshToken);
}
