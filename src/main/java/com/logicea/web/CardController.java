package com.logicea.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.logicea.model.APIResponse;
import com.logicea.model.Card;
import com.logicea.model.CardDTO;
import com.logicea.model.CardSearchDTO;
import com.logicea.model.CardSpecificationBuilder;
import com.logicea.model.SearchCriteria;
import com.logicea.model.SuccessResponse;
import com.logicea.service.CardService;
import com.logicea.web.exception.DataValidationException;

@RestController
@RequestMapping("/api/cards")
public class CardController {
    
    @Autowired
    private CardService cardService;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SuccessResponse create(@RequestBody CardDTO cardDTO) {
        try {
            return cardService.create(cardDTO) ;
        } catch (Exception e) {
            if (e instanceof DataValidationException) {
                throw e;
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
            }            
        }
    }

    @DeleteMapping("/{id}/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable String id, @PathVariable String email) {
        try {
            cardService.delete(Long.parseLong(id), email);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PatchMapping
    @ResponseStatus(HttpStatus.OK)
    public SuccessResponse update(@RequestBody CardDTO cardDTO) {        
        try {
            return cardService.update(cardDTO) ;
        } catch (Exception e) {
            if (e instanceof DataValidationException) {
                throw e;
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
            }            
        }
    }


    @GetMapping
    public CardDTO findOne(@RequestBody CardDTO cardDTO) {
        try {            
            return cardService.findByIdAndUser(cardDTO);
        } catch (Exception e) {
            throw e;
        }
    }

    @GetMapping("/search")
    public ResponseEntity<APIResponse> searchCard(
        @RequestParam(name = "pageNum", defaultValue = "0") int pageNum,
        @RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
        @RequestBody CardSearchDTO cardSearchDTO) {
        
        APIResponse apiResponse = new APIResponse();
        CardSpecificationBuilder builder = new CardSpecificationBuilder();

        List<SearchCriteria> criteriaList = cardSearchDTO.getSearchCriteriaList();
        if(criteriaList != null){
            criteriaList.forEach(
                x-> {
                    x.setDataOption(cardSearchDTO.getDataOption());
                    builder.with(x);
            });
        }

        Pageable page = PageRequest.of(pageNum, pageSize, 
        Sort.by("name").ascending().and(
        Sort.by("description")).ascending().and(
        Sort.by("color")).ascending().and(
        Sort.by("status")).ascending().and(
        Sort.by("creationDate")).ascending());
        
        Page<Card> cardPage = cardService.findBySearchCriteria(builder.build(), page);
        
        apiResponse.setData(cardPage.toList());
        apiResponse.setResponseCode(HttpStatus.OK);
        apiResponse.setMessage("Successfully retrieved card record");

        return new ResponseEntity<>(apiResponse, apiResponse.getResponseCode());

    }

}
