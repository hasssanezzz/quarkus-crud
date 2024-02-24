package com.example;

import com.example.entities.Product;
import com.example.entities.ProductImg;
import com.example.resources.ProductsImgResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static io.restassured.RestAssured.given;

@QuarkusTest
@TestHTTPEndpoint(ProductsImgResource.class)
class ProductImgResourceTest {


    static Product product;
    static UUID productImgId;

    @BeforeAll
    static void setup() {
        // create 3 test products
        createTestData();
    }

    @Transactional
    static void createTestData() {
        Product testProduct = new Product();
        testProduct.name = "Gaming mouse";
        testProduct.description = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec quam felis, ultricies nec, pellentesque eu, pretium quis, sem. Nulla consequat massa quis enim. Donec pede justo.";
        testProduct.price = 90L;
        testProduct.stock = 5L;

        testProduct.persist();
        product = testProduct;

        // Create a test product image
        ProductImg testImage = new ProductImg();
        testImage.img = "https://some-cdn.io/file.png";
        testImage.product = testProduct;
        testImage.persist();
        productImgId = testImage.id;
    }

    @AfterAll
    @Transactional
    static void teardown() {
        Product.deleteAll();
    }

    @Test
    void testGetProductImgsEndpoint() {
        given()
                .pathParam("product_id", product.id)
                .when()
                .get("/{product_id}")
                .then()
                .statusCode(Response.Status.OK.getStatusCode());
    }

    @Test
    void testCreateProductImgEndpoint() {
        String requestBody = "{\"img\": \"some-cdn.io/file.png\"}";

        // store the number of records before and after creation
        long productsCount = ProductImg.count();
        given()
                .pathParam("product_id", product.id)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/{product_id}")
                .then()
                .statusCode(Response.Status.CREATED.getStatusCode());
        long newProductsCount = ProductImg.count();


        Assertions.assertSame(productsCount + 1, newProductsCount);
    }

    @Test
    void testCreateInvalidProductImg() {
        String requestBody = "{\"img\": \"some-cdn.io/file\"}";
        given()
                .pathParam("product_id", product.id)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/{product_id}")
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    void testCreateProductImgByBadId() {
        String requestBody = "{\"img\": \"some-cdn.io/file.png\"}";
        given()
                .pathParam("product_id", UUID.randomUUID())
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/{product_id}")
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode());
    }


    @Test
    void testDeleteProductImgByIdEndpoint() {
        // store the number of records before and after deletion
        long productsCount = ProductImg.count();
        given()
                .pathParam("product_id", productImgId)
                .when()
                .delete("/{product_id}")
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());
        long newProductsCount = ProductImg.count();

        // make sure a record is deleted
        Assertions.assertSame(productsCount - 1, newProductsCount);
    }

    @Test
    void testDeleteProductImgByBadId() {
        // test a bad id
        given()
                .pathParam("id", UUID.randomUUID())
                .when()
                .delete("/{id}")
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode());
    }
}