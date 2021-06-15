package com.nubari.licensingservice.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Data
public class Organization {
    String id;
    String name;
    String contactName;
    String contactEmail;
    String contactPhone;


}
