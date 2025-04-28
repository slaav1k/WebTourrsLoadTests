package io.gatling.demo;

import java.text.SimpleDateFormat;
import java.util.*;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class LookWaybill extends Simulation {

    private HttpProtocolBuilder httpProtocol = http
            .baseUrl("http://localhost:1080")
            .inferHtmlResources()
            .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
            .acceptEncodingHeader("gzip, deflate")
            .acceptLanguageHeader("ru-RU,ru;q=0.8,en-US;q=0.5,en;q=0.3")
            .upgradeInsecureRequestsHeader("1")
            .userAgentHeader("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:128.0) Gecko/20100101 Firefox/128.0");

    private static Map<CharSequence, String> headers_0 = Map.ofEntries(
            Map.entry("If-Modified-Since", "Fri, 15 Dec 2023 21:35:31 GMT"),
            Map.entry("If-None-Match", "\"16e-60c932df4aec0\""),
            Map.entry("Priority", "u=0, i")
    );

    private static Map<CharSequence, String> headers_1 = Map.ofEntries(
            Map.entry("If-Modified-Since", "Fri, 15 Dec 2023 21:35:31 GMT"),
            Map.entry("If-None-Match", "\"2c6-60c932df4aec0\""),
            Map.entry("Priority", "u=4")
    );

    private static Map<CharSequence, String> headers_2 = Map.of("Priority", "u=4");

    private static Map<CharSequence, String> headers_4 = Map.ofEntries(
            Map.entry("Origin", "http://localhost:1080"),
            Map.entry("Priority", "u=4")
    );

    private static Map<CharSequence, String> headers_7 = Map.ofEntries(
            Map.entry("Accept", "image/avif,image/jxl,image/webp,image/png,image/svg+xml,image/*;q=0.8,*/*;q=0.5"),
            Map.entry("Priority", "u=5, i")
    );

    public static final FeederBuilder<String> csvFeeder = csv("data/registered_users.csv").circular();

    // OpenSite
    public static final ChainBuilder openSite = exec(
            group("OpenSite").on(
                    http("open_site_0")
                            .get("/WebTours/")
                            .headers(headers_0)
                            .check(substring("<frameset cols=\"160,*\""))
                            .check(bodyString().saveAs("responseBodyFrom0")),
                    pause(1),
                    http("open_site_1")
                            .get("/WebTours/header.html")
                            .headers(headers_1)
                            .check(substring("<title>Web Tours</title>"))
                            .check(bodyString().saveAs("responseBodyFrom1")),
                    pause(1),
                    http("open_site_2")
                            .get("/cgi-bin/welcome.pl?page=home")
                            .headers(headers_2)
                            .check(substring("A Session ID has been created"))
//                            .check(header("Set-Cookie").saveAs("sessionCookie"))
                            .check(bodyString().saveAs("responseBodyFrom2")),
                    pause(1),
                    http("open_site_3")
                            .get("/cgi-bin/nav.pl?in=home")
                            .headers(headers_2)
                            .check(substring("Web Tours Navigation Bar"))
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
                            .formParam("login.x", "0")
                            .formParam("login.y", "0")
                            .formParam("JSFormSubmit", "off")
                            .check(substring("User password was correct"))
                            .check(substring("<frame src=\"nav.pl?page=menu&in=home\""))
                            .check(bodyString().saveAs("responseBodyFrom4"))
                            .resources(
                                    http("login_1")
                                            .get("/cgi-bin/nav.pl?page=menu&in=home")
                                            .headers(headers_2)
                                            .check(substring("Search Flights Button"))
                                            .check(bodyString().saveAs("responseBodyFrom5")),
                                    http("login_2")
                                            .get("/cgi-bin/login.pl?intro=true")
                                            .headers(headers_2)
                                            .check(substring("Welcome, <b>#{username}</b>"))
                                            .check(bodyString().saveAs("responseBodyFrom6"))
                            )
            )
    );

    // Flights
    public static final ChainBuilder flights = exec(
            group("Flights").on(
                    http("flights_0")
                            .get("/cgi-bin/welcome.pl?page=search")
                            .headers(headers_2)
                            .check(substring("User has returned to the search page"))
                            .check(bodyString().saveAs("responseBodyFrom7"))
                            .resources(
                                    http("flights_1")
                                            .get("/cgi-bin/nav.pl?page=menu&in=flights")
                                            .headers(headers_2)
                                            .check(substring("in_flights.gif"))
                                            .check(bodyString().saveAs("responseBodyFrom8")),
                                    http("flights_2")
                                            .get("/cgi-bin/reservations.pl?page=welcome")
                                            .headers(headers_2)
                                            .check(substring("Find Flight"))
                                            .check(regex("<option[^>]*value=\"(.+?)\"").findAll().saveAs("cityList"))
                                            .check(bodyString().saveAs("responseBodyFrom9"))
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

    // Debug Output
    private static final ChainBuilder debugOutput = exec(
            group("DebugOutput").on(
                    exec(session -> {
                        System.out.println("Response for open_site_0: " + session.getString("responseBodyFrom0"));
                        System.out.println("Response for open_site_1: " + session.getString("responseBodyFrom1"));
                        System.out.println("Response for open_site_2: " + session.getString("responseBodyFrom2"));
                        System.out.println("Response for open_site_3: " + session.getString("responseBodyFrom3"));
                        System.out.println("Response for login_0: " + session.getString("responseBodyFrom4"));
                        System.out.println("Response for login_1: " + session.getString("responseBodyFrom5"));
                        System.out.println("Response for login_2: " + session.getString("responseBodyFrom6"));
                        System.out.println("Response for flights_0: " + session.getString("responseBodyFrom7"));
                        System.out.println("Response for flights_1: " + session.getString("responseBodyFrom8"));
                        System.out.println("Response for flights_2: " + session.getString("responseBodyFrom9"));
                        System.out.println("Response for itinerary_0: " + session.getString("responseBodyFrom12"));
                        System.out.println("Response for itinerary_1: " + session.getString("responseBodyFrom13"));
                        System.out.println("Response for itinerary_2: " + session.getString("responseBodyFrom14"));
                        System.out.println("Response for itinerary_3 (in_itinerary.gif): [binary data]");
                        System.out.println("Response for itinerary_4 (home.gif): [binary data]");
                        System.out.println("Response for itinerary_5 (cancelallreservations.gif): [binary data]");
                        System.out.println("Response for itinerary_6 (cancelreservation.gif): [binary data]");
                        return session;
                    })
            )
    );

    private ScenarioBuilder scn = scenario("UC_LookWaybill")
            .feed(csvFeeder)
            .exec(openSite)
            .pause(5)
            .exec(login)
            .pause(5)
            .exec(flights)
            .pause(5)
            .exec(itinerary)
            .exec(debugOutput);

    {
        setUp(scn.injectOpen(atOnceUsers(10))).protocols(httpProtocol);
    }
}