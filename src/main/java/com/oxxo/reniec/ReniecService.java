package com.oxxo.reniec;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class ReniecService {

  private final ReniecProperties props;
  private final RestTemplate restTemplate = new RestTemplate();

  public ReniecDto consultarDni(String dni) {
    // DNI peruano: 8 dígitos
    if (!dni.matches("\\d{8}")) {
      return null;
    }

    // https://api.decolecta.com/v1/reniec/dni?numero=XXXXXXX
    String url = UriComponentsBuilder
        .fromHttpUrl(props.getUrl())
        .queryParam("numero", dni)
        .toUriString();

    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + props.getToken());
    headers.set("Accept", "application/json");

    HttpEntity<Void> entity = new HttpEntity<>(headers);

    try {
      ResponseEntity<DecolectaDniResponse> resp = restTemplate.exchange(
          url,
          HttpMethod.GET,
          entity,
          DecolectaDniResponse.class);

      if (!resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null) {
        return null; // 401, 404, etc → devolvemos null
      }

      return resp.getBody().toDto();

    } catch (Exception e) {
      // error de red o lo que sea → devolvemos null, no reventamos
      return null;
    }
  }
}