package io.gatling.demo;

import java.util.*;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class DeleteFirstItinerary extends Simulation {

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

    private static Map<CharSequence, String> headers_8 = Map.ofEntries(
            Map.entry("Content-Type", "multipart/form-data; boundary=----#{boundary}"),
            Map.entry("Origin", "http://localhost:1080"),
            Map.entry("Priority", "u=4"),
            Map.entry("Sec-Fetch-Dest", "frame"),
            Map.entry("Sec-Fetch-Mode", "navigate"),
            Map.entry("Sec-Fetch-Site", "same-origin"),
            Map.entry("Sec-Fetch-User", "?1")
    );

    public static final FeederBuilder<String> csvFeeder = csv("data/registered_users.csv").circular();
    private static final Random random = new Random();

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
                                            .check(bodyString().saveAs("responseBodyFrom6")),
                                    http("login_3")
                                            .get("/WebTours/images/flights.gif")
                                            .headers(headers_7)
                                            .check(bodyBytes().saveAs("responseBodyFrom7")),
                                    http("login_4")
                                            .get("/WebTours/images/signoff.gif")
                                            .headers(headers_7)
                                            .check(bodyBytes().saveAs("responseBodyFrom8")),
                                    http("login_5")
                                            .get("/WebTours/images/in_home.gif")
                                            .headers(headers_7)
                                            .check(bodyBytes().saveAs("responseBodyFrom9")),
                                    http("login_6")
                                            .get("/WebTours/images/itinerary.gif")
                                            .headers(headers_7)
                                            .check(bodyBytes().saveAs("responseBodyFrom10"))
                            )
            )
    );

    // Itinerary
    public static final ChainBuilder itinerary = exec(
            group("Itinerary").on(
                    http("itinerary_0")
                            .get("/cgi-bin/welcome.pl?page=itinerary")
                            .headers(headers_2)
                            .check(bodyString().saveAs("responseBodyFrom11"))
                            .resources(
                                    http("itinerary_1")
                                            .get("/cgi-bin/nav.pl?page=menu&in=itinerary")
                                            .headers(headers_2)
                                            .check(bodyString().saveAs("responseBodyFrom12")),
                                    http("itinerary_2")
                                            .get("/cgi-bin/itinerary.pl")
                                            .headers(headers_2)
                                            .check(regex("name=\"flightID\" value=\"(.+?)\"").findAll().saveAs("flightIDs"))
                                            .check(regex("A total of (\\d+) scheduled flights").saveAs("flightCount"))
                                            .check(bodyString().saveAs("responseBodyFrom13")),
                                    http("itinerary_3")
                                            .get("/WebTours/images/in_itinerary.gif")
                                            .headers(headers_7)
                                            .check(bodyBytes().saveAs("responseBodyFrom14")),
                                    http("itinerary_4")
                                            .get("/WebTours/images/home.gif")
                                            .headers(headers_7)
                                            .check(bodyBytes().saveAs("responseBodyFrom15")),
                                    http("itinerary_5")
                                            .get("/WebTours/images/cancelallreservations.gif")
                                            .headers(headers_7)
                                            .check(bodyBytes().saveAs("responseBodyFrom16")),
                                    http("itinerary_6")
                                            .get("/WebTours/images/cancelreservation.gif")
                                            .headers(headers_7)
                                            .check(bodyBytes().saveAs("responseBodyFrom17"))
                            )
            )
    );

    // DeleteTickets
    public static final ChainBuilder deleteTickets = exec(
            group("DeleteTickets").on(
                    exec(session -> {
                        String boundary = "WebKitFormBoundary" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
                        List<String> flightIDs = session.get("flightIDs");
                        int flightCount = Integer.parseInt(session.getString("flightCount"));

                        StringBuilder requestBody = new StringBuilder();

                        // Чекбокс для первого билета
                        requestBody.append("------").append(boundary).append("\r\n")
                                .append("Content-Disposition: form-data; name=\"1\"\r\n\r\n")
                                .append("on\r\n");

                        // Все flightID
                        for (String flightID : flightIDs) {
                            requestBody.append("------").append(boundary).append("\r\n")
                                    .append("Content-Disposition: form-data; name=\"flightID\"\r\n\r\n")
                                    .append(flightID).append("\r\n");
                        }

                        // Координаты кнопки (случайные значения)
                        requestBody.append("------").append(boundary).append("\r\n")
                                .append("Content-Disposition: form-data; name=\"removeFlights.x\"\r\n\r\n")
                                .append(random.nextInt(50) + 20).append("\r\n")
                                .append("------").append(boundary).append("\r\n")
                                .append("Content-Disposition: form-data; name=\"removeFlights.y\"\r\n\r\n")
                                .append(random.nextInt(20)).append("\r\n");

                        // Поля .cgifields
                        for (int i = 1; i <= flightCount; i++) {
                            requestBody.append("------").append(boundary).append("\r\n")
                                    .append("Content-Disposition: form-data; name=\".cgifields\"\r\n\r\n")
                                    .append(i).append("\r\n");
                        }

                        // Финальный boundary
                        requestBody.append("------").append(boundary).append("--\r\n");

                        return session.set("dynamicBody", requestBody.toString())
                                .set("boundary", boundary)
                                .set("deletedFlightID", flightIDs.get(0)); // Сохраняем удаляемый flightID
                    }),
                    http("delete_tickets_0")
                            .post("/cgi-bin/itinerary.pl")
                            .headers(headers_8)
                            .body(StringBody("#{dynamicBody}"))
                            .check(substring("Itinerary"))
                            .check(regex("name=\"flightID\" value=\"(.+?)\"").findAll().saveAs("remainingFlightIDs"))
                            .check(regex("A total of (\\d+) scheduled flights").saveAs("newFlightCount"))
                            .check(bodyString().saveAs("responseBodyFrom18"))
                            .resources(
                                    http("delete_tickets_1")
                                            .get("/WebTours/images/cancelallreservations.gif")
                                            .headers(headers_7)
                                            .check(bodyBytes().saveAs("responseBodyFrom16")),
                                    http("delete_tickets_2")
                                            .get("/WebTours/images/cancelreservation.gif")
                                            .headers(headers_7)
                                            .check(bodyBytes().saveAs("responseBodyFrom17"))
                            )
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

    // Debug Output
    private static final ChainBuilder debugOutput = exec(
            group("DebugOutput").on(
                    exec(session -> {
                        System.out.println("Response for open_site_0: " + session.getString("responseBodyFrom0"));
                        System.out.println("Response for open_site_1: " + session.getString("responseBodyFrom1"));
                        System.out.println("Response for open_site_2: " + session.getString("responseBodyFrom2"));
                        System.out.println("Response for open_site_3: " + session.getString("responseBodyFrom3"));
                        System.out.println("userSession: " + session.getString("userSession"));
                        System.out.println("Response for login_0: " + session.getString("responseBodyFrom4"));
                        System.out.println("Response for login_1: " + session.getString("responseBodyFrom5"));
                        System.out.println("Response for login_2: " + session.getString("responseBodyFrom6"));
                        System.out.println("Response for login_3 (flights.gif): [binary data]");
                        System.out.println("Response for login_4 (signoff.gif): [binary data]");
                        System.out.println("Response for login_5 (in_home.gif): [binary data]");
                        System.out.println("Response for login_6 (itinerary.gif): [binary data]");
                        System.out.println("Response for itinerary_0: " + session.getString("responseBodyFrom11"));
                        System.out.println("Response for itinerary_1: " + session.getString("responseBodyFrom12"));
                        System.out.println("Response for itinerary_2: " + session.getString("responseBodyFrom13"));
                        System.out.println("Flight IDs: " + session.get("flightIDs"));
                        System.out.println("Flight Count: " + session.getString("flightCount"));
                        System.out.println("Dynamic Body for delete_tickets_0: " + session.getString("dynamicBody"));
                        System.out.println("Response for itinerary_3 (in_itinerary.gif): [binary data]");
                        System.out.println("Response for itinerary_4 (home.gif): [binary data]");
                        System.out.println("Response for itinerary_5 (cancelallreservations.gif): [binary data]");
                        System.out.println("Response for itinerary_6 (cancelreservation.gif): [binary data]");
                        System.out.println("Response for delete_tickets_0: " + session.getString("responseBodyFrom18"));
                        System.out.println("Remaining Flight IDs: " + session.get("remainingFlightIDs"));
                        System.out.println("New Flight Count: " + session.getString("newFlightCount"));
                        System.out.println("Response for logout_0: " + session.getString("responseBodyFrom19"));
                        System.out.println("Response for logout_1: " + session.getString("responseBodyFrom20"));
                        return session;
                    })
            )
    );

    private ScenarioBuilder scn = scenario("UC_DeleteFirstItinerary")
            .feed(csvFeeder)
            .exec(openSite)
            .pause(5)
            .exec(login)
            .pause(5)
            .exec(itinerary)
            .pause(5)
            .exec(deleteTickets)
            .pause(5)
            .exec(logout)
            .exec(debugOutput);

    {
        setUp(scn.injectOpen(atOnceUsers(1))).protocols(httpProtocol);
    }
}