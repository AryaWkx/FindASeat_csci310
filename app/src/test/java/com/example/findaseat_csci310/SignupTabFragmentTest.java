package com.example.findaseat_csci310;

import com.example.findaseat_csci310.SignupTabFragment;
import org.junit.Test;
import static org.junit.Assert.*;

public class SignupTabFragmentTest {

    @Test
    public void emailValidator() {
        assertTrue(SignupTabFragment.isEmailValid("test@example.com"));
        assertTrue(SignupTabFragment.isEmailValid("test@mail.example.com"));
        assertFalse(SignupTabFragment.isEmailValid("testexample"));
        assertFalse(SignupTabFragment.isEmailValid(null));
        assertFalse(SignupTabFragment.isEmailValid(""));
    }

    @Test
    public void idValidator() {
        assertTrue(SignupTabFragment.isIDValid("1234567890"));
        assertFalse(SignupTabFragment.isIDValid("123456789"));
        assertFalse(SignupTabFragment.isIDValid("12345678901"));
        assertFalse(SignupTabFragment.isIDValid(""));
    }

    @Test
    public void pwdValidator() {
        assertTrue(SignupTabFragment.isPwdValid("1234qqweee"));
        assertTrue(SignupTabFragment.isPwdValid("123456789"));
        assertFalse(SignupTabFragment.isPwdValid("17891"));
        assertFalse(SignupTabFragment.isPwdValid(""));
    }

    @Test
    public void affValidator() {
        assertTrue(SignupTabFragment.isAffValid("Student"));
        assertTrue(SignupTabFragment.isAffValid("Faculty"));
        assertFalse(SignupTabFragment.isAffValid("123456789"));
        assertFalse(SignupTabFragment.isPwdValid("student"));
        assertFalse(SignupTabFragment.isPwdValid(""));
    }
}

