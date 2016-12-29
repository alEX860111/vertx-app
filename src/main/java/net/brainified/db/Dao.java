package net.brainified.db;

import java.util.List;
import java.util.Optional;

import rx.Observable;

public interface Dao<T> {

  Observable<Long> getCount();

  Observable<List<T>> getList(Integer page, Integer perpage);

  Observable<Optional<T>> getById(String id);

  Observable<Optional<T>> getByKey(String key, String value);

  Observable<T> add(T object);

  Observable<Long> update(T object);

  Observable<Long> delete(String id);

}