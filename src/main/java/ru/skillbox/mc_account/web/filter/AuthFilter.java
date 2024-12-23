package ru.skillbox.mc_account.web.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.skillbox.mc_account.security.AccountDetails;
import ru.skillbox.mc_account.security.CustomUserDetailsService;
import ru.skillbox.mc_account.security.JwtUtils;
import java.io.IOException;


@Component
@RequiredArgsConstructor
public class AuthFilter implements Filter {

    private final RestTemplate restTemplate;
    private final CustomUserDetailsService userDetailsService;
    private final JwtUtils jwtUtils;


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String authToken = httpRequest.getHeader(HttpHeaders.AUTHORIZATION);
        String requestURI = httpRequest.getRequestURI();

        if (isSwaggerRequest(requestURI)) {
            chain.doFilter(request, response);
            return;
        }

        if (requestURI.equals("/api/v1/account/total")) {
            chain.doFilter(request, response);
            return;
        }


        if (authToken != null && authToken.startsWith("Bearer ")) {
            authToken = authToken.substring(7);
        } else {
            return;
        }

        if (isValidToken(authToken, httpResponse)) {
            try {
                String userEmail = jwtUtils.getEmailFromToken(authToken);
                UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

                if (userDetails instanceof AccountDetails) {
                    ((AccountDetails) userDetails).setToken(authToken);
                }

                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            } catch (Exception e) {
                httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
                return;
            }
        }

        chain.doFilter(request, response);
    }

    private boolean isSwaggerRequest(String requestURI) {
        return requestURI.startsWith("/swagger") ||
                requestURI.startsWith("/v3/api-docs") ||
                requestURI.startsWith("/swagger-ui");
    }

    protected boolean isValidToken(String token, HttpServletResponse response) throws IOException {
        if (token == null || token.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authorization token is missing");
            return false;
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);
            HttpEntity<String> entity = new HttpEntity<>(headers);
            String AUTH_SERVICE_URL = "http://79.174.80.223:8085/api/v1/auth/validate";
            ResponseEntity<Boolean> authResponse = restTemplate.exchange(AUTH_SERVICE_URL, HttpMethod.GET, entity, Boolean.class);

            if (authResponse.getStatusCode().is2xxSuccessful()) {
                return true;
            } else {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Authorization Token");
                return false;
            }
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "Auth service is unavailable");
            return false;
        }
    }
}