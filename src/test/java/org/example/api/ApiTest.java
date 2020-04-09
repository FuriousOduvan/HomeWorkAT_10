package org.example.api;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import org.example.model.Pet;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Random;

import static io.restassured.RestAssured.given;

public class ApiTest {
    @BeforeClass
    public void prepare() throws IOException {
        RestAssured.requestSpecification = new RequestSpecBuilder()
                .setBaseUri("https://petstore.swagger.io/v2/")
                .addHeader("api_key", "RuslanApiTest")
                .setAccept(ContentType.JSON)
                .setContentType(ContentType.JSON)
                .log(LogDetail.ALL)
                .build();
    }

    @Test
    public void checkObjectSave() {
        Pet pet = new Pet();
        int id = new Random().nextInt(500000);
        String name = "My_Pet_" + id;
        pet.setId(id);
        pet.setName(name);

        System.out.println("Создание питомца\n");

        given()
                .body(pet)
            .when()
                .post("/pet")
            .then()
                .statusCode(200);

        System.out.println("\nЗапрос информации о питомце\n");

        Pet actual =
                given()
                        .pathParam("petId", id)
                    .when()
                        .get("/pet/{petId}")
                    .then()
                        .statusCode(200)
                        .extract().body()
                        .as(Pet.class);

        Assert.assertEquals(actual.getName(), pet.getName());

        System.out.println("\nИзменение имени питомца\n");

        Pet newPet = new Pet();
        String newName = "My_new_name_pet_" + id;
        newPet.setId(id);
        newPet.setName(newName);

                given()
                        .body(newPet)
                        .when()
                        .put("/pet")
                        .then()
                        .statusCode(200);

        System.out.println("\nЗапрос информации с новым именем питомца\n");

        Pet newActual =
                given()
                        .pathParam("petId", id)
                        .when()
                        .get("/pet/{petId}")
                        .then()
                        .statusCode(200)
                        .extract().body()
                        .as(Pet.class);

        Assert.assertEquals(newActual.getName(), newPet.getName());

        System.out.println("\nУдаление информации о питомце.\n");

                given()
                        .pathParam("petId", id)
                        .when()
                        .delete("/pet/{petId}")
                        .then()
                        .statusCode(200)
                        .extract().body()
                        .as(Pet.class);

        System.out.println("\nЗапрос удаленной информации, должен вернуть 404\n");

                given()
                        .pathParam("petId", id)
                        .when()
                        .get("/pet/{petId}")
                        .then()
                        .statusCode(404)
                        .extract().body()
                        .as(Pet.class);
    }
}
