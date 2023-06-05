package com.logicea.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.logicea.model.Card;
import com.logicea.model.CardDTO;
import com.logicea.model.Role;
import com.logicea.model.Status;
import com.logicea.model.SuccessResponse;
import com.logicea.model.User;
import com.logicea.persistence.CardRepository;
import com.logicea.persistence.UserRepository;
import com.logicea.web.exception.CardNotFoundException;
import com.logicea.web.exception.DataValidationException;

@Service
public class CardService {
    
    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private UserRepository userRepository;
    
    public SuccessResponse create(CardDTO cardDTO) {    
        SuccessResponse response = new SuccessResponse();
        String cardName = cardDTO.getName();
        
        //name mandatory
        if (cardName == null) {
            throw new DataValidationException("Card name is empty");
        }

        //color format validation
        if (cardDTO.getColor() != null)
        if (!patternMatchesColor(cardDTO.getColor())) {
            throw new DataValidationException("Wrong color format");
        }

        //mapping from DTO to Entity
        Card card = new Card();
        card.setName(cardDTO.getName());
        card.setColor(cardDTO.getColor()==null ? "" : cardDTO.getColor());
        card.setDescription(cardDTO.getDescription() == null ? "" : cardDTO.getDescription());
        card.setCreationDate(new Timestamp(System.currentTimeMillis()));
        //status is To Do by default
        card.setStatus(Status.TODO.getStatus());

        User user = new User();

        if (cardDTO.getEmail() != null) {
             user = userRepository.findByEmail(cardDTO.getEmail());
             
             if (user == null) {
                throw new DataValidationException("User Email is invalid");    
             }
        } else {
            throw new DataValidationException("User Email is unknow");
        }

        card.setUser(user);
        cardRepository.save(card);        

        response.setId(card.getId());
        response.setCreated(card.getCreationDate());


        return response;

    }

    public void delete(Long id, String email) {

        User user = userRepository.findByEmail(email);
        
        cardRepository.findById(id)
        .orElseThrow(CardNotFoundException::new);

        if (user.getRole().equals(Role.Admin.toString())) {
            cardRepository.deleteById(id);
        }
        else {
            if (user.getEmail().equals(email)) {
                cardRepository.deleteById(id);   
            }
        }
        
    }
 
    public SuccessResponse update(CardDTO cardDTO) {
        SuccessResponse response = new SuccessResponse();

        Card card = cardRepository.findById(cardDTO.getId())
        .orElseThrow(CardNotFoundException::new);

        User user = userRepository.findByEmail(cardDTO.getEmail());

        if (cardDTO.getEmail() == null) {
            throw new DataValidationException("User not specified");
        }

        if (user == null) {
            throw new DataValidationException("User not specified");
        }

        if (cardDTO.getEmail() != null) {
            if (!card.getUser().getEmail().equals(cardDTO.getEmail())) {
                if (!user.getRole().equals(Role.Admin.toString())) {
                    throw new DataValidationException("User not allowed to modify card");
                }
                
            }            
        } 

        if (cardDTO.getStatus() != null) {
            if (!cardDTO.getStatus().equals(Status.TODO.toString()) ||
            !cardDTO.getStatus().equals(Status.DONE.toString()) ||
            !cardDTO.getStatus().equals(Status.INPROGRESS.toString()))
            throw new DataValidationException("Status not allowed");
        }

        if (cardDTO.getName() != null) card.setName(cardDTO.getName());
        if (cardDTO.getDescription() != null) card.setDescription(cardDTO.getDescription());
        if (cardDTO.getColor() != null) card.setColor(cardDTO.getColor());
        if (cardDTO.getStatus() != null) card.setStatus(cardDTO.getStatus());
        cardRepository.save(card);

        response.setId(card.getId());
        response.setUpdated(new Date());

        return response;

    }

    public CardDTO findById(Long id) {

        CardDTO cardDTO = new CardDTO();

        Card card = cardRepository.findById(id)
        .orElseThrow(CardNotFoundException::new);

        cardDTO.setColor(card.getColor());
        cardDTO.setDescription(card.getDescription());
        cardDTO.setName(card.getName());
        cardDTO.setStatus(card.getStatus());
        cardDTO.setEmail(card.getUser().getEmail());

        return cardDTO;
    }
    
    public CardDTO findByIdAndUser(CardDTO cardDTO) {

        Card card = cardRepository.findById(cardDTO.getId())
        .orElseThrow(CardNotFoundException::new);

        CardDTO returnCardDTO = new CardDTO();

        if (card.getUser().getEmail().equals(cardDTO.getEmail())) {
            returnCardDTO.setId(card.getId());
            returnCardDTO.setColor(card.getColor());
            returnCardDTO.setDescription(card.getDescription());
            returnCardDTO.setName(card.getName());
            returnCardDTO.setStatus(card.getStatus());
            returnCardDTO.setEmail(card.getUser().getEmail());
        } else {
            throw new DataValidationException("User not allowed to that card");
        }

        return returnCardDTO;
    }

    public List<CardDTO> findAll(CardDTO cardDTO) {

        List<CardDTO> listDTO = new ArrayList<>();

        Iterable<Card> list = cardRepository.findAll();

        for (Card card : list) {
            CardDTO cardDTOMapper = new CardDTO();
            cardDTOMapper.setName(card.getName());
            cardDTOMapper.setDescription(card.getDescription());
            cardDTOMapper.setColor(card.getColor());
            cardDTOMapper.setStatus(card.getStatus());
            cardDTOMapper.setEmail(card.getUser().getEmail());
            listDTO.add(cardDTOMapper);
        }

        return listDTO;
    }

    public Page<Card> findBySearchCriteria(Specification<Card> spec, Pageable page) {        
        Page<Card> searchResult = cardRepository.findAll(spec, page);
        return searchResult;
    }


    public static boolean patternMatchesColor(String color) {

        String regexPattern = "^#[a-zA-Z0-9]{6}$";

        return Pattern.compile(regexPattern)
          .matcher(color)
          .matches();
    }

}
