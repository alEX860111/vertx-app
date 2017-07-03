package net.brainified.http.login;

import java.util.Optional;

import rx.Observable;

interface LoginService {

  public Observable<Optional<LoginResponse>> login(LoginRequest loginRequest);

}