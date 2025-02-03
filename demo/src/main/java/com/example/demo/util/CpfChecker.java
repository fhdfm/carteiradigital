package com.example.demo.util;

public final class CpfChecker {

    private final String cpf; // Armazena o CPF conforme foi passado no construtor

    // Construtor que recebe o CPF e garante que não seja nulo
    public CpfChecker(String cpf) {

        if (cpf == null || cpf.isEmpty())
            throw new IllegalArgumentException("CPF não pode ser nulo ou vazio.");
            
        this.cpf = cpf;
    }

    /**
     * Remove todos os caracteres não numéricos, valida o CPF real e retorna somente os dígitos.
     *
     * @return o CPF contendo somente os 11 dígitos, se for válido
     * @throws IllegalArgumentException se o CPF for inválido
     */
    public String parse() {
        // Remove a formatação
        String digits = cpf.replaceAll("\\D", "");
        // Chama o método privado que valida o CPF
        validate(digits);
        return digits;
    }

    /**
     * Formata o CPF (assumindo que já está armazenado sem máscara, ou seja, contendo 11 dígitos)
     * para exibição com a máscara "###.###.###-##".
     *
     * @return o CPF formatado
     * @throws IllegalArgumentException se o CPF não tiver 11 dígitos
     */
    public String format() {
        return String.format("%s.%s.%s-%s",
                cpf.substring(0, 3),
                cpf.substring(3, 6),
                cpf.substring(6, 9),
                cpf.substring(9));
    }

    /**
     * Método privado que realiza a validação real do CPF.
     *
     * @param digits o CPF contendo somente os dígitos
     * @throws IllegalArgumentException se o CPF for inválido
     */
    private void validate(String digits) {
        if (digits.length() != 11) {
            throw new IllegalArgumentException("CPF deve conter 11 dígitos.");
        }
        if (digits.chars().distinct().count() == 1) {
            throw new IllegalArgumentException("CPF inválido: todos os dígitos são iguais.");
        }

        // Validação do primeiro dígito verificador
        int sum = 0;
        for (int i = 0; i < 9; i++) {
            int digit = Character.getNumericValue(digits.charAt(i));
            sum += digit * (10 - i);
        }
        int remainder = sum % 11;
        int firstVerifier = (remainder < 2) ? 0 : (11 - remainder);
        if (firstVerifier != Character.getNumericValue(digits.charAt(9))) {
            throw new IllegalArgumentException("CPF inválido: primeiro dígito verificador não confere.");
        }

        // Validação do segundo dígito verificador
        sum = 0;
        for (int i = 0; i < 10; i++) {
            int digit = Character.getNumericValue(digits.charAt(i));
            sum += digit * (11 - i);
        }
        remainder = sum % 11;
        int secondVerifier = (remainder < 2) ? 0 : (11 - remainder);
        if (secondVerifier != Character.getNumericValue(digits.charAt(10))) {
            throw new IllegalArgumentException("CPF inválido: segundo dígito verificador não confere.");
        }
    }
}
