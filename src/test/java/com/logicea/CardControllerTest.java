package com.logicea;

import static org.junit.Assert.assertNotNull;
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
import com.logicea.service.CardService;
import com.logicea.service.JwtUserDetailsService;
import com.logicea.web.CardController;


@ComponentScan({"jwt"})
@WebMvcTest(value = CardController.class, includeFilters = {
    @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtTokenUtil.class)})
public class CardControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private CardService cardService;

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
            get("/api/cards")
            .header("Authorization", "Bearer " + jwtToken)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            )
        .andExpect(status().isOk() );
    }

    @Test
    public void listAllCards_whenGetMethod()
            throws Exception {

        com.logicea.model.CardDTO card = new com.logicea.model.CardDTO();
        card.setName("");

        List<com.logicea.model.CardDTO> allCards = List.of(card);

        when(cardService.findAll()).thenReturn(allCards);

        RequestBuilder request = MockMvcRequestBuilders
                .get("/api/cards")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(request)
                .andExpect(status().is2xxSuccessful())
                .andReturn();
            //.andExpect(jsonPath("$", hasSize(1)))
            //.andExpect(jsonPath("$[0].name", is(user.getName())));

        assertNotNull(result.getResponse().getContentAsString());

        }
    



}
