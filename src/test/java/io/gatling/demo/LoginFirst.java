package io.gatling.demo;

import io.gatling.javaapi.core.ChainBuilder;
import io.gatling.javaapi.core.FeederBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import java.util.Map;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.header;
import static io.gatling.javaapi.http.HttpDsl.http;

public class LoginFirst extends Simulation {

    private HttpProtocolBuilder httpProtocol = http
            .baseUrl("http://localhost:1080")
            .inferHtmlResources()
            .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
            .acceptEncodingHeader("gzip, deflate")
            .acceptLanguageHeader("ru-RU,ru;q=0.8,en-US;q=0.5,en;q=0.3")
            .userAgentHeader("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:128.0) Gecko/20100101 Firefox/128.0");

    private static Map<CharSequence, String> headers_0 = Map.ofEntries(
            Map.entry("If-Modified-Since", "Fri, 15 Dec 2023 21:35:31 GMT"),
            Map.entry("If-None-Match", "\"16e-60c932df4aec0\""),
            Map.entry("Priority", "u=0, i"),
            Map.entry("Upgrade-Insecure-Requests", "1")
    );

    private static Map<CharSequence, String> headers_1 = Map.ofEntries(
            Map.entry("If-Modified-Since", "Fri, 15 Dec 2023 21:35:31 GMT"),
            Map.entry("If-None-Match", "\"2c6-60c932df4aec0\""),
            Map.entry("Priority", "u=4"),
            Map.entry("Upgrade-Insecure-Requests", "1")
    );

    private static Map<CharSequence, String> headers_2 = Map.ofEntries(
            Map.entry("Priority", "u=4"),
            Map.entry("Upgrade-Insecure-Requests", "1")
    );

    private static Map<CharSequence, String> headers_4 = Map.ofEntries(
            Map.entry("Origin", "http://localhost:1080"),
            Map.entry("Priority", "u=4"),
            Map.entry("Upgrade-Insecure-Requests", "1")
    );

    private static Map<CharSequence, String> headers_7 = Map.ofEntries(
            Map.entry("Accept", "image/avif,image/jxl,image/webp,image/png,image/svg+xml,image/*;q=0.8,*/*;q=0.5"),
            Map.entry("Priority", "u=5, i")
    );

    // Feeder для случайного выбора пользователя из CSV
    public static final FeederBuilder<String> csvFeeder = csv("data/registered_users.csv").random();

    // OpenSite
    public static final ChainBuilder openSite = exec(
            group("OpenSite").on(
                    http("open_site_0")
                            .get("/WebTours/")
                            .headers(headers_0)
                            .check(bodyString().saveAs("responseBodyFrom0")),
                    pause(1),
                    http("open_site_1")
                            .get("/WebTours/header.html")
                            .headers(headers_1)
                            .check(bodyString().saveAs("responseBodyFrom1")),
                    pause(1),
                    http("open_site_2")
                            .get("/cgi-bin/welcome.pl?page=home")
                            .headers(headers_2)
//                            .check(header("Set-Cookie").saveAs("sessionCookie"))
                            .check(bodyString().saveAs("responseBodyFrom2")),
                    pause(1),
                    http("open_site_3")
                            .get("/cgi-bin/nav.pl?in=home")
                            .headers(headers_2)
                            .check(regex("name=\"userSession\" value=\"(.+?)\"").saveAs("userSession"))
                            .check(bodyString().saveAs("responseBodyFrom3"))
            )
    );

    // Login
    public static final ChainBuilder login = exec(
            group("Login").on(
                    http("login_0")
                            .post("/cgi-bin/login.pl")
                            .headers(headers_4)
                            .formParam("userSession", "#{userSession}")
                            .formParam("username", "#{username}")
                            .formParam("password", "#{password}")
                            .formParam("login.x", "44")
                            .formParam("login.y", "8")
                            .formParam("JSFormSubmit", "off")
                            .check(substring("User password was correct"))
                            .check(bodyString().saveAs("responseBody"))
                            .resources(
                                    http("login_1")
                                            .get("/cgi-bin/nav.pl?page=menu&in=home")
                                            .headers(headers_2)
                                            .check(bodyString().saveAs("responseBodyFrom5")),
                                    http("login_2")
                                            .get("/cgi-bin/login.pl?intro=true")
                                            .headers(headers_2)
                                            .check(substring("Welcome, <b>#{username}</b>"))
                                            .check(bodyString().saveAs("responseBodyFrom6")),
                                    http("login_3")
                                            .get("/WebTours/images/flights.gif")
                                            .headers(headers_7),
                                    http("login_4")
                                            .get("/WebTours/images/in_home.gif")
                                            .headers(headers_7),
                                    http("login_5")
                                            .get("/WebTours/images/itinerary.gif")
                                            .headers(headers_7),
                                    http("login_6")
                                            .get("/WebTours/images/signoff.gif")
                                            .headers(headers_7)
                            )
            )
    );

    // Itinerary
    public static final ChainBuilder itinerary = exec(
            group("Itinerary").on(
                    http("itinerary_0")
                            .get("/cgi-bin/welcome.pl?page=itinerary")
                            .headers(headers_2)
                            .check(substring("<frame src=\"itinerary.pl\""))
                            .check(bodyString().saveAs("responseBodyFrom12"))
                            .resources(
                                    http("itinerary_1")
                                            .get("/cgi-bin/nav.pl?page=menu&in=itinerary")
                                            .headers(headers_2)
                                            .check(substring("in_itinerary.gif"))
                                            .check(bodyString().saveAs("responseBodyFrom13")),
                                    http("itinerary_2")
                                            .get("/cgi-bin/itinerary.pl")
                                            .headers(headers_2)
                                            .check(regex("name=\"flightID\" value=\"(.+?)\"").findAll().saveAs("flightIDs"))
                                            .check(regex("A total of (\\d+) scheduled flights").saveAs("flightCount"))
                                            .check(bodyString().saveAs("responseBodyFrom14")),
                                    http("itinerary_3")
                                            .get("/WebTours/images/in_itinerary.gif")
                                            .headers(headers_7)
                                            .check(bodyBytes().saveAs("responseBodyFrom15")),
                                    http("itinerary_4")
                                            .get("/WebTours/images/home.gif")
                                            .headers(headers_7)
                                            .check(bodyBytes().saveAs("responseBodyFrom16")),
                                    http("itinerary_5")
                                            .get("/WebTours/images/cancelallreservations.gif")
                                            .headers(headers_7)
                                            .check(bodyBytes().saveAs("responseBodyFrom17")),
                                    http("itinerary_6")
                                            .get("/WebTours/images/cancelreservation.gif")
                                            .headers(headers_7)
                                            .check(bodyBytes().saveAs("responseBodyFrom18"))
                            )
            )
    );

    // Logout
    public static final ChainBuilder logout = exec(
            group("Logout").on(
                    http("logout_0")
                            .get("/cgi-bin/welcome.pl?signOff=1")
                            .headers(headers_2)
                            .check(bodyString().saveAs("responseBodyFrom11"))
                            .resources(
                                    http("logout_1")
                                            .get("/cgi-bin/nav.pl?in=home")
                                            .headers(headers_2)
                                            .check(bodyString().saveAs("responseBodyFrom12"))
                            ),
                    exec(session -> {
//                        System.out.println("User's data: " + session.get("username") + " , " + session.get("password"));
//                        System.out.println("Session cookie: " + session.getString("sessionCookie"));
//                        System.out.println("Response for open_site_3: " + session.getString("responseBodyFrom3"));
//                        System.out.println("userSession: " + session.getString("userSession"));
//                        System.out.println("Response for login_0: " + session.getString("responseBody"));
//                        System.out.println("Response for login_1: " + session.getString("responseBodyFrom5"));
//                        System.out.println("Response for login_2: " + session.getString("responseBodyFrom6"));
                        return session;
                    })
            )
    );

    private ScenarioBuilder scn = scenario("UC_Login")
            .feed(csvFeeder)
            .exec(openSite)
            .pause(46)
            .exec(login)
            .pause(15)
            .exec(itinerary)
            .pause(5)
            .exec(logout);

    {
        setUp(scn.injectOpen(atOnceUsers(1))).protocols(httpProtocol);
    }
}