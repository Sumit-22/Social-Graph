package com.social.ingestor.controller;

import com.social.ingestor.repo.PostRepository;
// If you have a specific class for JWT, import it.
// If you haven't created it in this service yet, remove the @MockBean for it.
// import com.social.ingestor.security.JwtTokenProvider;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.userdetails.UserDetailsService; // Import this
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostController.class)
public class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostRepository postRepository;

    @MockBean
    private KafkaTemplate<String, String> kafkaTemplate;

    // --- FIX: Mock Security Dependencies ---
    // Spring Security needs a UserDetailsService to start, even if you don't use it directly.
    @MockBean
    private UserDetailsService userDetailsService;

    // IF your SecurityConfig uses a JwtTokenProvider, you MUST mock it here too.
    // Uncomment the lines below if you have created this class in this service.
    // @MockBean
    // private JwtTokenProvider jwtTokenProvider;
    // ---------------------------------------

    @Test
    public void testCreatePostWithoutAuth() throws Exception {
        mockMvc.perform(post("/post/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"authorId\":\"user123\",\"content\":\"Hello world\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testCreatePostWithValidAuth() throws Exception {
        // Since we are mocking everything, this token won't actually be validated
        // unless you attach a real Security Filter.
        // For now, let's see if we can just get the context to load (200 OK or 403 Forbidden).
        String token = "Bearer valid.jwt.token";

        mockMvc.perform(post("/post/create")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"authorId\":\"user123\",\"content\":\"Hello world\"}"))
                .andExpect(status().isOk());
    }
}
https://www.linkedin.com/posts/jiyasilawat_hiring-sdejobs-softwareengineer-activity-7415268071961001984-7fhZ?utm_source=social_share_send&utm_medium=member_desktop_web&rcm=ACoAADZJwgUBUV_yhKm__MPkWb2-vyuRGXIu1xI

https://www.linkedin.com/posts/alexxubyte_systemdesign-coding-interviewtips-activity-7415803959824535552--RiX?utm_source=social_share_send&utm_medium=member_desktop_web&rcm=ACoAADZJwgUBUV_yhKm__MPkWb2-vyuRGXIu1xI