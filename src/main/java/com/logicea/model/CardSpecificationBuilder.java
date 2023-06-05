package com.logicea.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

public class CardSpecificationBuilder {
    
    private final List<SearchCriteria> params;

    public CardSpecificationBuilder(){
        this.params = new ArrayList<>();
    }

    public final CardSpecificationBuilder with(String key, String operation, Object value){
        params.add(new SearchCriteria(key, operation, value));
        return this;
    }

    public final CardSpecificationBuilder with(SearchCriteria searchCriteria){
        params.add(searchCriteria);
        return this;
    }

    public Specification<Card> build(){
        if(params.size() == 0){
            return null;
        }

        Specification<Card> result = new CardSpecification(params.get(0));
        for (int idx = 1; idx < params.size(); idx++){
            SearchCriteria criteria = params.get(idx);
            result = SearchOperation.getDataOption(criteria.getDataOption()) == SearchOperation.ALL
                    ? Specification.where(result).and(new CardSpecification(criteria))
                    : Specification.where(result).or(new CardSpecification(criteria));
        }

        return result;
    }
    
}
