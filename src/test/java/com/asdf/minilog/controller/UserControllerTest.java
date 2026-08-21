package com.asdf.minilog.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.asdf.minilog.dto.UserRequestDto;
import com.asdf.minilog.dto.UserResponseDto;
import com.asdf.minilog.exception.UserNotFoundException;
import com.asdf.minilog.security.JwtAuthenticationEntryPoint;
import com.asdf.minilog.security.JwtRequestFilter;
import com.asdf.minilog.security.MinilogUserDetails;
import com.asdf.minilog.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Optional;
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

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockitoBean private UserService userService;

    @MockitoBean private JwtRequestFilter jwtRequestFilter;

    @MockitoBean private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @MockitoBean(name = "jpaMappingContext")
    private JpaMetamodelMappingContext jpaMappingContext;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MinilogUserDetails userDetails =
                new MinilogUserDetails(
                        1L,
                        "testuser",
                        "password",
                        List.of(() -> "ROLE_AUTHOR", () -> "ROLE_ADMIN"));

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    public void testGetUsers() throws Exception {
        List<UserResponseDto> userResponseDtoList =
                List.of(
                        UserResponseDto.builder().id(1L).username("testuser").build(),
                        UserResponseDto.builder().id(2L).username("Test User 2").build());

        when(userService.getUsers()).thenReturn(userResponseDtoList);

        mockMvc.perform(get("/api/v2/user"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].username").value("testuser"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].username").value("Test User 2"));
    }

    @Test
    public void testGetUserById() throws Exception {
        UserResponseDto userResponseDto =
                UserResponseDto.builder().id(1L).username("testuser").build();

        when(userService.getUserById(anyLong())).thenReturn(Optional.of(userResponseDto));

        mockMvc.perform(get("/api/v2/user/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    public void testCreateUser() throws Exception {
        UserRequestDto userRequestDto =
                UserRequestDto.builder().username("testuser").password("password").build();

        UserResponseDto userResponseDto =
                UserResponseDto.builder().id(1L).username("testuser").build();

        when(userService.createUser(any(UserRequestDto.class))).thenReturn(userResponseDto);

        mockMvc.perform(
                        post("/api/v2/user")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(userRequestDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    public void testUpdateUser() throws Exception {
        UserRequestDto userRequestDto =
                UserRequestDto.builder().username("testuser").password("password").build();

        UserResponseDto userResponseDto =
                UserResponseDto.builder().id(1L).username("testuser").build();

        when(userService.updateUser(
                        any(MinilogUserDetails.class), anyLong(), any(UserRequestDto.class)))
                .thenReturn(userResponseDto);

        mockMvc.perform(
                        put("/api/v2/user/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(userRequestDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    public void testDeleteUser() throws Exception {
        mockMvc.perform(delete("/api/v2/user/1")).andExpect(status().isNoContent());
    }

    @Test
    public void testGlobalExceptionHandler() throws Exception {
        when(userService.getUserById(anyLong()))
                .thenThrow(new UserNotFoundException("Test Exception"));

        mockMvc.perform(get("/api/v2/user/999"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Test Exception"));
    }
}
