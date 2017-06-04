package net.brainified.http;

import java.util.Optional;

interface StatusCodeRegistry {

  Optional<Integer> getStatusCode(Throwable throwable);

}