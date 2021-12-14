@Test

Feature: Schiphol

  Scenario: Test_1
    When get all destinations sorted by country ascending
    Then results contain city "Sydney" and country "Australia"

  Scenario: Test_2
    When get a list off all IATA codes for country "Australia" and return with destination city

  Scenario: Test_3
    When find and return information about the IATA code for each flight and verify there is an IATA code

  Scenario: Test_4
    When call API with incorrect key
    Then call is unsuccessful with http status "403"