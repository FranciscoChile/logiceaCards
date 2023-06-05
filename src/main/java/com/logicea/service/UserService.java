package com.logicea.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.logicea.model.SuccessResponse;
import com.logicea.model.User;
import com.logicea.model.UserDTO;
import com.logicea.persistence.UserRepository;
import com.logicea.web.exception.DataValidationException;
import com.logicea.web.exception.UserNotFoundException;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;


    public List<UserDTO> findAll() {

        List<UserDTO> listDTO = new ArrayList<>();

        Iterable<User> list = userRepository.findAll();

        for (User user : list) {
            UserDTO userDTO = new UserDTO();
            userDTO.setEmail(user.getEmail());
            userDTO.setRole(user.getRole());
            listDTO.add(userDTO);
        }

        return listDTO;
    }

    public UserDTO findByEmail(String name) {

        User user = userRepository.findByEmail(name);
        UserDTO userDTO = new UserDTO();

        if (user != null) {
            userDTO.setEmail(user.getEmail());         
            userDTO.setRole(user.getRole());
        }            

        return userDTO;
    }

    public UserDTO findById(Long id) {

        UserDTO userDTO = new UserDTO();

        User user = userRepository.findById(id)
        .orElseThrow(UserNotFoundException::new);

        userDTO.setEmail(user.getEmail());
        userDTO.setRole(user.getRole());

        return userDTO;
    }

    public SuccessResponse create(UserDTO userDTO) {    
        SuccessResponse response = new SuccessResponse();
        User userEmail = userRepository.findByEmail(userDTO.getEmail());

        if (userEmail != null) {
            throw new DataValidationException("Email is already registered");
        }

        if (userDTO.getEmail() != null)
        if (!patternMatchesEmail(userDTO.getEmail())) {
            throw new DataValidationException("Wrong email format");
        }

        //mapping from DTO to Entity
        User user = new User();
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword());
        user.setRole(userDTO.getRole());

        userRepository.save(user);

        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setCreated(new Date());

        return response;

    }

    public static boolean patternMatchesEmail(String emailAddress) {

        String regexPattern = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@" 
        + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";

        return Pattern.compile(regexPattern)
          .matcher(emailAddress)
          .matches();
    }
    
}
