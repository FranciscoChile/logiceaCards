package com.logicea.model;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SuccessResponse {
    
    private Long id;
    private String email;
    private String name;
    private Date created;
    private Date updated;

}
