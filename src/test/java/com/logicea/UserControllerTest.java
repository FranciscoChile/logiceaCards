package com.logicea;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.logicea.config.JwtAuthenticationEntryPoint;
import com.logicea.config.JwtTokenUtil;
import com.logicea.service.JwtUserDetailsService;
import com.logicea.service.UserService;
import com.logicea.web.UserController;

@ComponentScan({"jwt"})
@WebMvcTest(value = UserController.class, includeFilters = {
    @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtTokenUtil.class)})
public class UserControllerTest {
    
    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtUserDetailsService jwtUserDetailsService;

    @MockBean
    private JwtTokenUtil jwtUtil;

    @MockBean
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    private static UserDetails dummy;
    private static String jwtToken;

    @BeforeEach
    public void setUp() {
        dummy = new User("user@email.com", "123456", new ArrayList<>());
        jwtToken = jwtUtil.generateToken(dummy);
    }
    
    @Test
    public void givenToken_whenGetSecureRequest_thenOK() throws Exception {
        
        mvc.perform(
            get("/api/users")
            .header("Authorization", "Bearer " + jwtToken)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            )
        .andExpect(status().isOk() );
    }

    @Test
    public void listAllUsers_whenGetMethod()
            throws Exception {

        com.logicea.model.UserDTO user = new com.logicea.model.UserDTO();
        user.setEmail("");

        List<com.logicea.model.UserDTO> allUsers = List.of(user);

        when(userService.findAll()).thenReturn(allUsers);

        RequestBuilder request = MockMvcRequestBuilders
                .get("/api/users")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(request)
                .andExpect(status().is2xxSuccessful())
                .andReturn();
            //.andExpect(jsonPath("$", hasSize(1)))
            //.andExpect(jsonPath("$[0].name", is(user.getName())));

        String content = result.getResponse().getContentAsString();

    }
}
