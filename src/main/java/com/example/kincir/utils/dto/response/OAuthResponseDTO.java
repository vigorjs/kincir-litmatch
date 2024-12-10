package com.example.kincir.utils.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OAuthResponseDTO {

    @JsonProperty("access_token")
    private String access_token;

    @JsonProperty("token_type")
    private String token_type = "Bearer";

    @JsonProperty("id_token")
    private String id_token;

//    {
//        "access_token": "ya29.a0AcM612zX3L8pUZzMFOuOGHFfJy1dy1stKn_g6NYjP1gZG1rfY8A-Xcbi0JzkZa4ZaeHZNtWlz9wxo-GHOm3iuWB_VusBiEVz1mX_pDf4ut5l8qOmLxW955Gom5yzzxzQJG88N8RZmxci0KSFJf-scQlA-uCGwuIZ40E678NCaCgYKAWASARISFQHGX2MiPBfo8VniPBNmnhAKddlmvA0175",
//            "expires_in": 3599,
//            "scope": "https://www.googleapis.com/auth/userinfo.email https://www.googleapis.com/auth/userinfo.profile openid",
//            "token_type": "Bearer",
//            "id_token": "eyJhbGciOiJSUzI1NiIsImtpZCI6ImIyNjIwZDVlN2YxMzJiNTJhZmU4ODc1Y2RmMzc3NmMwNjQyNDlkMDQiLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJodHRwczovL2FjY291bnRzLmdvb2dsZS5jb20iLCJhenAiOiIzNzEzNTI0Mjk2MDYtbWZkY29uZzZxdjJ2MzE1OG8wbWtkc2QxMzdwY2Qyb28uYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJhdWQiOiIzNzEzNTI0Mjk2MDYtbWZkY29uZzZxdjJ2MzE1OG8wbWtkc2QxMzdwY2Qyb28uYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJzdWIiOiIxMDg3MzU5MzA0MDAyNTE1NjQ3MTgiLCJlbWFpbCI6Im1pcnVsaWRAZ21haWwuY29tIiwiZW1haWxfdmVyaWZpZWQiOnRydWUsImF0X2hhc2giOiJuTGFmYmpIc0ZmODNkOVF4eU1zbnJRIiwibmFtZSI6Ik11aGFtbWFkIEFtaXJ1bCIsInBpY3R1cmUiOiJodHRwczovL2xoMy5nb29nbGV1c2VyY29udGVudC5jb20vYS9BQ2c4b2NJdUhzYlJWd2pxZlJVTU9VS1EwaHZuT2NEeHFyTHNOX25XRkgxMGZmVDZBTE5nTzhhND1zOTYtYyIsImdpdmVuX25hbWUiOiJNdWhhbW1hZCIsImZhbWlseV9uYW1lIjoiQW1pcnVsIiwiaWF0IjoxNzI2NDI2MjUzLCJleHAiOjE3MjY0Mjk4NTN9.SA285REcAnhr-Z5x76RqpuD9ObPS9xgVUS8-LcbnMeBvORIr6DOCosBRlrIbnZFcVw_ylsDr702YYpIv1paAzbLISEp1cvIolVoAXCdCK_4g79XVEaisYuee1LDIg1r9X-1pfe_eF6y8rImIe68GAD4fxWBrXdH6949LxsjRoYH0ZKRoLrASWkcz0l9ZbXvruhkdqOswHwg5xpDTOk5W2OoErZ-gKbIde0akAGs9k5WzxwpQ5OwuZ-dFCFSBSbJPWWvq1xeoK14O6rw2Wg57YKiYzuc-Tt0wcqJ9cTI_xZHv2xeW4GfxliQhcfT5qew8In0cDxtmUThBNIDFDLPU0w"
//    }
}
