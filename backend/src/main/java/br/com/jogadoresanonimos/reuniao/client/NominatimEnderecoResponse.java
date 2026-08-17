package br.com.jogadoresanonimos.reuniao.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record NominatimEnderecoResponse(NominatimAddress address) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record NominatimAddress(String state, String city, String town, String municipality,
                                    String village, String suburb, String neighbourhood,
                                    @JsonProperty("city_district") String cityDistrict) {

        public String cidadeResolvida() {
            if (city != null && !city.isBlank()) {
                return city;
            }
            if (town != null && !town.isBlank()) {
                return town;
            }
            if (municipality != null && !municipality.isBlank()) {
                return municipality;
            }
            if (village != null && !village.isBlank()) {
                return village;
            }
            return null;
        }

        public String bairroResolvido() {
            if (suburb != null && !suburb.isBlank()) {
                return suburb;
            }
            if (neighbourhood != null && !neighbourhood.isBlank()) {
                return neighbourhood;
            }
            if (cityDistrict != null && !cityDistrict.isBlank()) {
                return cityDistrict;
            }
            return null;
        }
    }
}
