package net.brainified.http.login;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;

final class HashServiceImpl implements HashService {

  @Override
  public String hash(final String string) {
    return Hashing.sha1().hashString(string, Charsets.UTF_8).toString();
  }

}
