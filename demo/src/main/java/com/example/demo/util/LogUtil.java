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
    public static void log(Class<?> clazz, LogType type, String message, Object... args) {
        Logger logger = LoggerFactory.getLogger(clazz);
        switch (type) {
            case WARN:
                logger.warn(message, args);
                break;
            case ERROR:
                logger.error(message, args);
                break;
            case DEBUG:
                logger.debug(message, args);
                break;
            default:
                logger.info(message, args);
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
    public static void info(String message, Object... args) {
        log(getCallerClass(), LogType.INFO, message, args);
    }

    /**
     * Realiza log com uma mensagem simples.
     *
     * @param message a mensagem a ser logada
     */
    public static void error(String message, Object... args) {
        log(getCallerClass(), LogType.ERROR, message, args);
    }


    /**
     * Pega a classe que chamou o info.
     */
    private static Class<?> getCallerClass() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (int i = 3; i < stackTrace.length; i++) {
            String className = stackTrace[i].getClassName();
            if (!className.equals(LogUtil.class.getName())) {
                try {
                    return Class.forName(className);
                } catch (ClassNotFoundException e) {
                    // Em último caso, volta pro LogUtil mesmo
                    return LogUtil.class;
                }
            }
        }
        return LogUtil.class;
    }

}
