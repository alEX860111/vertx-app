package net.brainified.db;

import java.util.List;
import java.util.Optional;

import rx.Observable;

public interface Dao<T extends MongoObject> {

  Observable<Long> getCount();

  Observable<List<T>> getList(Integer page, Integer perpage);

  Observable<Optional<T>> get(String id);

  Observable<T> add(T object);

  Observable<Long> update(String id, T object);

  Observable<Long> delete(String id);

}