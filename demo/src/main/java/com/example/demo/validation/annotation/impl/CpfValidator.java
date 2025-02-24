package com.example.demo.validation.annotation.impl;

import com.example.demo.validation.annotation.CPF;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CpfValidator implements ConstraintValidator<CPF, String> {
    
    @Override
    public void initialize(CPF constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true; // Considerar null ou vazio como válido ou não, depende da regra de negócio
        }

        String cpf = value.replaceAll("\\D", ""); // Remove todos os não dígitos

        if (cpf.length() != 11 || cpf.matches("(\\d)\\1{10}")) {
            return false;
        }

        // Validação dos dígitos verificadores
        int sum = 0;
        for (int i = 0; i < 9; i++) {
            sum += Character.getNumericValue(cpf.charAt(i)) * (10 - i);
        }

        int firstDigit = 11 - (sum % 11);
        if (firstDigit > 9) firstDigit = 0;

        if (firstDigit != Character.getNumericValue(cpf.charAt(9))) return false;

        sum = 0;
        for (int i = 0; i < 10; i++) {
            sum += Character.getNumericValue(cpf.charAt(i)) * (11 - i);
        }

        int secondDigit = 11 - (sum % 11);
        if (secondDigit > 9) secondDigit = 0;

        return secondDigit == Character.getNumericValue(cpf.charAt(10));
    }

}
