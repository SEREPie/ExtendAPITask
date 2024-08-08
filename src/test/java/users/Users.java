package users;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.CodeLanguage;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.HashMap;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;



public class Users {
    ExtentSparkReporter sparkReporter;
    ExtentReports extent;
    RequestSpecification req;


    @BeforeTest
    public void setUp(){
        req = given().baseUri("https://reqres.in");

        sparkReporter = new ExtentSparkReporter("Report.html");
        extent = new ExtentReports();
        extent.attachReporter(sparkReporter);
    }


    @Test
    public void getUsersInPage2() {

        ExtentTest test = extent.createTest("Get Users In Page 2",
                "The data of the users should be correct.");
        Response response = given()
                .spec(req)
                .when()
                .get("/api/users/2");

        String usersNum = given()
                .spec(req)
                .when()
                .get("/api/users?page=2")
                .then().extract().response().path("per_page").toString();
        ;

        int perPage = Integer.parseInt(usersNum);

        try{
            given()
                    .spec(req)
                    .when()
                    .get("/api/users?page=2")
                    .then()
                    .statusCode(200) // Assert that the status code is 200
                    .time(lessThan(3000L)) // Assert that the response time is less than 3000ms

                    .assertThat().body(
                            "page", equalTo(2), // Assert that the page number is 2
                            "per_page", equalTo(6), // Assert that the number of users per page is 6
                            "total", equalTo(12), // Assert that the total number of users is 12
                            "total_pages", equalTo(2), // Assert that the total number of pages is 2
                            "data[1]", hasEntry("id", 8), // Assert that the second user has id 8
                            "data[1].first_name", equalTo("Lindsay"), // Assert that the second user's first name is Lindsay
                            "data[1].email", containsString("lindsay"), // Assert that the second user's email contains "lindsay"
                            "data.size()", equalTo(perPage) // Assert that the number of users matches the "per_page" value
                    ).log().all()
            ;
            // Log the success in the report
            test.pass("The response has correct users data");
            test.pass("The response status code is 200");
            test.pass("The response time is less than 3000ms");
            test.pass(MarkupHelper.createCodeBlock(response.prettyPrint(), CodeLanguage.JSON));


        }catch (AssertionError e){
            // Log any assertion failures
            test.fail("Assertion Failed");
            test.fail(e);
            test.fail(MarkupHelper.createCodeBlock(response.prettyPrint(), CodeLanguage.JSON));
        } catch (Exception e){
            // Log any unexpected exceptions
            test.fail("Exception Error");
            test.fail(e);
            test.fail(MarkupHelper.createCodeBlock(response.prettyPrint(), CodeLanguage.JSON));

        }

    }

    @Test
    public void getSingleUser(){
        ExtentTest test = extent.createTest("Get Single User data",
                "The response should have correct single user data.");

        Response response = given()
                .spec(req)
                .when()
                .get("/api/users/2");

        try{
        given()
                .spec(req)
                .when()
                .get("/api/users/2")
                .then()
                .statusCode(200) // Assert that the status code is 200
                .time(lessThan(3000L)) // Assert that the response time is less than 3000ms
                .assertThat().body(
                        "data.first_name" , equalTo("Janet"),
                        "data.id",equalTo(2),
                        "data.email",containsStringIgnoringCase("Janet"),
                        "data.avatar",containsString("reqres.in")
                )


        ;
            // Log the success in the report
            test.pass("The response has correct single user data");
            test.pass("The response status code is 200");
            test.pass("The response time is less than 3000ms");
            test.pass(MarkupHelper.createCodeBlock(response.prettyPrint(), CodeLanguage.JSON));
        }catch (AssertionError e){
            // Log any assertion failures
            test.fail("Assertion Failed");
            test.fail(e);
            test.fail(MarkupHelper.createCodeBlock(response.prettyPrint(), CodeLanguage.JSON));
        } catch (Exception e){
            // Log any unexpected exceptions
            test.fail("Exception Error");
            test.fail(e);
            test.fail(MarkupHelper.createCodeBlock(response.prettyPrint(), CodeLanguage.JSON));
        }
    }



    @Test
    public void getNonExistingUser(){
        ExtentTest test = extent.createTest("Get a non-existing User data",
                "The response should body should be empty.");
        Response response = given()
                .spec(req)
                .when()
                .get("/api/users/2");

        try {
            given()
                    .spec(req)
                    .when()
                    .get("/api/users/25")
                    .then()
                    .statusCode(404)  // Assert that the status code is 404
                    .time(lessThan(3000L)) // Assert that the response time is less than 3000ms

            ;
            // Log the success in the report
            test.pass("The response status code is 404");
            test.pass("The response time is less than 3000ms");
            test.pass(MarkupHelper.createCodeBlock(response.prettyPrint(), CodeLanguage.JSON));

        }catch (AssertionError e){
            // Log any assertion failures
            test.fail("Assertion Failed");
            test.fail(e);
            test.fail(MarkupHelper.createCodeBlock(response.prettyPrint(), CodeLanguage.JSON));
        } catch (Exception e){
            // Log any unexpected exceptions
            test.fail("Exception Error");
            test.fail(e);
            test.fail(MarkupHelper.createCodeBlock(response.prettyPrint(), CodeLanguage.JSON));
        }

    }

    @Test
    public void createUser() {
        ExtentTest test = extent.createTest("Create a new user",
                "The user should be created successfully.");
        Response response = given()
                .spec(req)
                .when()
                .get("/api/users/2");


        HashMap<String , String> body = new HashMap<>();
        body.put("name" , "Hossam");
        body.put("job" , "Software Tester");

        try{
            given()
                    .spec(req)
                    .contentType(ContentType.JSON)
                    .body(body)
                    .when()
                    .post("/api/users")
                    .then()
                    .statusCode(201) // Assert that the status code is 201
                    .time(lessThan(3000L)) // Assert that the response time is less than 3000ms

                    .assertThat().body(
                            "name", equalTo("Hossam"), // Assert that the name in the response is "Hossam"
                            "job", equalTo("Software Tester") // Assert that the job in the response is "Software Tester"
                    )
                    .log().all()
            ;
            // Log the success in the report
            test.pass("The new user data is created successfully");
            test.pass("The response status code is 201");
            test.pass("The response time is less than 3000ms");
            test.pass(MarkupHelper.createCodeBlock(response.prettyPrint(), CodeLanguage.JSON));

        } catch (AssertionError e){
            // Log any assertion failures
            test.fail("Assertion Failed");
            test.fail(e);
            test.fail(MarkupHelper.createCodeBlock(response.prettyPrint(), CodeLanguage.JSON));
        } catch (Exception e){
            // Log any unexpected exceptions
            test.fail("Exception Error");
            test.fail(e);
            test.fail(MarkupHelper.createCodeBlock(response.prettyPrint(), CodeLanguage.JSON));
        }
    }




    @Test
    public void updateUserData(){
        ExtentTest test = extent.createTest("Update an existing user",
                "The user data should be updated successfully.");

        Response response = given()
                .spec(req)
                .when()
                .get("/api/users/2");

        try{
            given()
                    .spec(req)
                    .body("{\n" +
                            "    \"name\": \"morpheus\",\n" +
                            "    \"job\": \"zion resident\"\n" +
                            "}")
                    .contentType(ContentType.JSON)
                    .when()
                    .put("/api/users/2")
                    .then()
                    .statusCode(200) // Assert that the status code is 200
                    .time(lessThan(3000L)) // Assert that the response time is less than 3000ms

                    .assertThat().body(
                            "name", equalTo("morpheus"), // Assert that the name in the response is "morpheus"
                            "job", equalTo("zion resident") // Assert that the job in the response is "zion resident"
                    )

            ;
            // Log the success in the report
            test.pass("The user data is updated successfully");
            test.pass("The response status code is 200");
            test.pass("The response time is less than 3000ms");
            test.pass(MarkupHelper.createCodeBlock(response.prettyPrint(), CodeLanguage.JSON));

        } catch (AssertionError e){
            // Log any assertion failures
            test.fail("Assertion Failed");
            test.fail(e);
            test.fail(MarkupHelper.createCodeBlock(response.prettyPrint(), CodeLanguage.JSON));
        } catch (Exception e){
            // Log any unexpected exceptions
            test.fail("Exception Error");
            test.fail(e);
            test.fail(MarkupHelper.createCodeBlock(response.prettyPrint(), CodeLanguage.JSON));
        }
    }


    @Test
    public void updateUserDataPatch(){
        ExtentTest test = extent.createTest("Update an existing user",
                "The user data should be updated successfully.");

        Response response = given()
                .spec(req)
                .when()
                .get("/api/users/2");

        try {
            given()
                    .spec(req)
                    .body("{\n" +
                            "    \"name\": \"morpheus\",\n" +
                            "    \"job\": \"zion resident\"\n" +
                            "}")
                    .contentType(ContentType.JSON)
                    .when()
                    .patch("/api/users/2")
                    .then()
                    .statusCode(200) // Assert that the status code is 200
                    .time(lessThan(3000L)) // Assert that the response time is less than 3000ms

                    .assertThat().body(
                            "name", equalTo("morpheus"), // Assert that the name in the response is "morpheus"
                            "job", equalTo("zion resident") // Assert that the job in the response is "zion resident"
                    )

            ;
            // Log the success in the report
            test.pass("The user data is updated successfully.");
            test.pass("The response status code is 200");
            test.pass("The response time is less than 3000ms");
            test.pass(MarkupHelper.createCodeBlock(response.prettyPrint(), CodeLanguage.JSON));

        } catch (AssertionError e){
            // Log any assertion failures
            test.fail("Assertion Failed");
            test.fail(e);
            test.fail(MarkupHelper.createCodeBlock(response.prettyPrint(), CodeLanguage.JSON));
        } catch (Exception e){
            // Log any unexpected exceptions
            test.fail("Exception Error");
            test.fail(e);
            test.fail(MarkupHelper.createCodeBlock(response.prettyPrint(), CodeLanguage.JSON));
        }
    }

    @Test
    public void deleteUser(){
        ExtentTest test = extent.createTest("Delete an existing user",
                "The user data should be deleted successfully.");

        Response response = given()
                .spec(req)
                .when()
                .get("/api/users/2");

        try {
            given()
                    .spec(req)
                    .when()
                    .delete("/api/users/2")
                    .then()
                    .statusCode(204) // Assert that the status code is 204
                    .time(lessThan(3000L)) // Assert that the response time is less than 3000ms

            ;
            // Log the success in the report
            test.pass("The user data is deleted successfully.");
            test.pass("The response status code is 204");
            test.pass("The response time is less than 3000ms");
            test.pass(MarkupHelper.createCodeBlock(response.prettyPrint(), CodeLanguage.JSON));


        } catch (AssertionError e){
            // Log any assertion failures
            test.fail("Assertion Failed");
            test.fail(e);
            test.fail(MarkupHelper.createCodeBlock(response.prettyPrint(), CodeLanguage.JSON));
        } catch (Exception e){
            // Log any unexpected exceptions
            test.fail("Exception Error");
            test.fail(e);
            test.fail(MarkupHelper.createCodeBlock(response.prettyPrint(), CodeLanguage.JSON));
        }

    }
    @AfterTest
    public void tearDown(){
        extent.flush();
    }

}
