package com.logicea.persistence;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import com.logicea.model.Card;

public interface CardRepository extends CrudRepository<Card, Long>, 
    JpaSpecificationExecutor<Card> {
    
}
