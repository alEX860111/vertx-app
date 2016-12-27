package net.brainified.db;

import java.util.Optional;

import rx.Observable;

public interface UserDao {

  Observable<Optional<User>> searchUser(String name);

  Observable<Optional<User>> getUser(String id);

  Observable<User> addUser(User user);

}
