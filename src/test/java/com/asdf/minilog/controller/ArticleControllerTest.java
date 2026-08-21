package com.asdf.minilog.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.asdf.minilog.dto.ArticleResponseDto;
import com.asdf.minilog.exception.ArticleNotFoundException;
import com.asdf.minilog.security.JwtAuthenticationEntryPoint;
import com.asdf.minilog.security.JwtRequestFilter;
import com.asdf.minilog.security.MinilogUserDetails;
import com.asdf.minilog.service.ArticleService;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ArticleController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ArticleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ArticleService articleService;

    @MockitoBean
    private JwtRequestFilter jwtRequestFilter;

    @MockitoBean
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @MockitoBean(name = "jpaMappingContext")
    private JpaMetamodelMappingContext jpaMappingContext;

    private final LocalDateTime fixtureDateTime =
            LocalDateTime.of(2025, 1, 1, 0, 0, 0);

    private final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    private final String formattedFixtureDateTime =
            fixtureDateTime.format(formatter);

    @BeforeEach
    void setUp() {
        MinilogUserDetails userDetails =
                new MinilogUserDetails(
                        1L,
                        "testuser",
                        "password",
                        List.of(() -> "ROLE_AUTHOR"));

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities());

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);
    }

    @Test
    public void testCreateArticle() throws Exception {
        ArticleResponseDto responseDto =
                ArticleResponseDto.builder()
                        .articleId(1L)
                        .content("Test Content")
                        .authorId(1L)
                        .authorName("testuser")
                        .createdAt(fixtureDateTime)
                        .build();

        when(articleService.createArticle(any(), anyLong()))
                .thenReturn(responseDto);

        mockMvc.perform(
                        post("/api/v2/article")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "content": "Test Content"
                                        }
                                        """))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetArticle() throws Exception {
        ArticleResponseDto responseDto =
                ArticleResponseDto.builder()
                        .articleId(1L)
                        .content("Test Content")
                        .authorId(1L)
                        .authorName("testuser")
                        .createdAt(fixtureDateTime)
                        .build();

        when(articleService.getArticleById(anyLong()))
                .thenReturn(responseDto);

        mockMvc.perform(get("/api/v2/article/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.articleId").value(1L))
                .andExpect(jsonPath("$.content").value("Test Content"))
                .andExpect(jsonPath("$.authorId").value(1L))
                .andExpect(jsonPath("$.authorName").value("testuser"))
                .andExpect(
                        jsonPath("$.createdAt")
                                .value(formattedFixtureDateTime));
    }

    @Test
    public void testUpdateArticle() throws Exception {
        ArticleResponseDto responseDto =
                ArticleResponseDto.builder()
                        .articleId(1L)
                        .content("Updated Content")
                        .authorId(1L)
                        .authorName("testuser")
                        .createdAt(fixtureDateTime)
                        .build();

        when(articleService.updateArticle(
                any(),
                anyLong(),
                any()))
                .thenReturn(responseDto);

        mockMvc.perform(
                        put("/api/v2/article/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "content": "Updated Content"
                                        }
                                        """))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteArticle() throws Exception {
        mockMvc.perform(delete("/api/v2/article/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testGetArticleByUserId() throws Exception {
        Long userId = 1L;
        Long articleId = 1L;

        ArticleResponseDto responseDto =
                ArticleResponseDto.builder()
                        .articleId(articleId)
                        .content("Test Content")
                        .authorId(userId)
                        .authorName("testuser")
                        .createdAt(fixtureDateTime)
                        .build();

        List<ArticleResponseDto> responseList =
                Collections.singletonList(responseDto);

        when(articleService.getArticleListByUserId(anyLong()))
                .thenReturn(responseList);

        mockMvc.perform(
                        get("/api/v2/article")
                                .param("authorId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].articleId").value(articleId))
                .andExpect(jsonPath("$[0].content").value("Test Content"))
                .andExpect(jsonPath("$[0].authorId").value(userId))
                .andExpect(jsonPath("$[0].authorName").value("testuser"))
                .andExpect(
                        jsonPath("$[0].createdAt")
                                .value(formattedFixtureDateTime));
    }

    @Test
    public void testGlobalExceptionHandler() throws Exception {
        when(articleService.getArticleById(anyLong()))
                .thenThrow(
                        new ArticleNotFoundException(
                                "Article Not Found"));

        mockMvc.perform(get("/api/v2/article/999"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Article Not Found"));
    }
}