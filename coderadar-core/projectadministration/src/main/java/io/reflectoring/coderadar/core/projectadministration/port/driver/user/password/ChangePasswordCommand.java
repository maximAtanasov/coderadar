package io.reflectoring.coderadar.core.projectadministration.port.driver.user.password;

import javax.validation.constraints.NotBlank;

import io.reflectoring.coderadar.core.projectadministration.port.driver.user.ValidPassword;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordCommand {
  @NotBlank private String refreshToken;

  @NotBlank
  @Length(min = 8, max = 64)
  @ValidPassword
  private String newPassword;
}
