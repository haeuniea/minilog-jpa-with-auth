package com.asdf.minilog.controller;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.asdf.minilog.dto.ArticleResponseDto;
import com.asdf.minilog.service.ArticleService;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(FeedController.class)
public class FeedControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockitoBean private ArticleService articleService;

    @MockitoBean(name = "jpaMappingContext")
    private JpaMetamodelMappingContext jpaMappingContext;

    LocalDateTime fixtureDateTime = LocalDateTime.of(2025, 1, 1, 0, 0, 0);
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    String formattedFixtureDateTime = fixtureDateTime.format(formatter);

    @Test
    public void testGetFeedList() throws Exception {
        ArticleResponseDto articleResponseDto =
                ArticleResponseDto.builder()
                        .articleId(1L)
                        .content("Test Content")
                        .authorId(1L)
                        .authorName("Test User")
                        .createdAt(fixtureDateTime)
                        .build();
        when(articleService.getFeedListByFollowerId(anyLong()))
                .thenReturn(Collections.singletonList(articleResponseDto));

        mockMvc.perform(get("/api/v1/feed?followerId=1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].articleId").value(1L))
                .andExpect(jsonPath("$[0].content").value("Test Content"))
                .andExpect(jsonPath("$[0].authorId").value(1L))
                .andExpect(jsonPath("$[0].authorName").value("Test User"))
                .andExpect(jsonPath("$[0].createdAt").value(formattedFixtureDateTime));
    }
}
