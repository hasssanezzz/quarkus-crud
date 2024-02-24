package com.example.resources;

import com.example.entities.Product;
import com.example.entities.ProductImg;
import com.example.utils.ErrorResponse;
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

@Path("/api/product-imgs")
public class ProductsImgResource {

    @Inject
    Validator validator;


    @GET
    @Path("/{product_id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getItemById(@PathParam("product_id") UUID productId) {
        List<ProductImg> results = ProductImg.find("product.id", productId).list();
        return Response.ok(results).build();
    }

    @POST
    @Path("/{product_id}")
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createProduct(@PathParam("product_id") UUID productId, ProductImg productImg) {
        Product product = Product.findById(productId);

        if (product == null || productImg == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        // handle validation
        Set<ConstraintViolation<ProductImg>> violations = validator.validate(productImg);
        if (!violations.isEmpty()) {
            ErrorResponse errors = new ErrorResponse();
            for (ConstraintViolation<ProductImg> violation : violations) {
                errors.addError(violation.getPropertyPath().toString(), violation.getMessage());
            }
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(errors)
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        product.images.size(); // init the array list to avoid lazy loading issues

        // save the new product image
        productImg.product = product;
        productImg.persist();

        // make sure data is persisted
        if (productImg.isPersistent()) {
            return Response.created(URI.create("/api/products/" + productImg.product.id)).entity(productImg).build();
        }

        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deleteProduct(@PathParam("id") UUID id) {
        boolean deleted = ProductImg.deleteById(id);

        if (deleted) {
            return Response.noContent().build();
        }

        // if id matches no product
        ErrorResponse errors = new ErrorResponse();
        errors.addError("id", "Can not find a product image with the provided ID");
        return Response.status(Response.Status.BAD_REQUEST).entity(errors).build();
    }
}
