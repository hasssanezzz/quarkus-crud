# Simple CRUD app with Quarkus

This API provides endpoints to perform CRUD (Create, Read, Update, Delete) operations on products and their images.

Additionally, the application is integrated with PostgreSQL using Docker containers. The Docker setup includes two images: one for PostgreSQL and one for the Quarkus application.

Comprehensive tests are implemented for each API route to ensure functionality and reliability.

## Documentation

The API documentation is available in the OpenAPI Specification (formerly Swagger) format and can be found by visiting the following URL after running the app: `<servr_url>/q/swagger-ui`

Please refer to the provided OpenAPI Specification file for detailed information about the API endpoints, request/response payloads, and schemas. This API follows the OpenAPI Specification version 3.0.1.

The database table schema is provided in a file named import.sql, located [here](./src/main/resources/import.sql). This file is executed by the PostgreSQL container during initialization to create the necessary tables.

## Schemas

### Product
Data element for product.

* `id` (string, uuid)
* `name` (string, 2-255 characters)
* `description` (string, 60-2000 characters)
* `price` (number, minimum value: 1)
* `stock` (number, minimum value: 0)

### ProductImg
Data element for product images.

* `id` (string, uuid)
* `product_id` (string, uuid)
* `img` (string, Image URL)

## Environment variables

To run the app, you need to have the following environment variables set up:

* `DB_URI`: Specifies the URI for the main database.
* `TEST_DB_URI`: Specifies the URI for the test database.
* `DB_NAME`: Specifies the name of the database.
* `DB_USERNAME`: Specifies the username for accessing the database.
* `DB_PASSWORD`: Specifies the password for accessing the database.
* `PG_VOLUME_PATH`: Specifies the path on the host machine where PostgreSQL database data will be stored in the container.

Ensure that these environment variables are configured correctly before running the application.

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
./mvnw compile quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at http://localhost:8080/q/dev/.

## Packaging and running the application

The application can be packaged using:

```shell script
./mvnw package
```

It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:

```shell script
./mvnw package -Dquarkus.package.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Creating a native executable

You can create a native executable using:

```shell script
./mvnw package -Dnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using:

```shell script
./mvnw package -Dnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/demo-1.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/maven-tooling.


