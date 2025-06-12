package probono.gcc.school.controller;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import probono.gcc.school.model.dto.TranslateRequestDTO;
import probono.gcc.school.model.dto.TranslateResponseDTO;
import probono.gcc.school.service.TranslateService;


@Slf4j
@RestController
@AllArgsConstructor
public class TranslateController {

  @Autowired
  private TranslateService translateService;

  private static final Logger logger = LoggerFactory.getLogger(TranslateController.class);

  @PostMapping("/translate")
  public ResponseEntity<TranslateResponseDTO> translate(@RequestBody TranslateRequestDTO translateRequestDTO) {
    logger.info("enter into TranslateController");
    TranslateResponseDTO translateResponseDTO = translateService.translateText(translateRequestDTO);
    logger.info("end translateText() in TranslateController");
    ResponseEntity<TranslateResponseDTO> response = ResponseEntity.ok(translateResponseDTO);
    logger.info("✅ 응답 ResponseEntity<TranslateResponseDTO>: {}", response);  // ✅ OK
    return ResponseEntity.ok(translateResponseDTO);

  }

  @PostConstruct
  public void initCache() {
    translateService.forcePutToCache();
  }

}
