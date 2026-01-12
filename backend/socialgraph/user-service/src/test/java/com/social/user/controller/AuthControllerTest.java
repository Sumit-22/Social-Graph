package com.social.user.controller;

import com.social.user.entity.User;
import com.social.user.repository.UserRepository;
import com.social.user.repository.FollowRepository; // Added
import com.social.user.service.FollowService;       // Added
import com.social.user.security.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false) // Security filters OFF
@TestPropertySource(properties = {
        "jwt.secret=this-is-a-very-long-secret-key-for-testing-purposes-that-meets-hs512-requirements-of-64-bytes-at-least",
        "jwt.expiration=3600000",
        "jwt.refresh-expiration=7200000"
})
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private PasswordEncoder passwordEncoder;

    // 👇 Ye teen (3) Mocks naye errors ko rokne ke liye add kiye hain
    @MockBean
    private FollowService followService;

    @MockBean
    private FollowRepository followRepository;

    @MockBean(name = "userController") // UserController ko bhi mock kar diya taaki conflict na ho
    private UserController userController;
    // 👆

    @Test
    public void testRegisterSuccess() throws Exception {
        when(userRepository.findByUsername(any())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(new User());
        when(jwtTokenProvider.generateToken(any(), any())).thenReturn("mocked-jwt-token");

        mockMvc.perform(post("/auth/register")
                        .contentType("application/json")
                        .content("{\"username\":\"newuser\",\"password\":\"password123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    public void testRegisterDuplicateUser() throws Exception {
        User existingUser = new User();
        existingUser.setUsername("testuser");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(existingUser));

        mockMvc.perform(post("/auth/register")
                        .contentType("application/json")
                        .content("{\"username\":\"testuser\",\"password\":\"password123\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    public void testLoginSuccess() throws Exception {
        // 1. Prepare Data
        User user = new User();
        user.setId("123-uuid");
        user.setUsername("testuser");
        user.setPassword("encodedPassFromDB"); // Important: Simulate a DB password

        // 2. Mock Repository
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        // 3. Mock Password Matching (CRITICAL: Many controllers check this manually)
        // This ensures if your code calls passwordEncoder.matches(), it returns true
        when(passwordEncoder.matches(any(), any())).thenReturn(true);

        // 4. Mock AuthenticationManager
        // We create a token with 3 arguments (user, pass, authorities) so isAuthenticated() = true
        Authentication auth = new UsernamePasswordAuthenticationToken(
                "testuser",
                "password123",
                Collections.emptyList()
        );
        when(authenticationManager.authenticate(any())).thenReturn(auth);

        // 5. Mock Token Provider
        // Using any() twice to avoid the "Expected 2 arguments" error you saw earlier
        when(jwtTokenProvider.generateToken(any(), any())).thenReturn("mocked-jwt-token");

        // 6. Perform Request
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"testuser\",\"password\":\"password123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }
}