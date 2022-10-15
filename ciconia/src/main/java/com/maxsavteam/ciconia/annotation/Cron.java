package com.maxsavteam.ciconia.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Cron {

	String value();

	FailurePolicy failurePolicy() default FailurePolicy.IGNORE;

	long retryTimeoutInSeconds() default 30;

	enum FailurePolicy {
		IGNORE, RETRY_AFTER_TIMEOUT, RETRY_IMMEDIATELY
	}

}
