package africa.semicolon.safereportbackend.configs;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PasswordValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPassword {
String message() default
        "A valid Password must be at least 8 characters long and include at least 1 uppercase, 1 lowercase, 1 digit, and 1 special character";
Class<?>[] groups() default {};
Class<? extends Payload>[] payload() default {};
}
