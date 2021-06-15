package com.nubari.licensingservice.model;

import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "licenses")
public class License extends RepresentationModel<License> {
    @Id
    @Column(nullable = false)
    private String licenseId;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false)
    private String organizationId;
    @Column(nullable = false)
    private String productName;
    @Column(nullable = false)
    private String licenseType;
    private String comment;

    public License withComment(String comment) {
        this.setComment(comment);
        return this;
    }
}
