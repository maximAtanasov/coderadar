package io.reflectoring.coderadar.core.projectadministration.service.user;

import io.reflectoring.coderadar.core.projectadministration.UserNotFoundException;
import io.reflectoring.coderadar.core.projectadministration.domain.RefreshToken;
import io.reflectoring.coderadar.core.projectadministration.domain.User;
import io.reflectoring.coderadar.core.projectadministration.port.driven.user.LoadUserPort;
import io.reflectoring.coderadar.core.projectadministration.port.driven.user.LoginUserPort;
import io.reflectoring.coderadar.core.projectadministration.port.driven.user.RefreshTokenPort;
import io.reflectoring.coderadar.core.projectadministration.port.driver.user.login.LoginUserCommand;
import io.reflectoring.coderadar.core.projectadministration.port.driver.user.login.LoginUserResponse;
import io.reflectoring.coderadar.core.projectadministration.port.driver.user.login.LoginUserUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service("LoginUserService")
public class LoginUserService implements LoginUserUseCase {

  private final LoginUserPort port;
  private final LoadUserPort loadUserPort;
  private final RefreshTokenPort refreshTokenPort;
  private final AuthenticationManager authenticationManager;
  private final TokenService tokenService;

  @Autowired
  public LoginUserService(@Qualifier("LoginUserServiceNeo4j") LoginUserPort port, LoadUserPort loadUserPort, RefreshTokenPort refreshTokenPort, AuthenticationManager authenticationManager, TokenService tokenService) {
    this.port = port;
    this.loadUserPort = loadUserPort;
    this.refreshTokenPort = refreshTokenPort;
    this.authenticationManager = authenticationManager;
    this.tokenService = tokenService;
  }

  @Override
  public LoginUserResponse login(LoginUserCommand command) {
    Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            command.getUsername(), command.getPassword()));
    SecurityContextHolder.getContext().setAuthentication(authentication);

    Optional<User> user = loadUserPort.loadUserByUsername(command.getUsername());
    if(user.isPresent()){
      String accessToken = tokenService.generateAccessToken(user.get().getId(), user.get().getUsername());
      String refreshToken = tokenService.generateRefreshToken(user.get().getId(), user.get().getUsername());
      saveRefreshToken(user.get(), refreshToken);
      return new LoginUserResponse(accessToken, refreshToken);
    } else {
      throw new UserNotFoundException(command.getUsername());
    }
  }

  /**
   * Saves the refresh token with relation to user.
   *
   * @param user User, that is logged in.
   * @param refreshToken the new refresh token.
   */
  void saveRefreshToken(User user, String refreshToken) {
    RefreshToken refreshTokenEntity = new RefreshToken();
    refreshTokenEntity.setToken(refreshToken);
    refreshTokenEntity.setUser(user);
    refreshTokenPort.createAccessToken(refreshTokenEntity.getToken());
  }
}
