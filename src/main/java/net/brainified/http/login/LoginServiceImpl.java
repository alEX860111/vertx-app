package net.brainified.http.login;

import java.util.Optional;

import javax.inject.Inject;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.jwt.JWTOptions;
import io.vertx.rxjava.ext.auth.jwt.JWTAuth;
import net.brainified.db.Dao;
import net.brainified.db.User;
import rx.Observable;

final class LoginServiceImpl implements LoginService {

  private final Dao<User> userDao;

  private final JWTAuth jwtAuth;

  @Inject
  public LoginServiceImpl(final Dao<User> userDao, final JWTAuth jwtAuth) {
    this.userDao = userDao;
    this.jwtAuth = jwtAuth;
  }

  @Override
  public Observable<Optional<LoginResponse>> login(final LoginRequest loginRequest) {
    return userDao.getByKey("username", loginRequest.getUsername()).map(userOptional -> {
      if (!userOptional.isPresent()) {
        return Optional.empty();
      }

      final User user = userOptional.get();
      final String passwordHash = Hashing.sha1().hashString(loginRequest.getPassword(), Charsets.UTF_8).toString();

      if (user.getPasswordHash().equals(passwordHash)) {
        return Optional.of(createResponse(user));
      } else {
        return Optional.empty();
      }
    });
  }

  private LoginResponse createResponse(final User user) {
    final JsonObject claims = new JsonObject().put("username", user.getUsername()).put("role", user.getRole());
    final String token = jwtAuth.generateToken(claims, new JWTOptions());
    final LoginResponse loginResponse = new LoginResponse();
    loginResponse.setToken(token);
    return loginResponse;
  }

}
