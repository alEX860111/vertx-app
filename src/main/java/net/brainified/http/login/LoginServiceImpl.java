package net.brainified.http.login;

import java.util.Optional;

import javax.inject.Inject;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.jwt.JWTOptions;
import io.vertx.rxjava.ext.auth.jwt.JWTAuth;
import net.brainified.db.Dao;
import net.brainified.db.User;
import rx.Observable;

final class LoginServiceImpl implements LoginService {

  private final Dao<User> userDao;

  private final HashService hashService;

  private final JWTAuth jwtAuth;

  @Inject
  public LoginServiceImpl(final Dao<User> userDao, final HashService hashService, final JWTAuth jwtAuth) {
    this.userDao = userDao;
    this.hashService = hashService;
    this.jwtAuth = jwtAuth;
  }

  @Override
  public Observable<Optional<LoginResponse>> login(final LoginRequest loginRequest) {
    return userDao.getByKey("username", loginRequest.getUsername()).map(userOptional -> {
      if (!userOptional.isPresent()) {
        return Optional.empty();
      }

      final User user = userOptional.get();
      final String passwordHash = hashService.hash(loginRequest.getPassword());

      if (user.getPasswordHash().equals(passwordHash)) {
        return Optional.of(createSession(user));
      } else {
        return Optional.empty();
      }
    });
  }

  private LoginResponse createSession(final User user) {
    final LoginResponse response = new LoginResponse();

    final String token = createToken(user);
    response.setToken(token);

    return response;
  }

  private String createToken(final User user) {
    final JsonObject claims = new JsonObject()
        .put("username", user.getUsername())
        .put("role", user.getRole())
        .put("userId", user.get_id());
    return jwtAuth.generateToken(claims, new JWTOptions());
  }

}
