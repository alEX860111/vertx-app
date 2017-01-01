package net.brainified.http.login;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.jwt.JWTOptions;
import io.vertx.rxjava.ext.auth.jwt.JWTAuth;
import net.brainified.db.Dao;
import net.brainified.db.User;
import rx.Observable;

@RunWith(MockitoJUnitRunner.class)
public class LoginServiceImplTest {

  private static final String TOKEN = "token";

  @Mock
  private Dao<User> userDao;

  @Mock
  private HashService hashService;

  @Mock
  private JWTAuth jwtAuth;

  @InjectMocks
  private LoginServiceImpl loginService;

  private LoginRequest loginRequest;

  private User user;

  @Before
  public void setUp() {
    loginRequest = new LoginRequest();
    loginRequest.setUsername("zoe");
    loginRequest.setPassword("password");

    user = new User();
    user.setUsername("zoe");
    user.setPasswordHash("passwordHash");
  }

  @Test
  public void testLogin() {
    when(userDao.getByKey("username", "zoe")).thenReturn(Observable.just(Optional.of(user)));
    when(hashService.hash("password")).thenReturn("passwordHash");
    when(jwtAuth.generateToken(any(JsonObject.class), any(JWTOptions.class))).thenReturn(TOKEN);

    loginService.login(loginRequest).subscribe(loginResponseOptional -> {
      assertTrue(loginResponseOptional.isPresent());
      assertEquals(TOKEN, loginResponseOptional.get().getToken());
    });
  }

  @Test
  public void testLogin_UserNotFound() {
    when(userDao.getByKey("username", "zoe")).thenReturn(Observable.just(Optional.empty()));

    loginService.login(loginRequest).subscribe(loginResponseOptional -> {
      assertFalse(loginResponseOptional.isPresent());
    });

    verifyZeroInteractions(hashService, jwtAuth);
  }

  @Test
  public void testLogin_PasswordDoNotMatch() {
    when(userDao.getByKey("username", "zoe")).thenReturn(Observable.just(Optional.of(user)));
    when(hashService.hash("password")).thenReturn("some value");

    loginService.login(loginRequest).subscribe(loginResponseOptional -> {
      assertFalse(loginResponseOptional.isPresent());
    });

    verifyZeroInteractions(jwtAuth);
  }

}
