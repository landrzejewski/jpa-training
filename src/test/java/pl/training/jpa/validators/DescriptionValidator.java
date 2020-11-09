package pl.training.jpa.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class DescriptionValidator implements ConstraintValidator<Description, String> {

    private int minLength;

    public void initialize(Description constraint) {
        minLength = constraint.minLength();
    }

    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value.length() >= minLength;
    }

}
