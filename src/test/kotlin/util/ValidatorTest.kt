package util

import domain.Person
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import java.util.function.Predicate

internal class ValidatorTest {

    @Nested
    inner class SingleValidationOnValidInstanceShould {

        @Test
        internal fun `return instance being validated`() {
            val sarah = Person("Sarah", 29)

            val validatedSarah = Validator
                .validate(NAME_SHOULD_NOT_BE_NULL, "The name should not be null")
                .on(sarah)
                .validate()

            assertThat(validatedSarah).isEqualTo(sarah)
        }

    }

    @Nested
    inner class SingleValidationOnInvalidInstanceShould {

        @Test
        internal fun `be able to detect when name is null`() {
            val james = Person(null, 29)
            val errorMessage = "The name should not be null"

            val e = assertThrows(Validator.ValidationException::class.java) {
                Validator
                    .validate(NAME_SHOULD_NOT_BE_NULL, errorMessage)
                    .on(james)
                    .validate()
            }

            assertThat(e.suppressed.first().message).isEqualTo(errorMessage)
        }

        @Test
        internal fun `be able to detect age less than zero`() {
            val mary = Person("Mary", -10)
            val errorMessage = "Age should be greater than zero"

            val e = assertThrows(Validator.ValidationException::class.java) {
                Validator
                    .validate(AGE_SHOULD_BE_GREATER_THAN_ZERO, errorMessage)
                    .on(mary)
                    .validate()
            }

            assertThat(e.suppressed.first().message).isEqualTo(errorMessage)
        }

        @Test
        internal fun `be able to detect age greater than 150`() {
            val john = Person("John", 1_000)
            val errorMessage = "Age should be less than 150"

            val e = assertThrows(Validator.ValidationException::class.java) {
                Validator
                    .validate(AGE_SHOULD_BE_LESS_THAN_150, errorMessage)
                    .on(john)
                    .validate()
            }

            assertThat(e.suppressed.first().message).isEqualTo(errorMessage)
        }
    }

    @Nested
    inner class MultipleValidationOnInvalidInstanceShould {

        @Test
        internal fun `be able to detect that name is null and age is greater than 150`() {
            val linda = Person(null, 1_000)
            val nameErrorMessage = "Name should not be null"
            val ageErrorMessage = "Age should be less than 150"

            val e = assertThrows(Validator.ValidationException::class.java) {
                Validator
                    .validate(NAME_SHOULD_NOT_BE_NULL, nameErrorMessage)
                    .thenValidate(AGE_SHOULD_BE_LESS_THAN_150, ageErrorMessage)
                    .on(linda)
                    .validate()
            }

            assertThat(e.suppressed.first().message).isEqualTo(nameErrorMessage)
            assertThat(e.suppressed.last().message).isEqualTo(ageErrorMessage)
        }

        @Test
        internal fun `be able to detect that name is null and age is less than zero`() {
            val zack = Person(null, -10)
            val nameErrorMessage = "Name should not be null"
            val ageErrorMessage = "Age should be greater than 0"

            val e = assertThrows(Validator.ValidationException::class.java) {
                Validator
                    .validate(NAME_SHOULD_NOT_BE_NULL, nameErrorMessage)
                    .thenValidate(AGE_SHOULD_BE_GREATER_THAN_ZERO, ageErrorMessage)
                    .on(zack)
                    .validate()
            }

            assertThat(e.suppressed.first().message).isEqualTo(nameErrorMessage)
            assertThat(e.suppressed.last().message).isEqualTo(ageErrorMessage)
        }

        @Test
        internal fun `be able to reuse validator to validate multiple instances`() {
            val validator: Validator<Person> = Validator
                .validate(NAME_SHOULD_NOT_BE_NULL, "Name should not be null")
                .thenValidate(AGE_SHOULD_BE_GREATER_THAN_ZERO, "Age should be greater than 0")
                .thenValidate(AGE_SHOULD_BE_LESS_THAN_150, "Age should be less than 150")

            val adam = Person("Adam", 29)
            val eve = Person("Eve", 28)

            assertAll(
                { assertThat(adam).isEqualTo(validator.on(adam).validate()) },
                { assertThat(eve).isEqualTo(validator.on(eve).validate()) }
            )
        }
    }

    companion object {
        val NAME_SHOULD_NOT_BE_NULL: Predicate<Person> = Predicate { p: Person -> p.name != null }
        val AGE_SHOULD_BE_GREATER_THAN_ZERO: Predicate<Person> = Predicate { p: Person -> p.age > 0 }
        val AGE_SHOULD_BE_LESS_THAN_150: Predicate<Person> = Predicate { p: Person -> p.age < 150 }
    }

}
