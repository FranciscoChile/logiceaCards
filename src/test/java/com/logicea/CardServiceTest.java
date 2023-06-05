package com.logicea;

import java.util.ArrayList;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.logicea.model.Card;
import com.logicea.model.CardDTO;
import com.logicea.model.SuccessResponse;
import com.logicea.model.User;
import com.logicea.persistence.CardRepository;
import com.logicea.persistence.UserRepository;
import com.logicea.service.CardService;
import com.logicea.web.exception.CardNotFoundException;
import com.logicea.web.exception.DataValidationException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
public class CardServiceTest {
    
    final String token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJmcmFuY2lzY28iLCJleHAiOjE2NTk2MDgwNzksImlhdCI6MTY1OTU5MDA3OX0.pmmmfcgJjnUe4bJkVfi4FaAKVebxQruZiU5P9UUJXkl09xKD1EibunQYBc_EDJ6ozmLfp4n-OnH7AvEYkNrzfA";

    @InjectMocks
    private CardService cardService;

    @Mock
    private CardRepository cardRepository;

    @Mock
    private UserRepository userRepository;


    @Test
    public void whenGetAllUser_thenOK() {
        Iterable<Card> cards = new ArrayList<>();
        ((ArrayList<Card>) cards).add(new Card());

        given(cardRepository.findAll()).willReturn(cards);

        Iterable<CardDTO> expected = cardService.findAll();

        assertNotNull(expected);
        //assertEquals(expected, users);
        verify(cardRepository).findAll();
    }

    @Test
    public void givenCardObject_whenCreateCard_thenReturnSavedCard() throws Exception {
        
        Card card = createCard();
        User user = createUser();

        SuccessResponse expected = new SuccessResponse();

        expected.setId(card.getId());

        when(userRepository.findByEmail(user.getEmail())).thenReturn(user);
        when(cardRepository.save(ArgumentMatchers.any(Card.class))).thenReturn(card);

        CardDTO cardDTO = new CardDTO();  
        cardDTO.setName("color");
        cardDTO.setEmail("hola@mundo.com");
        SuccessResponse created = cardService.create(cardDTO);

        assertEquals(created.getName(), card.getName());
        //verify(userRepository).save(user);

    }


    @Test
    public void whenGivenId_shouldReturnCard_ifFound() {
        Card card = createCard();

        when(cardRepository.findById(card.getId())).thenReturn(Optional.of(card));

        CardDTO expected = cardService.findById(card.getId());

        assertEquals(expected.getName(), card.getName());
        verify(cardRepository).findById(card.getId());
    }

    @Test
    public void should_throw_exception_when_card_doesnt_exist()  {
        Card card = new Card();
        card.setId(20000L);

        given(cardRepository.findById(any())).willReturn(Optional.ofNullable(null));

        assertThrows(CardNotFoundException.class, () -> {             
            cardService.findById(20000L);
        });
    }

    @Test
    public void should_throw_exception_when_color_malformed()  {
        CardDTO card = new CardDTO();        
        card.setName("token");
        card.setColor("color");

        assertThrows(DataValidationException.class, () -> {             
            cardService.create(card);        
        });
    }
    
    private Card createCard() {

        Card card = Card.builder()
            .name("color")
            .description("good color")
            .color("#FFFFFF")
            .build();

        return card;
    }

    private User createUser() {

        User user = User.builder()
            .id(1l)
            .email("hola@mundo.com")
            .password("Aaaaa12")
            .build();

        return user;
    }
}
