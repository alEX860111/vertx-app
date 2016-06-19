package io.vertx.blog.first;

import java.util.Collection;
import java.util.Optional;

interface WhiskyService {

	Whisky addOne(Whisky whisky);

	Optional<Whisky> getOne(int id);

	Optional<Whisky> updateOne(int id, String name, String origin);

	Optional<Whisky> deleteOne(int id);

	Collection<Whisky> getAll();

}
