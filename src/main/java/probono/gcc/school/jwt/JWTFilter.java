package probono.gcc.school.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import probono.gcc.school.model.dto.CustomUserDetails;
import probono.gcc.school.model.dto.users.UserLoginDTO;
import probono.gcc.school.model.entity.Users;
import probono.gcc.school.model.enums.Role;

@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

  private final JWTUtil jwtUtil;


  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    //request에서 Authorization 헤더를 찾음
    String authorization = request.getHeader("Authorization");

    //Authorization 헤더 검증
    if (authorization == null || !authorization.startsWith("Bearer ")) {

      System.out.println("token null");
      filterChain.doFilter(request, response);

      //조건이 해당되면 메소드 종료 (필수)
      return;
    }

    System.out.println("authorization now");
    //Bearer 부분 제거 후 순수 토큰만 획득
    String token = authorization.split(" ")[1];

    //토큰 소멸 시간 검증
    if (jwtUtil.isExpired(token)) {

      System.out.println("token expired");
      filterChain.doFilter(request, response);

      //조건이 해당되면 메소드 종료 (필수)
      return;
    }

    //토큰에서 username과 role 획득
    String username = jwtUtil.getUsername(token);
    String role = jwtUtil.getRole(token);

    //userEntity를 생성하여 값 set
    //Users user = new Users();
//    user.setUsername(username);
//    user.setPassword("temppassword");
//    user.setRole(Role.valueOf(role));

    UserLoginDTO user = new UserLoginDTO(username, "temppassword", Role.valueOf(role));

    //UserDetails에 회원 정보 객체 담기
    CustomUserDetails customUserDetails = new CustomUserDetails(user);

    //스프링 시큐리티 인증 토큰 생성
    Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null,
        customUserDetails.getAuthorities());

    //세션에 사용자 등록
    SecurityContextHolder.getContext().setAuthentication(authToken);

    filterChain.doFilter(request, response);
  }
}
