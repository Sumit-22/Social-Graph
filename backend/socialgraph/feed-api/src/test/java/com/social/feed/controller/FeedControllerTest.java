package com.social.feed.controller;

import com.social.feed.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach; // Import added
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Set;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FeedController.class)
@AutoConfigureMockMvc(addFilters = false) // Security bypassed
class FeedControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StringRedisTemplate stringRedisTemplate;

    // Use raw type or <String, String> because StringRedisTemplate works with Strings
    @MockBean
    private ZSetOperations<String, String> zSetOperations;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    // Ye method har Test run hone se pehle chalega
    @BeforeEach
    void setUp() {
        // ERROR FIX: Humne mock ko bataya ki jab opsForZSet maanga jaye, toh hamara mock object do
        when(stringRedisTemplate.opsForZSet()).thenReturn(zSetOperations);

        // Default behavior: Return empty set to avoid NullPointerException inside controller logic
        when(zSetOperations.reverseRange(anyString(), anyLong(), anyLong()))
                .thenReturn(Collections.emptySet());
    }

    @Test
    void testGetFeedWithoutAuth() throws Exception {
        // Kyunki addFilters=false hai, ye request controller tak jayegi
        // Aur Redis mock ready hai (setUp method ki wajah se), toh ye pass hoga
        mockMvc.perform(get("/feed?userId=user123&limit=10"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetFeedWithValidToken() throws Exception {
        // JWT Mocks
        when(jwtTokenProvider.validateToken(anyString())).thenReturn(true);
        when(jwtTokenProvider.getUserIdFromToken(anyString())).thenReturn("user123");

        // Note: Redis mock 'setUp()' method mein already handle ho gaya hai

        mockMvc.perform(
                get("/feed?userId=user123&limit=10")
                        .header("Authorization", "Bearer test.token")
        ).andExpect(status().isOk());
    }
}