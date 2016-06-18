package io.vertx.blog.first;

import java.util.Collection;
import java.util.Optional;

interface WhiskyService {
	
	Whisky addOne(Whisky whisky);

	Optional<Whisky> getOne(int id);

	Optional<Whisky> deleteOne(int id);

	Collection<Whisky> getAll();

}
