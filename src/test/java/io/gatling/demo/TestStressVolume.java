package io.gatling.demo;

import java.time.Duration;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class TestStressVolume extends Simulation {

    private HttpProtocolBuilder httpProtocol = http
            .baseUrl("http://localhost:1080")
            .inferHtmlResources()
            .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
            .acceptEncodingHeader("gzip, deflate")
            .acceptLanguageHeader("ru-RU,ru;q=0.8,en-US;q=0.5,en;q=0.3")
            .upgradeInsecureRequestsHeader("1")
            .userAgentHeader("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:128.0) Gecko/20100101 Firefox/128.0");



    private ScenarioBuilder UC1_BuyTicket = scenario("UC1_BuyTicket")
            .forever().on(
                    pace(Duration.ofSeconds(42))
                            .feed(BuyTicket.csvFeeder)
                            .exec(BuyTicket.openSite)
                            .pause(5)
                            .exec(BuyTicket.login)
                            .pause(5)
                            .exec(BuyTicket.flights)
                            .pause(5)
                            .exec(BuyTicket.findFlights)
                            .pause(5)
                            .exec(BuyTicket.payment)
                            .pause(5)
                            .exec(BuyTicket.invoice)
            );



    private ScenarioBuilder UC2_DeleteFirstItinerary = scenario("UC2_DeleteFirstItinerary")
            .forever().on(
                    pace(Duration.ofSeconds(50))
                            .feed(DeleteFirstItinerary.csvFeeder)
                            .exec(DeleteFirstItinerary.openSite)
                            .pause(5)
                            .exec(DeleteFirstItinerary.login)
                            .pause(5)
                            .exec(DeleteFirstItinerary.itinerary)
                            .pause(5)
                            .exec(DeleteFirstItinerary.deleteTickets)
                            .pause(5)
                            .exec(DeleteFirstItinerary.logout)
            );



    private ScenarioBuilder UC3_Registration = scenario("UC3_Registration")
            .forever().on(
                    pace(Duration.ofSeconds(72))
                            .exec(Registration.openSite)
                            .pause(5)
                            .exec(Registration.openRegisterForm)
                            .pause(5)
                            .exec(Registration.generateData)
                            .exec(Registration.confirmRegister)
                            .pause(5)
                            .exec(Registration.authAfterRegister)
                            .pause(5)
                            .exec(Registration.logout)
            );



    private ScenarioBuilder UC4_Login = scenario("UC4_Login")
            .forever().on(
                    pace(Duration.ofSeconds(71))
                            .feed(LoginFirst.csvFeeder)
                            .exec(LoginFirst.openSite)
                            .pause(46)
                            .exec(LoginFirst.login)
                            .pause(15)
                            .exec(LoginFirst.itinerary)
                            .pause(5)
                            .exec(LoginFirst.logout)
            );



    private ScenarioBuilder UC5_FindTicketWithoutPay = scenario("UC5_FindTicketWithoutPay")
            .forever().on(
                    pace(Duration.ofSeconds(65))
                            .feed(FindTicketWithoutPay.csvFeeder)
                            .exec(FindTicketWithoutPay.openSite)
                            .pause(5)
                            .exec(FindTicketWithoutPay.login)
                            .pause(5)
                            .exec(FindTicketWithoutPay.flights)
                            .pause(5)
                            .exec(FindTicketWithoutPay.findFlights)
                            .pause(5)
                            .exec(FindTicketWithoutPay.payment)
                            .pause(5)
                            .exec(FindTicketWithoutPay.itinerary)
                            .pause(5)
                            .exec(FindTicketWithoutPay.logout)
            );



    private ScenarioBuilder UC6_LookWaybill = scenario("UC6_LookWaybill")
            .forever().on(
                    pace(Duration.ofSeconds(190))
                            .feed(LookWaybill.csvFeeder)
                            .exec(LookWaybill.openSite)
                            .pause(5)
                            .exec(LookWaybill.login)
                            .pause(5)
                            .exec(LookWaybill.flights)
                            .pause(5)
                            .exec(LookWaybill.itinerary)
            );



    {
        setUp(
                UC1_BuyTicket.injectClosed(
                        rampConcurrentUsers(0).to(12).during(Duration.ofMinutes(1)),
                        constantConcurrentUsers(12).during(Duration.ofMinutes(5)),
                        rampConcurrentUsers(12).to(14).during(Duration.ofMinutes(1)),
                        constantConcurrentUsers(14).during(Duration.ofMinutes(5)),
                        rampConcurrentUsers(14).to(16).during(Duration.ofMinutes(1)),
                        constantConcurrentUsers(16).during(Duration.ofMinutes(62)),
                        rampConcurrentUsers(16).to(18).during(Duration.ofMinutes(1)),
                        constantConcurrentUsers(18).during(Duration.ofMinutes(62)),
                        rampConcurrentUsers(18).to(0).during(Duration.ofMinutes(2))
                ),
                UC2_DeleteFirstItinerary.injectClosed(
                        rampConcurrentUsers(0).to(6).during(Duration.ofMinutes(1)),
                        constantConcurrentUsers(6).during(Duration.ofMinutes(5)),
                        rampConcurrentUsers(6).to(7).during(Duration.ofMinutes(1)),
                        constantConcurrentUsers(7).during(Duration.ofMinutes(5)),
                        rampConcurrentUsers(7).to(8).during(Duration.ofMinutes(1)),
                        constantConcurrentUsers(8).during(Duration.ofMinutes(62)),
                        rampConcurrentUsers(8).to(9).during(Duration.ofMinutes(1)),
                        constantConcurrentUsers(9).during(Duration.ofMinutes(62)),
                        rampConcurrentUsers(9).to(0).during(Duration.ofMinutes(2))
                ),
                UC3_Registration.injectClosed(
                        rampConcurrentUsers(0).to(12).during(Duration.ofMinutes(1)),
                        constantConcurrentUsers(12).during(Duration.ofMinutes(5)),
                        rampConcurrentUsers(12).to(14).during(Duration.ofMinutes(1)),
                        constantConcurrentUsers(14).during(Duration.ofMinutes(5)),
                        rampConcurrentUsers(14).to(16).during(Duration.ofMinutes(1)),
                        constantConcurrentUsers(16).during(Duration.ofMinutes(62)),
                        rampConcurrentUsers(16).to(18).during(Duration.ofMinutes(1)),
                        constantConcurrentUsers(18).during(Duration.ofMinutes(62)),
                        rampConcurrentUsers(18).to(0).during(Duration.ofMinutes(2))
                ),
                UC4_Login.injectClosed(
                        rampConcurrentUsers(0).to(6).during(Duration.ofMinutes(1)),
                        constantConcurrentUsers(6).during(Duration.ofMinutes(5)),
                        rampConcurrentUsers(6).to(7).during(Duration.ofMinutes(1)),
                        constantConcurrentUsers(7).during(Duration.ofMinutes(5)),
                        rampConcurrentUsers(7).to(8).during(Duration.ofMinutes(1)),
                        constantConcurrentUsers(8).during(Duration.ofMinutes(62)),
                        rampConcurrentUsers(8).to(9).during(Duration.ofMinutes(1)),
                        constantConcurrentUsers(9).during(Duration.ofMinutes(62)),
                        rampConcurrentUsers(9).to(0).during(Duration.ofMinutes(2))
                ),
                UC5_FindTicketWithoutPay.injectClosed(
                        rampConcurrentUsers(0).to(12).during(Duration.ofMinutes(1)),
                        constantConcurrentUsers(12).during(Duration.ofMinutes(5)),
                        rampConcurrentUsers(12).to(14).during(Duration.ofMinutes(1)),
                        constantConcurrentUsers(14).during(Duration.ofMinutes(5)),
                        rampConcurrentUsers(14).to(16).during(Duration.ofMinutes(1)),
                        constantConcurrentUsers(16).during(Duration.ofMinutes(62)),
                        rampConcurrentUsers(16).to(18).during(Duration.ofMinutes(1)),
                        constantConcurrentUsers(18).during(Duration.ofMinutes(62)),
                        rampConcurrentUsers(18).to(0).during(Duration.ofMinutes(2))
                ),
                UC6_LookWaybill.injectClosed(
                        rampConcurrentUsers(0).to(12).during(Duration.ofMinutes(1)),
                        constantConcurrentUsers(12).during(Duration.ofMinutes(5)),
                        rampConcurrentUsers(12).to(14).during(Duration.ofMinutes(1)),
                        constantConcurrentUsers(14).during(Duration.ofMinutes(5)),
                        rampConcurrentUsers(14).to(16).during(Duration.ofMinutes(1)),
                        constantConcurrentUsers(16).during(Duration.ofMinutes(62)),
                        rampConcurrentUsers(16).to(18).during(Duration.ofMinutes(1)),
                        constantConcurrentUsers(18).during(Duration.ofMinutes(62)),
                        rampConcurrentUsers(18).to(0).during(Duration.ofMinutes(2))
                )

        ).protocols(httpProtocol)
                .maxDuration(Duration.ofMinutes(140));
    }
}