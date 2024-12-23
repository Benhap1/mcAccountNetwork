//package ru.skillbox.mc_account;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletResponse;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.ResponseEntity;
//import org.springframework.mock.web.MockFilterChain;
//import org.springframework.mock.web.MockHttpServletRequest;
//import org.springframework.mock.web.MockHttpServletResponse;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.web.client.RestTemplate;
//import ru.skillbox.mc_account.web.filter.AuthFilter;
//import ru.skillbox.mc_account.entity.Role;
//import ru.skillbox.mc_account.security.AccountDetails;
//import ru.skillbox.mc_account.security.CustomUserDetailsService;
//import ru.skillbox.mc_account.security.JwtUtils;
//
//import java.io.IOException;
//import java.util.Base64;
//import java.util.UUID;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNull;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.when;
//
//
//@SpringBootTest(classes = McAccountApplication.class)
//public class AuthFilterTest {
//
//    @Autowired
//    private AuthFilter authFilter;
//
//    @MockBean
//    private RestTemplate restTemplate;
//
//    @MockBean
//    private CustomUserDetailsService customUserDetailsService;
//
//    @MockBean
//    private JwtUtils jwtUtils;
//
//    private String validToken;
//
//    @BeforeEach
//    void setUp() {
//        String payload = "{\"sub\":\"testuser@gmail.com\"}";
//        String encodedPayload = Base64.getEncoder().encodeToString(payload.getBytes());
//        String tokenHeader = Base64.getEncoder().encodeToString("{\"alg\":\"HS256\"}".getBytes());
//        String signature = UUID.randomUUID().toString();
//        validToken = tokenHeader + "." + encodedPayload + "." + signature;
//    }
//
//    @Test
//    void testValidTokenFilter() throws ServletException, IOException {
//        when(restTemplate.exchange(
//                Mockito.eq("http://auth-service/api/v1/auth/validate"),
//                Mockito.eq(HttpMethod.GET),
//                any(),
//                Mockito.eq(Boolean.class))
//        ).thenReturn(ResponseEntity.ok(true));
//
//        when(jwtUtils.getEmailFromToken(anyString())).thenReturn("testuser@gmail.com");
//        AccountDetails userDetails = new AccountDetails(
//                UUID.randomUUID(), "Admin", "Adminov", "testuser@gmail.com",
//                "adminpassword", Role.ADMIN, false, false
//        );
//        when(customUserDetailsService.loadUserByUsername("testuser@gmail.com")).thenReturn(userDetails);
//        MockHttpServletRequest request = new MockHttpServletRequest();
//        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + validToken);
//        MockHttpServletResponse response = new MockHttpServletResponse();
//        FilterChain filterChain = new MockFilterChain();
//        authFilter.doFilter(request, response, filterChain);
//        assertEquals("testuser@gmail.com", SecurityContextHolder.getContext().getAuthentication().getName());
//    }
//
//
//    @Test
//    void testEmptyToken() throws Exception {
//        MockHttpServletRequest request = new MockHttpServletRequest();
//        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer ");
//        MockHttpServletResponse response = new MockHttpServletResponse();
//        FilterChain filterChain = new MockFilterChain();
//        authFilter.doFilter(request, response, filterChain);
//        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
//        assertEquals("Authorization token is missing", response.getErrorMessage());
//        assertNull(SecurityContextHolder.getContext().getAuthentication());
//    }
//
//    @Test
//    void testTokenNotValidated() throws Exception {
//        MockHttpServletRequest request = new MockHttpServletRequest();
//        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + validToken);
//        MockHttpServletResponse response = new MockHttpServletResponse();
//        FilterChain filterChain = new MockFilterChain();
//
//        when(restTemplate.exchange(
//                Mockito.eq("http://auth-service/api/v1/auth/validate"),
//                Mockito.eq(HttpMethod.GET),
//                any(),
//                Mockito.eq(Boolean.class))
//        ).thenReturn(ResponseEntity.ok(false));
//
//        authFilter.doFilter(request, response, filterChain);
//        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
//        assertEquals("Invalid Authorization Token", response.getErrorMessage());
//        assertNull(SecurityContextHolder.getContext().getAuthentication());
//    }
//
//    @Test
//    void testAuthServiceUnavailable() throws Exception {
//        MockHttpServletRequest request = new MockHttpServletRequest();
//        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + validToken);
//        MockHttpServletResponse response = new MockHttpServletResponse();
//        FilterChain filterChain = new MockFilterChain();
//
//        when(restTemplate.exchange(
//                Mockito.eq("http://auth-service/api/v1/auth/validate"),
//                Mockito.eq(HttpMethod.GET),
//                any(),
//                Mockito.eq(Boolean.class))
//        ).thenThrow(new RuntimeException("Service Unavailable"));
//
//        authFilter.doFilter(request, response, filterChain);
//        assertEquals(HttpServletResponse.SC_SERVICE_UNAVAILABLE, response.getStatus());
//        assertEquals("Auth service is unavailable", response.getErrorMessage());
//        assertNull(SecurityContextHolder.getContext().getAuthentication());
//    }
//}
