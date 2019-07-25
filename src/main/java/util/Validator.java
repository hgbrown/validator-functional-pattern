package util;

import java.util.function.Predicate;
import java.util.function.Supplier;

public interface Validator<T> {

    ValidatorSupplier on(T t);

    default Validator<T> thenValidate(Predicate<T> predicate, String errorMessage) {

        return t -> {
            final boolean currentValidationResult = predicate.test(t);

            try {
                on(t).validate();
                if (currentValidationResult) {
                    return () -> t;
                }
                return ValidationException.exceptionSupplier(errorMessage);
            } catch (ValidationException validationException) {
                if (!currentValidationResult) {
                    validationException.addSuppressed(new IllegalArgumentException(errorMessage));
                }
                return ValidationException.exceptionSupplier(validationException);
            }
        };
    }

    static <T> Validator<T> validate(Predicate<T> predicate, String errorMessage) {
        return t -> {
            if (predicate.test(t)) {
                return () -> t;
            }
            return ValidationException.exceptionSupplier(errorMessage);
        };
    }

    interface ValidatorSupplier<S> extends Supplier<S> {
        default S validate() {
            return get();
        }
    }

    class ValidationException extends RuntimeException {

        ValidationException(String message) {
            super(message);
        }

        static ValidationException fromErrorMessage(String errorMessage) {
            final ValidationException validationException = new ValidationException("Validation Failure");
            validationException.addSuppressed(new IllegalArgumentException(errorMessage));
            return validationException;
        }

        static ValidatorSupplier exceptionSupplier(String errorMessage) {
            return exceptionSupplier(ValidationException.fromErrorMessage(errorMessage));
        }

        static ValidatorSupplier exceptionSupplier(ValidationException validationException) {
            return () -> {
                throw validationException;
            };
        }
    }
}
