package com.example.interfaces.web;

import com.example.core.entities.Product;
import com.example.core.exceptions.ErrorResponse;
import io.quarkus.panache.common.Sort;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Path("/api/products")
public class ProductsResource {

    @Inject
    Validator validator;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll() {
        List<Product> results = Product.listAll(Sort.by("name"));
        return Response.ok(results).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getItemById(@PathParam("id") UUID id) {
        return Product.findByIdOptional(id)
                .map(product -> Response.ok(product).build())
                .orElseGet(() -> {
                    // if id matches no product
                    ErrorResponse errors = new ErrorResponse();
                    errors.addError("id", "Can not find a product with the provided ID");
                    return Response.status(Response.Status.NOT_FOUND).entity(errors).build();
                });
    }

    @POST
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createProduct(Product product) {

        if (product == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        // handle validation
        Set<ConstraintViolation<Product>> violations = validator.validate(product);
        if (!violations.isEmpty()) {
            ErrorResponse errors = new ErrorResponse();
            for (ConstraintViolation<Product> violation : violations) {
                errors.addError(violation.getPropertyPath().toString(), violation.getMessage());
            }
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(errors)
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }


        product.persist();

        // make sure data is persisted
        if (product.isPersistent()) {
            return Response.created(URI.create("/api/products/" + product.id)).entity(product).build();
        }

        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateProduct(@PathParam("id") UUID id, Product updatedProduct) {
        Product product = Product.findById(id);

        if (updatedProduct == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        // if id matches no product
        if (product == null) {
            ErrorResponse errors = new ErrorResponse();
            errors.addError("id", "Can not find a product with the provided ID");
            return Response.status(Response.Status.NOT_FOUND).entity(errors).build();
        }

        // handle validation
        Set<ConstraintViolation<Product>> violations = validator.validate(updatedProduct);
        if (!violations.isEmpty()) {
            ErrorResponse errors = new ErrorResponse();
            for (ConstraintViolation<Product> violation : violations) {
                if (violation.getInvalidValue() != null && !violation.getInvalidValue().equals(""))
                    errors.addError(violation.getPropertyPath().toString(), violation.getMessage());
            }

            if (errors.getErrorCount() > 0) {
                return Response
                        .status(Response.Status.BAD_REQUEST)
                        .entity(errors)
                        .type(MediaType.APPLICATION_JSON)
                        .build();
            }
        }


        if (updatedProduct.name != null && !updatedProduct.name.isEmpty())
            product.name = updatedProduct.name;

        if (updatedProduct.description != null && !updatedProduct.description.isEmpty())
            product.description = updatedProduct.description;

        if (updatedProduct.price != null)
            product.price = updatedProduct.price;

        if (updatedProduct.stock != null)
            product.stock = updatedProduct.stock;

        product.images.size(); // init the array list to avoid lazy loading issues
        product.persist();

        // make sure data is persisted
        if (product.isPersistent()) {
            return Response.created(URI.create("/api/products/" + id)).entity(product).build();
        }

        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deleteProduct(@PathParam("id") UUID id) {
        boolean deleted = Product.deleteById(id);

        if (deleted) {
            return Response.noContent().build();
        }

        // if id matches no product
        ErrorResponse errors = new ErrorResponse();
        errors.addError("id", "Can not find a product with the provided ID");
        return Response.status(Response.Status.BAD_REQUEST).entity(errors).build();
    }
}
