package net.brainified.db;

import java.util.Optional;

import rx.Observable;

public interface Dao<T> {

  Observable<ItemContainer<T>> getList(Integer page, Integer perpage, String sortKey, SortOrder sortOrder);

  Observable<Optional<T>> getById(String id);

  Observable<Optional<T>> getByKey(String key, String value);

  Observable<T> add(T object);

  Observable<Boolean> update(T object);

  Observable<Boolean> delete(String id);

}