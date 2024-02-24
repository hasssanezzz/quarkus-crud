package com.example.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.UUID;

@Entity
@Table(name = "product_img")
public class ProductImg extends PanacheEntityBase {
    @Id
    @GeneratedValue(generator = "UUID")
    public UUID id;

    @ManyToOne
    @JsonBackReference
    public Product product;

    @NotBlank(message = "Image may not be blank")
    @Size(max = 2000, message = "Image URI may not exceed 2000 characters")
    @Pattern(regexp = "^(https?://)?([a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}(/[^/#?]+)+\\.(jpg|jpeg|png|gif|bmp|webp)$", message = "A valid image URL must be provided")
    public String img;
}
