package net.brainified.http;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.vertx.core.http.HttpMethod;
import net.brainified.db.Role;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface HandlerConfiguration {
  String path();
  HttpMethod method();
  Role[] allowedRoles() default {};
}
