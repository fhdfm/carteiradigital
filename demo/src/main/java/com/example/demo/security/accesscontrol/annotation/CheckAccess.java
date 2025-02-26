package com.example.demo.security.accesscontrol.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface CheckAccess {
    
    /**
     * Nome da entidade para a qual a política de acesso será aplicada (ex.: "escola").
     */
    String entity();
    
    /**
     * Nome do parâmetro do método que contém o identificador do recurso (default: "uuid").
     */
    String paramName() default "uuid";
}
