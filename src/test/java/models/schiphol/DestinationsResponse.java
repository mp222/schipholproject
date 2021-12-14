package models.schiphol;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DestinationsResponse {
    private List<DestinationsData> destinations;
}