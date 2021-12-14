package models.schiphol;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class FlightsData {
    private String scheduleDateTime;
    private AircraftType aircraftType;
    private Route route;
}