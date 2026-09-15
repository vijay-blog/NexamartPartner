package com.nexamart.backend.api;

import com.nexamart.backend.api.ApiModels.RegistrationResponse;
import com.nexamart.backend.api.ApiModels.RegisterRequest;
import com.nexamart.backend.api.ApiModels.UserResponse;
import com.nexamart.backend.config.CorsConfig;
import com.nexamart.backend.config.SecurityConfig;
import com.nexamart.backend.exception.ApiException;
import com.nexamart.backend.exception.GlobalExceptionHandler;
import com.nexamart.backend.security.JwtAuthenticationFilter;
import com.nexamart.backend.security.JwtService;
import com.nexamart.backend.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {AuthController.class, AdminController.class, DeliveryController.class})
@Import({SecurityConfig.class, CorsConfig.class, GlobalExceptionHandler.class, AuthControllerSecurityTest.FilterConfiguration.class})
class AuthControllerSecurityTest {
  private static final String REGISTER_JSON = """
    {"name":"New Partner","email":"new@example.com","password":"password123",
     "confirmPassword":"password123","phone":"9876543210"}
    """;

  @Autowired MockMvc mvc;
  @MockBean AuthService authService;
  @MockBean JwtService jwtService;
  @MockBean com.nexamart.backend.service.AdminService adminService;
  @MockBean com.nexamart.backend.service.CatalogService catalogService;
  @MockBean com.nexamart.backend.service.DeliveryService deliveryService;

  @TestConfiguration
  static class FilterConfiguration {
    @Bean JwtAuthenticationFilter jwtAuthenticationFilter(JwtService jwtService) {
      return new JwtAuthenticationFilter(jwtService);
    }
  }

  @Test
  void registerWithoutAuthorizationReturnsLoginResponse() throws Exception {
    when(authService.register(any(RegisterRequest.class))).thenReturn(new RegistrationResponse("Delivery partner account created successfully."));

    mvc.perform(post("/api/v1/auth/register").contentType("application/json").content(REGISTER_JSON))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.message").value("Delivery partner account created successfully."));
  }

  @Test
  void mismatchedPasswordsReturnBadRequest() throws Exception {
    when(authService.register(any(RegisterRequest.class)))
      .thenThrow(new ApiException(HttpStatus.BAD_REQUEST, "Passwords do not match."));

    mvc.perform(post("/api/v1/auth/register").contentType("application/json")
        .content(REGISTER_JSON.replace("\"password123\",\"phone\"", "\"different123\",\"phone\"")))
      .andExpect(status().isBadRequest());
  }

  @Test
  void existingEmailReturnsConflict() throws Exception {
    when(authService.register(any(RegisterRequest.class)))
      .thenThrow(new ApiException(HttpStatus.CONFLICT, "Email is already registered."));

    mvc.perform(post("/api/v1/auth/register").contentType("application/json").content(REGISTER_JSON))
      .andExpect(status().isConflict());
  }

  @Test
  void invalidRegistrationReturnsBadRequest() throws Exception {
    mvc.perform(post("/api/v1/auth/register").contentType("application/json")
        .content("{\"name\":\"\",\"email\":\"invalid\",\"password\":\"\",\"confirmPassword\":\"\",\"phone\":\"\"}"))
      .andExpect(status().isBadRequest());
  }

  @Test
  void adminEndpointWithoutAuthenticationReturnsUnauthorized() throws Exception {
    mvc.perform(get("/api/v1/admin/dashboard")).andExpect(status().isUnauthorized());
    mvc.perform(post("/api/v1/admin/categories").contentType("application/json").content("{}"))
      .andExpect(status().isUnauthorized());
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void deliveryEndpointWithoutDeliveryPartnerRoleReturnsForbidden() throws Exception {
    mvc.perform(get("/api/v1/delivery/dashboard")).andExpect(status().isForbidden());
  }

  @Test
  void csrfTokenIsNotRequiredForPublicRegistration() throws Exception {
    when(authService.register(any(RegisterRequest.class))).thenReturn(new RegistrationResponse("Delivery partner account created successfully."));

    mvc.perform(post("/api/v1/auth/register").contentType("application/json").content(REGISTER_JSON))
      .andExpect(status().isOk());
  }
}
