package models.schiphol;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SchipholHttpResponseValues {
    private String responseBody;
    private int statusCode;
    private String contentType;
}