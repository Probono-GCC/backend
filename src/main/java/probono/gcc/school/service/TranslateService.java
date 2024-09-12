package probono.gcc.school.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import probono.gcc.school.model.dto.TranslateRequestDTO;
import probono.gcc.school.model.dto.TranslateResponseDTO;

@Service
@Slf4j
public class TranslateService {

  @Autowired
  private RestTemplate restTemplate;
  @Autowired
  private ObjectMapper objectMapper;  // ObjectMapper를 사용해 JSON 파싱

  public TranslateResponseDTO translateText(TranslateRequestDTO translateRequestDTO) {
    log.info("into translateText in TranslateService!!!!");
    String url = "http://localhost:8000/translate";

    // 헤더 설정
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    // 요청 본문 설정
    HttpEntity<TranslateRequestDTO> entity = new HttpEntity<>(translateRequestDTO, headers);

    // POST 요청 보내기
    ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

    log.info("response.getStatusCode() : {}",response.getStatusCode());

    // 응답 처리
    TranslateResponseDTO translateResponseDTO = new TranslateResponseDTO();

    if (response.getStatusCode() == HttpStatus.OK) {
      try {
        // JSON 응답을 Map으로 파싱
        Map<String, Object> responseBody = objectMapper.readValue(response.getBody(), Map.class);


        // 특정 키에 접근하여 DTO에 값 설정
        translateResponseDTO.setTranslatedText((String) responseBody.get("translatedText"));
        translateResponseDTO.setStatus(String.valueOf(responseBody.get("status"))); // Boolean to String
        translateResponseDTO.setMessage((String) responseBody.get("message"));

      } catch (Exception e) {
        log.info("Failed to parse translation response : "+translateRequestDTO.getText());
        throw new IllegalStateException("Failed to parse translation response", e);
      }
    } else {
      log.info("Failed to translate : "+translateRequestDTO.getText());
      throw new IllegalStateException("Failed to translate: " + response.getStatusCode());
    }

    return translateResponseDTO;


  }
}
