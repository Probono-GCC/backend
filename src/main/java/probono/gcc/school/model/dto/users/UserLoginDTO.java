package probono.gcc.school.model.dto.users;

import lombok.AllArgsConstructor;
import lombok.Data;
import probono.gcc.school.model.enums.Role;
import probono.gcc.school.model.projection.UserLoginProjection;

@Data
@AllArgsConstructor
public class UserLoginDTO implements UserLoginProjection {
  private String username;
  private String password;
  private Role role;

}
