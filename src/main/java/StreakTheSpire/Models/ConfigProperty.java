package StreakTheSpire.Models;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigProperty {
    String localisationID() default "";
    String groupID() default "";
    Class<?> type();
}
