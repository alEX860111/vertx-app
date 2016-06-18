package io.vertx.blog.first;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

final class WhiskyServiceImpl implements WhiskyService {

	private final Map<Integer, Whisky> whiskies = new LinkedHashMap<>();

	public WhiskyServiceImpl() {
		final Whisky bowmore = new Whisky("Bowmore 15 Years Laimrig", "Scotland, Islay");
		whiskies.put(bowmore.getId(), bowmore);
		final Whisky talisker = new Whisky("Talisker 57° North", "Scotland, Island");
		whiskies.put(talisker.getId(), talisker);
	}

	@Override
	public Whisky addOne(final Whisky whisky) {
		whiskies.put(whisky.getId(), whisky);
		return whisky;
	}

	@Override
	public Optional<Whisky> getOne(final int id) {
		return Optional.ofNullable(whiskies.get(id));
	}

	@Override
	public Optional<Whisky> deleteOne(final int id) {
		return Optional.ofNullable(whiskies.remove(id));
	}

	@Override
	public Collection<Whisky> getAll() {
		return whiskies.values();
	}

}
