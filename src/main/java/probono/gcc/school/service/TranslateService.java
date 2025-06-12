package probono.gcc.school.service;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
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



  @Cacheable(value = "translations", key = "#translateRequestDTO.text + '_' + #translateRequestDTO.to")
  public TranslateResponseDTO translateText(TranslateRequestDTO translateRequestDTO) {
    log.info("into translateText in TranslateService!!!!");
    //String url = "http://172.31.47.247:8000/translate"; //blue-green docker 구조로 localhost->private ip
    String url = "http://192.168.45.188:8000/translate"; //blue-green docker 구조로 localhost->private ip , mac에서 test

    log.info("url : {}",url);

    // 헤더 설정
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    // 요청 본문 설정
    HttpEntity<TranslateRequestDTO> entity = new HttpEntity<>(translateRequestDTO, headers);

    // POST 요청 보내기
    ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

    log.info("response.getStatusCode() : {}",response.getStatusCode());
    log.info("🌐 응답 body: {}", response.getBody());

    // 응답 처리
    TranslateResponseDTO translateResponseDTO = new TranslateResponseDTO();
    //log.info("🔄 응답 DTO: {}", translateResponseDTO);


    if (response.getStatusCode() == HttpStatus.OK) {
      try {
         translateResponseDTO =
            objectMapper.readValue(response.getBody(), TranslateResponseDTO.class);
        log.info("✅ 직렬화된 DTO: {}", translateResponseDTO);

//        // JSON 응답을 Map으로 파싱
//        Map<String, Object> responseBody = objectMapper.readValue(response.getBody(), Map.class);
//
//
//        // 특정 키에 접근하여 DTO에 값 설정
//        translateResponseDTO.setTranslatedText((String) responseBody.get("translatedText"));
//        translateResponseDTO.setStatus(String.valueOf(responseBody.get("status"))); // Boolean to String
//        translateResponseDTO.setMessage((String) responseBody.get("message"));

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
  // 기존 translateText() 아래에 추가
  @CachePut(value = "translations", key = "'test_ko'")
  public TranslateResponseDTO forcePutToCache() {
    log.info("✅ forcePutToCache 실행");
    return new TranslateResponseDTO("테스트", true, "");
  }

}
