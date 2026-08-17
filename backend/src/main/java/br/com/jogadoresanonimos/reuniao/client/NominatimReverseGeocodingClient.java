package br.com.jogadoresanonimos.reuniao.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

@Component
public class NominatimReverseGeocodingClient {

    private static final Logger log = LoggerFactory.getLogger(NominatimReverseGeocodingClient.class);
    private static final String REVERSE_URL = "https://nominatim.openstreetmap.org/reverse";
    private static final String USER_AGENT = "JogadoresAnonimosBrasil-Backend/1.0 (contato: contato@jogadoresanonimos.org.br)";

    private final RestTemplate restTemplate;

    public NominatimReverseGeocodingClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Optional<NominatimEnderecoResponse> reverseGeocode(double latitude, double longitude) {
        String url = UriComponentsBuilder.fromUriString(REVERSE_URL)
                .queryParam("format", "json")
                .queryParam("lat", latitude)
                .queryParam("lon", longitude)
                .queryParam("zoom", 18)
                .queryParam("addressdetails", 1)
                .toUriString();
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.USER_AGENT, USER_AGENT);
        try {
            var resposta = restTemplate.exchange(url, HttpMethod.GET,
                    new HttpEntity<Void>(headers), NominatimEnderecoResponse.class);
            return Optional.ofNullable(resposta.getBody());
        } catch (RestClientException e) {
            log.warn("nominatim_reverse_geocode_falha erro={}", e.getClass().getSimpleName());
            return Optional.empty();
        }
    }
}
