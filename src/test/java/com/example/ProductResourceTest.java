package com.example;

import com.example.core.entities.Product;
import com.example.interfaces.web.ProductsResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
@TestHTTPEndpoint(ProductsResource.class)
class ProductResourceTest {


    static List<Product> products;

    @BeforeAll
    static void setup() {
        // create 3 test products
        products = new ArrayList<>();
        createTestData();
    }

    @Transactional
    static void createTestData() {
        Product testProduct1 = new Product();
        testProduct1.name = "Gaming mouse";
        testProduct1.description = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec quam felis, ultricies nec, pellentesque eu, pretium quis, sem. Nulla consequat massa quis enim. Donec pede justo.";
        testProduct1.price = 90L;
        testProduct1.stock = 5L;

        Product testProduct2 = new Product();
        testProduct2.name = "Gaming mouse pad";
        testProduct2.description = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec quam felis, ultricies nec, pellentesque eu, pretium quis, sem. Nulla consequat massa quis enim. Donec pede justo.";
        testProduct2.price = 15L;
        testProduct2.stock = 20L;

        Product testProduct3 = new Product();
        testProduct3.name = "Gaming keyboard";
        testProduct3.description = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec quam felis, ultricies nec, pellentesque eu, pretium quis, sem. Nulla consequat massa quis enim. Donec pede justo.";
        testProduct3.price = 890L;
        testProduct3.stock = 4L;

        testProduct1.persist();
        testProduct2.persist();
        testProduct3.persist();

        // preserve product
        products.add(testProduct1);
        products.add(testProduct2);
        products.add(testProduct3);
    }

    @AfterAll
    @Transactional
    static void teardown() {
        Product.deleteAll();
    }

    @Test
    void testGetAllEndpoint() {
        given()
                .when()
                .get("/")
                .then()
                .statusCode(Response.Status.OK.getStatusCode());
    }

    @Test
    void testGetProductByIdEndpoint() {
        // test a valid id
        given()
                .pathParam("id", products.get(0).id)
                .when()
                .get("/{id}")
                .then()
                .statusCode(Response.Status.OK.getStatusCode());
    }

    @Test
    void testGetProductByBadId() {
        // test a bad id
        given()
                .pathParam("id", UUID.randomUUID())
                .when()
                .get("/{id}")
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    void testCreateProductEndpoint() {
        String requestBody = "{\"name\":\"Headphones\",\"description\":\"Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec quam felis, ultricies nec, pellentesque eu, pretium quis, sem. Nulla consequat massa quis enim. Donec pede justo.\",\"price\":250,\"stock\":10}";

        // store the number of records before and after creation
        long productsCount = Product.count();
        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/")
                .then()
                .statusCode(Response.Status.CREATED.getStatusCode())
                .body("name", is("Headphones"))
                .body("price", is(250))
                .body("stock", is(10));
        long newProductsCount = Product.count();


        Assertions.assertSame(productsCount + 1, newProductsCount);
    }

    @Test
    void testNameColumnValidation() {
        // test a body with no name
        String noNameRequestBody = "{\"description\":\"Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec quam felis, ultricies nec, pellentesque eu, pretium quis, sem. Nulla consequat massa quis enim. Donec pede justo.\",\"price\":250,\"stock\":10}";
        given()
                .contentType(ContentType.JSON)
                .body(noNameRequestBody)
                .when()
                .post("/")
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .body("errorCount", is(1));

        // test a body with a short name
        String shortNameRequestBody = "{\"name\":\"x\",\"description\":\"Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec quam felis, ultricies nec, pellentesque eu, pretium quis, sem. Nulla consequat massa quis enim. Donec pede justo.\",\"price\":250,\"stock\":10}";
        // when creating a new product
        given()
                .contentType(ContentType.JSON)
                .body(shortNameRequestBody)
                .when()
                .post("/")
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .body("errorCount", is(1));
        // when updating a product
        given()
                .pathParam("id", products.get(2).id)
                .contentType(ContentType.JSON)
                .body(shortNameRequestBody)
                .when()
                .put("/{id}")
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .body("errorCount", is(1));

        // test a body with long name
        String longNameRequestBody = "{\"name\":\"xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx\",\"description\":\"Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec quam felis, ultricies nec, pellentesque eu, pretium quis, sem. Nulla consequat massa quis enim. Donec pede justo.\",\"price\":250,\"stock\":10}";
        // when creating a new product
        given()
                .contentType(ContentType.JSON)
                .body(longNameRequestBody)
                .when()
                .post("/")
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .body("errorCount", is(1));
        // when updating a product
        given()
                .pathParam("id", products.get(2).id)
                .contentType(ContentType.JSON)
                .body(longNameRequestBody)
                .when()
                .put("/{id}")
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .body("errorCount", is(1));
    }

    @Test
    void testDescriptionColumnValidation() {
        // test a body with no description
        String noDescriptionRequestBody = "{\"name\":\"Headphones\",\"price\":250,\"stock\":10}";
        given()
                .contentType(ContentType.JSON)
                .body(noDescriptionRequestBody)
                .when()
                .post("/")
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .body("errorCount", is(1));

        // test a body with a short description
        String shortDescriptionRequestBody = "{\"name\":\"Headphones\",\"description\":\"x\",\"price\":250,\"stock\":10}";
        // when creating a new product
        given()
                .contentType(ContentType.JSON)
                .body(shortDescriptionRequestBody)
                .when()
                .post("/")
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .body("errorCount", is(1));
        // when updating a product
        given()
                .pathParam("id", products.get(2).id)
                .contentType(ContentType.JSON)
                .body(shortDescriptionRequestBody)
                .when()
                .put("/{id}")
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .body("errorCount", is(1));
    }

    @Test
    void testPriceColumnValidation() {
        // test a body with no price
        String noPriceRequestBody = "{\"name\":\"Headphones\",\"description\":\"Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec quam felis, ultricies nec, pellentesque eu, pretium quis, sem. Nulla consequat massa quis enim. Donec pede justo.\",\"stock\":10}";
        given()
                .contentType(ContentType.JSON)
                .body(noPriceRequestBody)
                .when()
                .post("/")
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .body("errorCount", is(1));


        // test a body with invalid price (negative value)
        String invalidPriceRequestBody = "{\"name\":\"Headphones\",\"description\":\"Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec quam felis, ultricies nec, pellentesque eu, pretium quis, sem. Nulla consequat massa quis enim. Donec pede justo.\",\"price\":-1,\"stock\":10}";
        // when creating a new product
        given()
                .contentType(ContentType.JSON)
                .body(invalidPriceRequestBody)
                .when()
                .post("/")
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .body("errorCount", is(1));
        // when updating a product
        given()
                .pathParam("id", products.get(2).id)
                .contentType(ContentType.JSON)
                .body(invalidPriceRequestBody)
                .when()
                .put("/{id}")
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .body("errorCount", is(1));
    }

    @Test
    void testStockColumnValidation() {
        // test a body with no stock
        String noStockRequestBody = "{\"name\":\"Headphones\",\"description\":\"Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec quam felis, ultricies nec, pellentesque eu, pretium quis, sem. Nulla consequat massa quis enim. Donec pede justo.\",\"price\":250}";
        given()
                .contentType(ContentType.JSON)
                .body(noStockRequestBody)
                .when()
                .post("/")
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .body("errorCount", is(1));


        // test a body with invalid stock (negative value)
        String invalidStockRequestBody = "{\"name\":\"Headphones\",\"description\":\"Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec quam felis, ultricies nec, pellentesque eu, pretium quis, sem. Nulla consequat massa quis enim. Donec pede justo.\",\"price\":250,\"stock\":-1}";
        // when creating a new product
        given()
                .contentType(ContentType.JSON)
                .body(invalidStockRequestBody)
                .when()
                .post("/")
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .body("errorCount", is(1));
        // when updating a product
        given()
                .pathParam("id", products.get(2).id)
                .contentType(ContentType.JSON)
                .body(invalidStockRequestBody)
                .when()
                .put("/{id}")
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .body("errorCount", is(1));
    }

    @Test
    void testCreateInvalidProduct() {
        // invalid values: description, stock
        String invalidRequestBody = "{\"name\":\"Headphones\",\"description\":\"short description\",\"price\":250,\"stock\":-10}";

        given()
                .contentType(ContentType.JSON)
                .body(invalidRequestBody)
                .when()
                .post("/")
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .body("errorCount", is(2)); // 2 invalid values were provided
    }

    @Test
    void testUpdateProductEndpoint() {
        // edit name and stock values
        String requestBody = "{\"name\":\"Gaming Headphones\",\"stock\":60}";

        given()
                .pathParam("id", products.get(0).id)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .put("/{id}")
                .then()
                .statusCode(Response.Status.CREATED.getStatusCode())
                .body("name", is("Gaming Headphones"))
                .body("stock", is(60));
    }

    @Test
    void testInvalidUpdateProduct() {
        // invalid values: name, stock
        String invalidRequestBody = "{\"name\":\"x\",\"stock\":-60}";

        given()
                .pathParam("id", products.get(1).id)
                .contentType(ContentType.JSON)
                .body(invalidRequestBody)
                .when()
                .put("/{id}")
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .body("errorCount", is(2)); // 2 invalid values were provided
    }

    @Test
    void testUpdateProductByBadId() {
        // sample request body
        String requestBody = "{\"name\":\"Gaming Headphones\",\"stock\":60}";

        given()
                .pathParam("id", UUID.randomUUID())
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .put("/{id}")
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    void testDeleteProductByIdEndpoint() {
        // store the number of records before and after deletion
        long productsCount = Product.count();
        given()
                .pathParam("id", products.get(0).id)
                .when()
                .delete("/{id}")
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());
        long newProductsCount = Product.count();

        // make sure a record is deleted
        Assertions.assertSame(productsCount - 1, newProductsCount);
    }

    @Test
    void testDeleteProductByBadId() {
        // test a bad id
        given()
                .pathParam("id", UUID.randomUUID())
                .when()
                .delete("/{id}")
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode());
    }
}