package com.logicea.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardDTO implements Serializable {
    
    @JsonProperty(access = Access.WRITE_ONLY)
    private Long id;
    
    private String name;
    private String description;
    private String color;
    private String status;
    private String email;   
    private String creationDate; 

}
