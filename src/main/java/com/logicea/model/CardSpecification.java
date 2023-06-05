package com.logicea.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

public class CardSpecification implements Specification<Card> {
    
    private final SearchCriteria searchCriteria;

    public CardSpecification(final SearchCriteria searchCriteria){
        super();
        this.searchCriteria = searchCriteria;
    }

    @Override
    public Predicate toPredicate(Root<Card> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        String strToSearch = searchCriteria.getValue().toString().toLowerCase();

        switch(Objects.requireNonNull(SearchOperation.getSimpleOperation(searchCriteria.getOperation()))){
            case CONTAINS:
                if(searchCriteria.getFilterKey().equals("email")){
                    return cb.like(cb.lower(userJoin(root).<String>get(searchCriteria.getFilterKey())), "%" + strToSearch + "%");
                }
                return cb.like(cb.lower(root.get(searchCriteria.getFilterKey())), "%" + strToSearch + "%");

            case DOES_NOT_CONTAIN:
                if(searchCriteria.getFilterKey().equals("email")){
                    return cb.notLike(cb.lower(userJoin(root).<String>get(searchCriteria.getFilterKey())), "%" + strToSearch + "%");
                }
                return cb.notLike(cb.lower(root.get(searchCriteria.getFilterKey())), "%" + strToSearch + "%");

            case BEGINS_WITH:
                if(searchCriteria.getFilterKey().equals("email")){
                    return cb.like(cb.lower(userJoin(root).<String>get(searchCriteria.getFilterKey())), strToSearch + "%");
                }
                return cb.like(cb.lower(root.get(searchCriteria.getFilterKey())), strToSearch + "%");

            case DOES_NOT_BEGIN_WITH:
                if(searchCriteria.getFilterKey().equals("email")){
                    return cb.notLike(cb.lower(userJoin(root).<String>get(searchCriteria.getFilterKey())), strToSearch + "%");
                }
                return cb.notLike(cb.lower(root.get(searchCriteria.getFilterKey())), strToSearch + "%");

            case ENDS_WITH:
                if(searchCriteria.getFilterKey().equals("email")){
                    return cb.like(cb.lower(userJoin(root).<String>get(searchCriteria.getFilterKey())), "%" + strToSearch);
                }
                return cb.like(cb.lower(root.get(searchCriteria.getFilterKey())), "%" + strToSearch);

            case DOES_NOT_END_WITH:
                if(searchCriteria.getFilterKey().equals("email")){
                    return cb.notLike(cb.lower(userJoin(root).<String>get(searchCriteria.getFilterKey())), "%" + strToSearch);
                }
                return cb.notLike(cb.lower(root.get(searchCriteria.getFilterKey())), "%" + strToSearch);

            case EQUAL:
                if(searchCriteria.getFilterKey().equals("email")){
                    return cb.equal(userJoin(root).<String>get(searchCriteria.getFilterKey()), searchCriteria.getValue());
                }

                if (searchCriteria.getFilterKey().equals("creationDate")) {
                    String pattern = "yyyy-MM-dd";
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                    try {
                        Date date = simpleDateFormat.parse(searchCriteria.getValue().toString());
                        return cb.greaterThan(root.<Date> get(searchCriteria.getFilterKey()), date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }                    
                }                
                
                return cb.equal(root.get(searchCriteria.getFilterKey()), searchCriteria.getValue());

            case NOT_EQUAL:
                if(searchCriteria.getFilterKey().equals("email")){
                    return cb.notEqual(userJoin(root).<String>get(searchCriteria.getFilterKey()), searchCriteria.getValue() );
                }
                return cb.notEqual(root.get(searchCriteria.getFilterKey()), searchCriteria.getValue());

            case NUL:
                return cb.isNull(root.get(searchCriteria.getFilterKey()));

            case NOT_NULL:
                return cb.isNotNull(root.get(searchCriteria.getFilterKey()));

            case GREATER_THAN:

                if (searchCriteria.getFilterKey().equals("creationDate")) {
                    String pattern = "yyyy-MM-dd";
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                    try {
                        Date date = simpleDateFormat.parse(searchCriteria.getValue().toString());
                        return cb.greaterThan(root.<Date> get(searchCriteria.getFilterKey()), date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }                    
                }

                return cb.greaterThan(root.<String> get(searchCriteria.getFilterKey()), searchCriteria.getValue().toString());

            case GREATER_THAN_EQUAL:

                if (searchCriteria.getFilterKey().equals("creationDate")) {
                    String pattern = "yyyy-MM-dd";
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                    try {
                        Date date = simpleDateFormat.parse(searchCriteria.getValue().toString());
                        return cb.greaterThanOrEqualTo(root.<Date> get(searchCriteria.getFilterKey()), date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }                    
                }

                return cb.greaterThanOrEqualTo(root.<String> get(searchCriteria.getFilterKey()), searchCriteria.getValue().toString());

            case LESS_THAN:
                if (searchCriteria.getFilterKey().equals("creationDate")) {
                    String pattern = "yyyy-MM-dd";
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                    try {
                        Date date = simpleDateFormat.parse(searchCriteria.getValue().toString());
                        return cb.lessThan(root.<Date> get(searchCriteria.getFilterKey()), date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }                    
                }

                return cb.lessThan(root.<String> get(searchCriteria.getFilterKey()), searchCriteria.getValue().toString());

            case LESS_THAN_EQUAL:
                if (searchCriteria.getFilterKey().equals("creationDate")) {
                    String pattern = "yyyy-MM-dd";
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                    try {
                        Date date = simpleDateFormat.parse(searchCriteria.getValue().toString());
                        return cb.lessThanOrEqualTo(root.<Date> get(searchCriteria.getFilterKey()), date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }                    
                }

                return cb.lessThanOrEqualTo(root.<String> get(searchCriteria.getFilterKey()), searchCriteria.getValue().toString());
        }
        return null;
    }

    private Join<Card, User> userJoin(Root<Card> root){
        return root.join("user");

    }

}
