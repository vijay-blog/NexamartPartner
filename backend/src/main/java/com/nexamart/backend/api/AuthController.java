package com.nexamart.backend.api;

import com.nexamart.backend.api.ApiModels.*;
import com.nexamart.backend.exception.ApiException;
import com.nexamart.backend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
  private final AuthService auth;
  public AuthController(AuthService auth) { this.auth = auth; }

  @PostMapping("/login")
  public LoginResponse login(@Valid @RequestBody LoginRequest request) {
    LoginResponse response = auth.login(request);
    requirePartnerRole(response);
    return response;
  }

  @PostMapping("/register")
  public LoginResponse registerDeliveryPartner(@Valid @RequestBody RegisterRequest request) {
    return auth.register(request);
  }

  @PostMapping("/admin/login")
  public LoginResponse adminLogin(@Valid @RequestBody LoginRequest request) {
    LoginResponse response = auth.login(request);
    if (!"ADMIN".equals(response.user().role())) {
      throw new ApiException(HttpStatus.FORBIDDEN, "Admin access required.");
    }
    return response;
  }

  @PostMapping("/refresh")
  public LoginResponse refresh(@Valid @RequestBody RefreshRequest request) {
    LoginResponse response = auth.refresh(request);
    requirePartnerRole(response);
    return response;
  }

  @PostMapping("/logout")
  public ResponseEntity<Void> logout() { return ResponseEntity.noContent().build(); }

  private void requirePartnerRole(LoginResponse response) {
    String role = response.user().role();
    if (!"ADMIN".equals(role) && !"DELIVERY_PARTNER".equals(role)) {
      throw new ApiException(HttpStatus.FORBIDDEN, "Admin or delivery partner access required.");
    }
  }
}
