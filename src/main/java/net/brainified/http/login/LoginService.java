package net.brainified.http.login;

import java.util.Optional;

import rx.Single;

interface LoginService {

  public Single<Optional<LoginResponse>> login(LoginRequest loginRequest);

}