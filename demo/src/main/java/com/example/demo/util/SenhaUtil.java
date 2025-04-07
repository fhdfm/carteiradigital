package com.example.demo.util;

import java.security.SecureRandom;
import java.util.Collections;
import java.util.List;

public class SenhaUtil {

    private SenhaUtil() {}

    /**
     * Gera uma senha temporária aleatória com tamanho fixo de 8 caracteres.
     *
     * <p>A senha gerada atende aos seguintes requisitos:
     * <ul>
     *   <li>Contém pelo menos um caractere maiúsculo</li>
     *   <li>Contém pelo menos um caractere minúsculo</li>
     *   <li>Contém pelo menos um dígito numérico</li>
     *   <li>Contém pelo menos um caractere especial</li>
     * </ul>
     * Os demais caracteres são escolhidos aleatoriamente dentre todos os caracteres permitidos.
     * A ordem dos caracteres é embaralhada para evitar qualquer padrão previsível.
     * </p>
     *
     * @return uma String contendo a senha temporária gerada.
     */
    public static String gerarSenhaTemporaria() {

        int tamanhoSenha = 8;

        String maiusculas = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String minusculas = "abcdefghijklmnopqrstuvwxyz";
        String numeros = "0123456789";
        String especiais = "!@#$%^&*()-_=+[]{}|;:,.<>?";
        String todos = maiusculas + minusculas + numeros + especiais;

        SecureRandom random = new SecureRandom();

        List<Character> senha = new java.util.ArrayList<>();

        // Adiciona obrigatoriamente um caractere de cada tipo
        senha.add(maiusculas.charAt(random.nextInt(maiusculas.length())));
        senha.add(minusculas.charAt(random.nextInt(minusculas.length())));
        senha.add(numeros.charAt(random.nextInt(numeros.length())));
        senha.add(especiais.charAt(random.nextInt(especiais.length())));

        for (int i = 4; i < tamanhoSenha; i++) {
            senha.add(todos.charAt(random.nextInt(todos.length())));
        }

        Collections.shuffle(senha, random);

        // Converte a lista de caracteres para String
        StringBuilder resultado = new StringBuilder(tamanhoSenha);
        for (char c : senha) {
            resultado.append(c);
        }

        return resultado.toString();
    }

    /**
     * Gera uma senha temporária de 4 dígitos numéricos, no estilo PIN de cartão.
     * <p>
     * A senha contém apenas números (0-9), podendo incluir zeros à esquerda.
     * Útil para autenticação rápida, senhas de uso único e etc.
     * </p>
     *
     * <pre>
     * Exemplo de saída:
     * gerarSenhaTemporariaCartao(); // "0742"
     * </pre>
     *
     * @return uma {@code String} com exatamente 4 dígitos numéricos aleatórios.
     */
    public static String gerarSenhaTemporariaPin() {
        SecureRandom random = new SecureRandom();
        StringBuilder senha = new StringBuilder(4);

        for (int i = 0; i < 4; i++) {
            senha.append(random.nextInt(10)); // de 0 a 9
        }

        return senha.toString();
    }

}
