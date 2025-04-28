package io.gatling.demo;

import io.gatling.javaapi.core.ChainBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import ru.slaav1k.items.DataGenerator;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.Random;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

public class Registration extends Simulation {

    private HttpProtocolBuilder httpProtocol = http
            .baseUrl("http://localhost:1080")
            .inferHtmlResources()
            .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
            .acceptEncodingHeader("gzip, deflate")
            .acceptLanguageHeader("ru-RU,ru;q=0.8,en-US;q=0.5,en;q=0.3")
            .upgradeInsecureRequestsHeader("1")
            .userAgentHeader("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:128.0) Gecko/20100101 Firefox/128.0");

    private static Map<CharSequence, String> headers_0 = Map.of("Priority", "u=4");

    private static Map<CharSequence, String> headers_2 = Map.ofEntries(
            Map.entry("Priority", "u=4"),
            Map.entry("Upgrade-Insecure-Requests", "1")
    );

    private static Map<CharSequence, String> headers_3 = Map.ofEntries(
            Map.entry("Content-Type", "multipart/form-data; boundary=---------------------------41534795353139666137712298142"),
            Map.entry("Origin", "http://localhost:1080"),
            Map.entry("Priority", "u=4")
    );

    // OpenSite
    // pause(14)
    public static final ChainBuilder openSite = exec(
            group("OpenSite").on(
                    http("open_site_0")
                            .get("/cgi-bin/welcome.pl?signOff=true")
                            .headers(headers_0)
                            .check(bodyString().saveAs("responseBodyFrom0"))
                            .resources(
                                    http("open_site_1")
                                            .get("/cgi-bin/nav.pl?in=home")
                                            .headers(headers_0)
                                            .check(bodyString().saveAs("responseBodyFrom1"))
                            )
            )
    );

    // OpenRegisterForm
    // pause(66)
    public static final ChainBuilder openRegisterForm = exec(
            group("Open Register Form").on(
                    http("open_register_from_0")
                            .get("/cgi-bin/login.pl?username=&password=&getInfo=true")
                            .headers(headers_0)
                            .check(substring("Customer Profile").exists())
                            .check(bodyString().saveAs("responseBodyFrom2"))
            )
    );

    // Generate unique registration data
    public static final ChainBuilder generateData = exec(
            group("Generate Unique Registration Data").on(
                    exec(session -> {
                        Random rand = new Random();
                        String username = DataGenerator.generateRandomString(rand, 5, 10);
                        String password = DataGenerator.generatePassword(rand);
                        String firstName = DataGenerator.generateRandomString(rand, 5, 10);
                        String lastName = DataGenerator.generateRandomString(rand, 5, 10);
                        String address1 = DataGenerator.generateRandomString(rand, 10, 15);
                        String address2 = DataGenerator.generateRandomString(rand, 10, 15);
                        return session
                                .set("username", username)
                                .set("password", password)
                                .set("firstName", firstName)
                                .set("lastName", lastName)
                                .set("address1", address1)
                                .set("address2", address2);
                    })
            )
    );

    // ConfirmRegister
    public static final ChainBuilder confirmRegister = exec(
            group("Confirm Register").on(
                    http("confirm_register_0")
                            .post("/cgi-bin/login.pl")
                            .headers(headers_3)
                            .body(ElFileBody("0003_request.html"))
                            .check(status().is(200))
                            .check(substring("Your username is taken").notExists())
                            .check(substring("Thank you, <b>#{username}</b>, for registering and welcome to the Web Tours family."))
                            .check(bodyString().saveAs("responseBodyFrom3"))
            )
    );

    // Save to CSV
    private static final ChainBuilder saveToCsv = exec(
            group("Save to CSV").on(
                    exec(session -> {
                        Path csvPath = Paths.get("src/test/resources/data/registered_users.csv");
                        try {
                            Files.createDirectories(csvPath.getParent()); // Создаёт папку data в src/test/resources
                            if (!Files.exists(csvPath)) {
                                Files.writeString(csvPath, "username,password,firstName,lastName,address1,address2\n");
                            }
                            String csvLine = String.format(
//                                    "\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"\n",
                                    "%s,%s,%s,%s,%s,%s%n",
                                    session.getString("username"),
                                    session.getString("password"),
                                    session.getString("firstName"),
                                    session.getString("lastName"),
                                    session.getString("address1"),
                                    session.getString("address2")
                            );
                            Files.writeString(csvPath, csvLine, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                        } catch (Exception e) {
                            System.err.println("Failed to write to CSV: " + e.getMessage());
                            e.printStackTrace();
                        }
                        return session;
                    })
            )
    );

    // Logout
    public static final ChainBuilder logout = exec(
            group("Logout").on(
                    http("logout_0")
                            .get("/cgi-bin/welcome.pl?signOff=1")
                            .headers(headers_2)
                            .check(substring("A Session ID has been created"))
                            .check(bodyString().saveAs("responseBodyFrom19"))
                            .resources(
                                    http("logout_1")
                                            .get("/cgi-bin/nav.pl?in=home")
                                            .headers(headers_2)
                                            .check(substring("Web Tours Navigation Bar"))
                                            .check(regex("name=\"userSession\" value=\"(.+?)\"").exists())
                                            .check(bodyString().saveAs("responseBodyFrom20"))
                            )
            )
    );

    // AuthAfterRegister
    public static final ChainBuilder authAfterRegister = exec(
            group("Auth After Register").on(
                    http("auth_after_register_0")
                            .get("/cgi-bin/welcome.pl?page=menus")
                            .headers(headers_0)
                            .check(bodyString().saveAs("responseBodyFrom4"))
                            .resources(
                                    http("auth_after_register_1")
                                            .get("/cgi-bin/nav.pl?page=menu&in=home")
                                            .headers(headers_0)
                                            .check(bodyString().saveAs("responseBodyFrom5")),
                                    http("auth_after_register_2")
                                            .get("/cgi-bin/login.pl?intro=true")
                                            .headers(headers_0)
                                            .check(substring("Welcome, <b>#{username}</b>"))
                                            .check(bodyString().saveAs("responseBodyFrom6"))
                            ),
                    exec(session -> {
//                        System.out.println("Generated username: " + session.getString("username"));
//                        System.out.println("Generated password: " + session.getString("password"));
//                        System.out.println("Generated firstName: " + session.getString("firstName"));
//                        System.out.println("Generated lastName: " + session.getString("lastName"));
//                        System.out.println("Generated address1: " + session.getString("address1"));
//                        System.out.println("Generated address2: " + session.getString("address2"));
//                        System.out.println("Response for open_site_0: " + session.getString("responseBodyFrom0"));
//                        System.out.println("Response for open_site_1: " + session.getString("responseBodyFrom1"));
//                        System.out.println("Response for open_register_from_0: " + session.getString("responseBodyFrom2"));
//                        System.out.println("Response for confirm_register_0: " + session.getString("responseBodyFrom3"));
//                        System.out.println("Response for auth_after_register_0: " + session.getString("responseBodyFrom4"));
//                        System.out.println("Response for auth_after_register_1: " + session.getString("responseBodyFrom5"));
//                        System.out.println("Response for auth_after_register_2: " + session.getString("responseBodyFrom6"));
                        return session;
                    })
            )
    );

    private ScenarioBuilder scn = scenario("UC6_Registration")
            .exec(openSite)
            .pause(5)
            .exec(openRegisterForm)
            .pause(5)
            .exec(generateData)
            .exec(confirmRegister)
            .pause(5)
            .exec(saveToCsv)
            .exec(authAfterRegister)
            .pause(5)
            .exec(logout);


    {
        setUp(scn.injectOpen(atOnceUsers(50))).protocols(httpProtocol);
    }

    private static String handleNull(String value) {
        return value == null ? "null" : value;
    }
}