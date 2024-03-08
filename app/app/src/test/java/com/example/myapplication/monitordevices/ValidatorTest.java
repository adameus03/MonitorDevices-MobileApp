package com.example.myapplication.monitordevices;

import junit.framework.TestCase;

public class ValidatorTest extends TestCase {

    public void testIsValidEmail() {
        String validEmail = "example@example.com";
        String invalidEmail1 = "";
        String invalidEmail2 = "example";
        String invalidEmail3 = "example.com";
        String invalidEmail4 = "example@example";

        Validator validator = new Validator();

        assertTrue(validator.isValidEmail(validEmail));
        assertFalse(validator.isValidEmail(invalidEmail1));
        assertFalse(validator.isValidEmail(invalidEmail2));
        assertFalse(validator.isValidEmail(invalidEmail3));
        assertFalse(validator.isValidEmail(invalidEmail4));
    }

    public void testIsValidPassword() {
        String validPassword = "password12";
        String invalidPassword1 = "a";

        Validator validator = new Validator();

        assertTrue(validator.isValidPassword(validPassword));
        assertFalse(validator.isValidPassword(invalidPassword1));
    }

    public void testIsValidRepeatedPassword() {
        String password = "password";
        String validRepeated = "password";
        String invalidRepeated = "invalid";

        Validator validator = new Validator();

        assertTrue(validator.isValidRepeatedPassword(password, validRepeated));
        assertFalse(validator.isValidRepeatedPassword(password, invalidRepeated));
    }

    public void testIsValidName() {
        String validName = "Name";
        String invalidName1 = "";
        String invalidName2 = "Name#";
        String invalidName3 = "NameNameNameNameNameNameNameNamee";

        Validator validator = new Validator();

        assertTrue(validator.isValidName(validName));
        assertFalse(validator.isValidName(invalidName1));
        assertFalse(validator.isValidName(invalidName2));
        assertFalse(validator.isValidName(invalidName3));
    }
}