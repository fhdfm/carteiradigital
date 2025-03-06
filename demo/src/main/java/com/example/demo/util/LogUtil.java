package com.example.demo.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.demo.exception.eureka.EurekaException;

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
     * @param EurekaException a exceção a ser logada
     */
    public static void log(Class<?> clazz, LogType type, EurekaException eurekaException) {
        Logger logger = LoggerFactory.getLogger(clazz);
        switch (type) {
            case WARN:
                logger.warn(eurekaException.getMessage(), eurekaException);
                break;
            case ERROR:
                logger.error(eurekaException.getMessage(), eurekaException);
                break;
            case DEBUG:
                logger.debug(eurekaException.getMessage(), eurekaException);
                break;
            default:
                logger.info(eurekaException.getMessage(), eurekaException);
                break;
        }
    }
}
