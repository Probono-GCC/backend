package probono.gcc.school.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisConnectionChecker {
  private final RedisConnectionFactory redisConnectionFactory;

  @PostConstruct
  public void checkRedisConnection() {
    try {
      String pong = redisConnectionFactory.getConnection().ping();
      System.out.println("✅ Redis 연결 성공: " + pong);
    } catch (Exception e) {
      System.err.println("❌ Redis 연결 실패: " + e.getMessage());
    }
  }

}
