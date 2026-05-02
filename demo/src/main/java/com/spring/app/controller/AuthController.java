package com.spring.app.controller;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spring.app.dto.AuthResponse;
import com.spring.app.dto.LoginRequest;
import com.spring.app.security.JwtUtil;

@RestController
@RequestMapping("/auth")
public class AuthController {

        private final AuthenticationManager authenticationManager;
        private final UserDetailsService userDetailsService;
        private final JwtUtil jwtUtil;

        @Value("${jwt.expiration}")
        private long jwtExpiration;

        public AuthController(AuthenticationManager authenticationManager,
                        UserDetailsService userDetailsService,
                        JwtUtil jwtUtil) {
                this.authenticationManager = authenticationManager;
                this.userDetailsService = userDetailsService;
                this.jwtUtil = jwtUtil;
        }

        @PostMapping("/login")
        public ResponseEntity<?> login(@RequestBody LoginRequest request) {
                try {
                        authenticationManager.authenticate(
                                        new UsernamePasswordAuthenticationToken(
                                                        request.getUsername(),
                                                        request.getPassword()));

                        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
                        String token = jwtUtil.generateToken(userDetails);
                        String rol = userDetails.getAuthorities().stream()
                                        .findFirst()
                                        .map(authority -> authority.getAuthority().replaceFirst("^ROLE_", ""))
                                        .orElse("");

                        ResponseCookie jwtCookie = ResponseCookie.from("jwtToken", token)
                                        .httpOnly(true)
                                        .path("/")
                                        .maxAge(jwtExpiration / 1000)
                                        .sameSite("Lax")
                                        .build();

                        return ResponseEntity.ok()
                                        .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                                        .body(new AuthResponse(token, rol));

                } catch (BadCredentialsException e) {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                        .body("Credenciales invalidas");
                }
        }

        @GetMapping("/logout")
        public void logout(HttpServletResponse response) throws java.io.IOException {
                ResponseCookie jwtCookie = ResponseCookie.from("jwtToken", "")
                                .httpOnly(true)
                                .path("/")
                                .maxAge(0)
                                .sameSite("Lax")
                                .build();

                response.addHeader(HttpHeaders.SET_COOKIE, jwtCookie.toString());
                response.sendRedirect("/");
        }
}
