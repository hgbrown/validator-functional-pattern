![build status](https://travis-ci.org/hgbrown/validator-functional-pattern.svg?branch=master)

# Functional Fluent Validation Pattern using Lambdas

The pattern uses partial application and lambdas to build a validator that can be used using a 
fluent interface to specify the validation rules and the object on which the validation is to be performed.

The validator can perform multiple validations on a single object and will wrap up all of these exceptions in a single exception. 

The `Person` class represents a bean that has some validation requirements. These requirements are:
- the name cannot be null
- the age must be greater than zero
- the age must be less than 150

The `Validator` interface implements the validation pattern.

The `ValidatorTest` class demonstrates how the Validator can be used to perform validation.

The test class is implemented using Kotlin, while the `Person` and `Validator` is implemented in Java.

## Software Used

- Gradle (4.10.3)
- Java (8)
- Kotlin (1.3.41)
- JUnit (5)
- AssertJ (3.12.2)