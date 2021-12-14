package models.schiphol;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FlightsResponse {
    private List<FlightsData> flights;
}