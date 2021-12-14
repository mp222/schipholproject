package models.schiphol;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class DestinationsData {
    private String city;
    private String country;
    private String iata;
    private PublicName publicName = new PublicName();
}