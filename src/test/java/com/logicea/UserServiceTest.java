package com.logicea;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.logicea.model.Card;
import com.logicea.model.SuccessResponse;
import com.logicea.model.User;
import com.logicea.model.UserDTO;
import com.logicea.persistence.UserRepository;
import com.logicea.service.UserService;
import com.logicea.web.exception.DataValidationException;
import com.logicea.web.exception.UserNotFoundException;


@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    
final String token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJmcmFuY2lzY28iLCJleHAiOjE2NTk2MDgwNzksImlhdCI6MTY1OTU5MDA3OX0.pmmmfcgJjnUe4bJkVfi4FaAKVebxQruZiU5P9UUJXkl09xKD1EibunQYBc_EDJ6ozmLfp4n-OnH7AvEYkNrzfA";

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;
    
    @Test
    public void whenGetAllUser_thenOK() {
        Iterable<User> users = new ArrayList<>();
        ((ArrayList<User>) users).add(new User());

        given(userRepository.findAll()).willReturn(users);

        Iterable<UserDTO> expected = userService.findAll();

        //assertEquals(expected, users);
        verify(userRepository).findAll();
    }

    @Test
    public void givenUserObject_whenCreateUser_thenReturnSavedUser() throws Exception {
        
        User user = createUser();
        
        SuccessResponse expected = new SuccessResponse();

        expected.setId(user.getId());

        when(userRepository.save(ArgumentMatchers.any(User.class))).thenReturn(user);

        UserDTO userDTO = new UserDTO();  
        userDTO.setEmail("hola@mundo.com");      
        SuccessResponse created = userService.create(userDTO);

        assertEquals(created.getEmail(), user.getEmail());
        //verify(userRepository).save(user);

    }

    @Test
    public void whenGivenName_shouldReturnUser_ifFound() {
        User user = createUser();
        

        when(userRepository.findByEmail(user.getEmail())).thenReturn(user);

        UserDTO expected = userService.findByEmail(user.getEmail());

        assertEquals(expected.getEmail(), user.getEmail());
        verify(userRepository).findByEmail(user.getEmail());
    }


    @Test
    public void whenGivenId_shouldReturnUser_ifFound() {
        User user = createUser();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        UserDTO expected = userService.findById(user.getId());

        assertEquals(expected.getEmail(), user.getEmail());
        verify(userRepository).findById(user.getId());
    }
    

    @Test
    public void givenInvalidName_whenSearch_thenRetunEmpty() {
        given(userRepository.findByEmail("hi@mundo.com")).willReturn(null);
        
        UserDTO expected = userService.findByEmail("hi@mundo.com");
        
        assertNull(expected.getEmail());
    }

    @Test
    public void should_throw_exception_when_user_doesnt_exist()  {
        User user = new User();
        user.setId(2l);

        given(userRepository.findById(any())).willReturn(Optional.ofNullable(null));

        assertThrows(UserNotFoundException.class, () -> {             
            userService.findById(2l);        
        });
    }

    @Test
    public void should_throw_exception_when_email_malformed()  {
        UserDTO user = new UserDTO();        
        user.setEmail("email");

        assertThrows(DataValidationException.class, () -> {             
            userService.create(user);        
        });
    }
    
    private User createUser() {

        Card card = Card.builder()
            .name("")
            .description("token")
            .build();

            Set<Card> s = new HashSet<>();
            s.add(card);

        User user = User.builder()
            .id(1l)
            .email("hola@mundo.com")
            .password("Aaaaa12")
            .build();

        return user;
    }
}