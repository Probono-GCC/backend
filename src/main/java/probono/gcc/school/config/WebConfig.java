//package probono.gcc.school.config;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.CorsRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//@Configuration
//public class WebConfig implements WebMvcConfigurer {
//
//  private static final String DEVELOP_FRONT_ADDRESS = "http://localhost:3000";
//  private static final String PRODUCTION_FRONT_ADDRESS = "http://52.78.104.115:80"; // 배포 IP
//
//  @Override
//  public void addCorsMappings(CorsRegistry registry) {
//    registry.addMapping("/**")
//        .allowedOrigins(DEVELOP_FRONT_ADDRESS, PRODUCTION_FRONT_ADDRESS)
//        .allowedMethods("GET", "POST", "PUT", "DELETE")
//        .exposedHeaders("location")
//        .allowedHeaders("*")
//        .allowCredentials(true);
//  }
//}
