package com.example.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "product")
public class Product extends PanacheEntityBase {

    @Id
    @GeneratedValue(generator = "UUID")
    public UUID id;

    @NotBlank(message = "Name may not be blank")
    @Size(min = 2, max = 255, message = "Name must be from 2 to 255 characters")
    public String name;

    @NotBlank(message = "Description may not be blank")
    @Size(min = 60, max = 2000, message = "Description must be from 60 to 2000 characters")
    public String description;

    @NotNull(message = "Product price must be provided")
    @Min(value = 1, message = "Price must be greater than 0")
    public Long price;

    @NotNull(message = "Product stock must be provided")
    @Min(value = 0, message = "Stock must be greater than or equal to 0")
    public Long stock;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    public List<ProductImg> images;
}

