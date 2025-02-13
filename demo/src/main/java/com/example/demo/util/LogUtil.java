package com.example.demo.util;

import com.example.demo.config.api.response.exception.EscolaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogUtil {

    public enum LogType {
        INFO, WARN, ERROR, DEBUG
    }

    // Construtor privado para evitar instanciação
    private LogUtil() {}

    /**
     * Realiza log com uma mensagem simples.
     *
     * @param clazz   a classe de onde o log está sendo chamado
     * @param type    o tipo de log (INFO, WARN, ERROR, DEBUG)
     * @param message a mensagem a ser logada
     */
    public static void log(Class<?> clazz, LogType type, String message) {
        Logger logger = LoggerFactory.getLogger(clazz);
        switch (type) {
            case WARN:
                logger.warn(message);
                break;
            case ERROR:
                logger.error(message);
                break;
            case DEBUG:
                logger.debug(message);
                break;
            default:
                logger.info(message);
                break;
        }
    }

    /**
     * Realiza log com mensagem e exceção.
     *
     * @param clazz     a classe de onde o log está sendo chamado
     * @param type      o tipo de log (INFO, WARN, ERROR, DEBUG)
     * @param EscolaException a exceção a ser logada
     */
    public static void log(Class<?> clazz, LogType type, EscolaException escolaException) {
        Logger logger = LoggerFactory.getLogger(clazz);
        switch (type) {
            case WARN:
                logger.warn(escolaException.getMessage(), escolaException);
                break;
            case ERROR:
                logger.error(escolaException.getMessage(), escolaException);
                break;
            case DEBUG:
                logger.debug(escolaException.getMessage(), escolaException);
                break;
            default:
                logger.info(escolaException.getMessage(), escolaException);
                break;
        }
    }
}
