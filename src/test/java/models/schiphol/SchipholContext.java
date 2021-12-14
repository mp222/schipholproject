package models.schiphol;

import java.util.LinkedHashMap;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class SchipholContext {
    private SchipholHttpResponseValues schipholHttpResponseValues = new SchipholHttpResponseValues();
    private DestinationsResponse destinationsResponse = new DestinationsResponse();
    private FlightsResponse flightsResponse = new FlightsResponse();
    private LinkedHashMap<Integer, DestinationsResponse> destinationsResponseLinkedHashMap = new LinkedHashMap<>();
    private LinkedHashMap<Integer, FlightsResponse> flightsResponseLinkedHashMap = new LinkedHashMap<>();
}