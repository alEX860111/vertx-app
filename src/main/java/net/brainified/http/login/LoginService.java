package net.brainified.http.login;

import java.util.Optional;

import rx.Observable;

interface LoginService {

  public Observable<Optional<Session>> login(LoginRequest loginRequest);

}