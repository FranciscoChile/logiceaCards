package com.logicea.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.logicea.model.SuccessResponse;
import com.logicea.model.UserDTO;
import com.logicea.service.UserService;
import com.logicea.web.exception.DataValidationException;
import com.logicea.web.exception.UserNotFoundException;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/all")
    public List<UserDTO> findAll() {
        return userService.findAll();
    }

    @GetMapping("/{email}")
    public UserDTO findOne(@PathVariable String email) {
        try {            
            return userService.findByEmail(email);
        } catch (Exception e) {
            throw new UserNotFoundException();
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SuccessResponse create(@RequestBody UserDTO user, @RequestHeader HttpHeaders headers) {
        try {
            return userService.create(user);
        } catch (Exception e) {
            if (e instanceof DataValidationException) {
                throw e;
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
            }            
        }
    }


}
