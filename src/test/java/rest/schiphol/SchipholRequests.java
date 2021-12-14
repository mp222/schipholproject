package rest.schiphol;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import models.schiphol.*;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;

import static io.restassured.RestAssured.given;

public class SchipholRequests {

    private final SchipholContext schipholContext;

    public SchipholRequests() {
        schipholContext = new SchipholContext();
    }

    @When("^get all destinations sorted by country ascending$")
    public void getAllDestinationsSortedByCountryAscendingWithAppKey() {
        getDestinations();
    }

    @And("^results contain city \"([^\"]*)\" and country \"([^\"]*)\"$")
    public void resultsContainCityAndCountry(final String city, final String country) {
        final Optional<DestinationsData> cityData = findCity(city, country);
        final var softly = new SoftAssertions();
        softly.assertThat(cityData.get().getCity()).as("city should be equal to " + city).isEqualToIgnoringCase(city);
        softly.assertThat(cityData.get().getCountry()).as("country should be equal to " + country).isEqualToIgnoringCase(country);
        softly.assertAll();
    }

    @When("^successfully get all flights leaving Schiphol today$")
    public void successfullyGetAllFlightsLeavingSchipholToday() {
        LocalDateTime now = LocalDateTime.now();
        final var todayDate = now.format(DateTimeFormatter.ISO_LOCAL_DATE);
        getFlights(todayDate);
    }

    @Then("^find and return information about the IATA code for each flight and verify there is an IATA code$")
    public void findAndReturnInformationAboutTheIataCode() {
        LocalDateTime now = LocalDateTime.now();
        final var todayDate = now.format(DateTimeFormatter.ISO_LOCAL_DATE);
        List<FlightsData> data = findFlights(todayDate);
        returnFlightsInfo(data);
    }

    @When("^call API with incorrect key$")
    public void callAPIWithIncorrectKey() {
        callWithIncorrectKey();
    }

    @Then("^call is unsuccessful with http status \"([^\"]*)\"$")
    public void callIsUnsuccessfulWithHttpStatusAndMessage(final int status) {
        final var httpStatus = schipholContext.getSchipholHttpResponseValues().getStatusCode();
        Assertions.assertThat(httpStatus).as("http status code should be equal to " + status)
            .isEqualTo(status);
    }

    @When("^get a list off all IATA codes for country \"([^\"]*)\" and return with destination city$")
    public void getAListOffAllIataCodesForCountryAndReturnWithDestinationCity(final String country) {
        final List<DestinationsData> iataData = findIata(country);
        getDestinations();
        final int length = iataData.size();
        System.out.println(length);
        final var softly = new SoftAssertions();
        for (int i = 0; i < length; i++) {
            final var countryFromList = iataData.get(i).getCountry();
            final var cityFromList = iataData.get(i).getCity();
            final var iataFromList = iataData.get(i).getIata();
            if (null == iataFromList && cityFromList == null) {
                softly.assertThat(cityFromList)
                    .as("City and IATA code is missing for country: " + countryFromList)
                    .isNotNull();
            } else if (null == cityFromList) {
                softly.assertThat(cityFromList)
                    .as("City is missing for country: " + countryFromList + " and IATA code: " + iataFromList)
                    .isNotNull();
            } else if (null == iataFromList) {
                softly.assertThat(iataFromList)
                    .as("IATA is missing for country: " + countryFromList + " and city: " + cityFromList)
                    .isNotNull();
            } else {
                System.out.println("IATA code: " + iataFromList + " for country: " + countryFromList + " and destination city: " + cityFromList);
            }
        }
        softly.assertAll();
    }

    private void callToGetAllDestinationsByPage(final int pageNumber) {
        final var response = given()
            .header("Accept", "application/json")
            .header("app_id", "27759925")
            .header("app_key", "2d0e4600a11fd4bbb0f81767446908b3")
            .header("ResourceVersion", "v4")
            .queryParam("sort", "country")
            .queryParam("page", pageNumber)
            .when()
            .get("https://api.schiphol.nl/public-flights/destinations")
            .then();
        final var schipholHttpResponseValues = response.extract().statusCode();
        schipholContext.getSchipholHttpResponseValues().setStatusCode(schipholHttpResponseValues);
        final var httpStatus = schipholContext.getSchipholHttpResponseValues().getStatusCode();
        Assertions.assertThat(httpStatus).as("http status code should be equal 200.")
            .isEqualTo(200);
        schipholContext.setDestinationsResponse(response.extract().body().as(DestinationsResponse.class));
        schipholContext.getDestinationsResponseLinkedHashMap()
            .put(pageNumber, schipholContext.getDestinationsResponse());
    }

    private void callWithIncorrectKey() {
        final var response = given()
            .header("Accept", "application/json")
            .header("app_id", "27759925")
            .header("app_key", UUID.randomUUID())
            .header("ResourceVersion", "v4")
            .queryParam("sort", "country")
            .queryParam("page", "0")
            .when()
            .get("https://api.schiphol.nl/public-flights/destinations")
            .then();
        final var schipholHttpResponseValues = response.extract().statusCode();
        schipholContext.getSchipholHttpResponseValues().setStatusCode(schipholHttpResponseValues);
    }

    private void callToGetAllFlightsByPage(final int pageNumber, final String date) {
        final var response = given()
            .header("Accept", "application/json")
            .header("app_id", "27759925")
            .header("app_key", "2d0e4600a11fd4bbb0f81767446908b3")
            .header("ResourceVersion", "v4")
            .queryParam("page", pageNumber)
            .queryParam("scheduleDate", date)
            .when()
            .get("https://api.schiphol.nl/public-flights/flights")
            .then();
        final var schipholHttpResponseValues = response.extract().statusCode();
        schipholContext.getSchipholHttpResponseValues().setStatusCode(schipholHttpResponseValues);
        final var httpStatus = schipholContext.getSchipholHttpResponseValues().getStatusCode();
        Assertions.assertThat(httpStatus).as("http status code should be equal 200.")
            .isEqualTo(200);
        schipholContext.setFlightsResponse(response.extract().body().as(FlightsResponse.class));
        schipholContext.getFlightsResponseLinkedHashMap()
            .put(pageNumber, schipholContext.getFlightsResponse());
    }

    private Optional<DestinationsData> findCity(final String city, final String country) {
        int page = 0;
        Optional<DestinationsData> data = Optional.empty();
        do {
            callToGetAllDestinationsByPage(page);
            page++;
            Optional<DestinationsData> dest = schipholContext.getDestinationsResponse().getDestinations()
                .stream()
                .filter(pt -> pt.getCountry().equalsIgnoreCase(country))
                .filter(pt -> pt.getCity().equalsIgnoreCase(city))
                .findFirst();

            if (dest.isPresent()) {
                data = dest;
                break;
            }
        } while (schipholContext.getDestinationsResponse().getDestinations().size() != 0);
        return data;
    }

    private void getFlights(final String date) {
        int page = 0;
        do {
            callToGetAllFlightsByPage(page, date);
            page++;
        } while (schipholContext.getFlightsResponse().getFlights().size() != 0);
    }

    private void getDestinations() {
        int page = 0;
        do {
            callToGetAllDestinationsByPage(page);
            page++;
        } while (schipholContext.getDestinationsResponse().getDestinations().size() != 0);
    }

    private List<DestinationsData> findIata(final String country) {
        int page = 0;
        List<DestinationsData> data = new ArrayList<>();
        do {
            callToGetAllDestinationsByPage(page);
            page++;
            schipholContext.getDestinationsResponse().getDestinations()
                .stream()
                .filter(pt -> pt.getCountry().equalsIgnoreCase(country))
                .collect(Collectors.toCollection(() -> data));
        } while (page < schipholContext.getDestinationsResponseLinkedHashMap().size());
        return data;
    }

    private List<FlightsData> findFlights(final String date) {
        int page = 0;
        List<FlightsData> data = new ArrayList<>();
        do {
            callToGetAllFlightsByPage(page, date);
            page++;
            schipholContext.getFlightsResponse().getFlights()
                .stream()
                .collect(Collectors.toCollection(() -> data));
        } while (schipholContext.getFlightsResponse().getFlights().size() != 0);
        return data;
    }

    private void returnFlightsInfo(final List<FlightsData> data) {
        final int length = data.size();
        final var softly = new SoftAssertions();
        for (int i = 0; i < length; i++) {
            final var iataMainFromList = data.get(i).getAircraftType().getIataMain();
            final var destinationFromList = data.get(i).getRoute().getDestinations();
            final var departureTimeFromList = data.get(i).getScheduleDateTime();
            System.out.println("IATA code: " + iataMainFromList + " for destinations: " + destinationFromList + " departures " + departureTimeFromList);
            // if (null != iataMainFromList && null != destinationFromList) {
            // System.out.println("IATA code: " + iataMainFromList + " for destinations: " + destinationFromList + " departures " + departureTimeFromList);
            // }
            softly.assertThat(iataMainFromList) //failed 2021-11-11
                .as("IATA code is missing for today's flight to destinations: " + destinationFromList)
                .isNotNull();
        }
        softly.assertAll();
    }
}
