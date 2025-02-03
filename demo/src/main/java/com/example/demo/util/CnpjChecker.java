package com.example.demo.util;

public final class CnpjChecker {

    private final String cnpj;


    // Construtor que recebe o CNPJ e garante que não seja nulo.
    public CnpjChecker(String cnpj) {

        if (cnpj == null || cnpj.isEmpty())
            throw new IllegalArgumentException("CNPJ não pode ser nulo ou vazio.");

        this.cnpj = cnpj;
    }

    /**
     * Método público que remove a formatação e retorna somente os dígitos do CNPJ.
     * Antes de retornar, chama o método privado validate para assegurar que o CNPJ é válido.
     *
     * @return uma String contendo somente os 14 dígitos do CNPJ
     * @throws IllegalArgumentException se o CNPJ for inválido
     */
    public String parse() {
        // Remove todos os caracteres que não são dígitos
        String digits = cnpj.replaceAll("\\D", "");
        // Valida o CNPJ utilizando o método privado
        validate(digits);
        return digits;
    }

    /**
     * Método público que formata um CNPJ (assumindo que já está sem máscara, ou seja, com 14 dígitos)
     * para exibição com a máscara "XX.XXX.XXX/XXXX-XX".
     *
     * @return o CNPJ formatado
     * @throws IllegalArgumentException se o CNPJ não tiver 14 dígitos
     */
    public String format() {
        return String.format("%s.%s.%s/%s-%s",
                cnpj.substring(0, 2),
                cnpj.substring(2, 5),
                cnpj.substring(5, 8),
                cnpj.substring(8, 12),
                cnpj.substring(12));
    }

    /**
     * Método privado que realiza a validação real do CNPJ.
     * Verifica se:
     * - Possui exatamente 14 dígitos.
     * - Não é composto por dígitos todos iguais.
     * - Os dígitos verificadores (13º e 14º dígitos) conferem de acordo com o algoritmo.
     *
     * @param digits uma String contendo somente os dígitos do CNPJ
     * @throws IllegalArgumentException se o CNPJ for inválido
     */
    private void validate(String digits) {
        // Verifica se o CNPJ tem 14 dígitos.
        if (digits.length() != 14) {
            throw new IllegalArgumentException("CNPJ deve conter 14 dígitos.");
        }

        // Verifica se todos os dígitos são iguais (ex.: "00000000000000" é inválido).
        if (digits.chars().distinct().count() == 1) {
            throw new IllegalArgumentException("CNPJ inválido: todos os dígitos são iguais.");
        }

        // Validação do primeiro dígito verificador:
        // Para os 12 primeiros dígitos, os pesos são: 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2.
        int sum = 0;
        int[] weight1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        for (int i = 0; i < 12; i++) {
            int digit = Character.getNumericValue(digits.charAt(i));
            sum += digit * weight1[i];
        }
        int remainder = sum % 11;
        int firstVerifier = (remainder < 2) ? 0 : (11 - remainder);
        if (firstVerifier != Character.getNumericValue(digits.charAt(12))) {
            throw new IllegalArgumentException("CNPJ inválido: primeiro dígito verificador não confere.");
        }

        // Validação do segundo dígito verificador:
        // Agora, para os 13 dígitos (os 12 originais + o primeiro dígito verificador),
        // os pesos são: 6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2.
        sum = 0;
        int[] weight2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        for (int i = 0; i < 13; i++) {
            int digit = Character.getNumericValue(digits.charAt(i));
            sum += digit * weight2[i];
        }
        remainder = sum % 11;
        int secondVerifier = (remainder < 2) ? 0 : (11 - remainder);
        if (secondVerifier != Character.getNumericValue(digits.charAt(13))) {
            throw new IllegalArgumentException("CNPJ inválido: segundo dígito verificador não confere.");
        }
    }
}
