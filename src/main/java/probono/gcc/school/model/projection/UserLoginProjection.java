package probono.gcc.school.model.projection;

import probono.gcc.school.model.enums.Role;

public interface UserLoginProjection {
  String getUsername();
  String getPassword();
  Role getRole();
}
