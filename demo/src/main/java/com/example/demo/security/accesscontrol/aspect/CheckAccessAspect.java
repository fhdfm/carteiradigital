package com.example.demo.security.accesscontrol.aspect;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.domain.enums.Perfil;
import com.example.demo.exception.escola.EscolaException;
import com.example.demo.security.SecurityUtils;
import com.example.demo.security.UsuarioLogado;
import com.example.demo.security.accesscontrol.annotation.CheckAccess;
import com.example.demo.security.accesscontrol.policy.AccessPolicy;
import com.example.demo.security.accesscontrol.policy.AccessPolicyFactory;

@Aspect
@Component
public class CheckAccessAspect {
    
    private AccessPolicyFactory accessPolicyFactory;

    public CheckAccessAspect(AccessPolicyFactory accessPolicyFactory) {
        this.accessPolicyFactory = accessPolicyFactory;
    }

    @Around("@annotation(com.example.demo.security.accesscontrol.annotation.CheckAccess)")
    public Object enforceAccess(ProceedingJoinPoint joinPoint) throws Throwable {

        UsuarioLogado usuarioLogado = SecurityUtils.getUsuarioLogado();
        
        // Se é usuário MASTER tem acesso irrestrito.
        boolean isMaster = usuarioLogado.possuiPerfil(Perfil.MASTER);
        if (isMaster) return joinPoint.proceed();

        // Recupera a anotação e os parâmetros do método
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        CheckAccess checkAccess = method.getAnnotation(CheckAccess.class);

        String entity = checkAccess.entity();
        String paramName = checkAccess.paramName();

        // Recupera os parâmetros do método
        Object[] args = joinPoint.getArgs();
        Parameter[] parameters = method.getParameters();

        Object resourceId = null;

        // Procura no array de parâmetros qual deles representa a entidade
        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i].getName().equals(paramName)) {
                resourceId = args[i];
                break;
            }
        }

        if (resourceId == null)
            EscolaException.ofValidation("Parâmetro '" + paramName + "' não encontrado no método.");

        AccessPolicy policy = accessPolicyFactory.getPolicy(entity);

        String httpMethod = getHttpMethodFromAnnotations(method);

        if (!policy.hasAccess(usuarioLogado, httpMethod, resourceId))
            throw new AccessDeniedException("Acesso negado");

        return joinPoint.proceed();
    }

    private String getHttpMethodFromAnnotations(Method method) {
        if (method.isAnnotationPresent(GetMapping.class)) return "GET";
        if (method.isAnnotationPresent(PostMapping.class)) return "POST";
        if (method.isAnnotationPresent(PutMapping.class)) return "PUT";
        if (method.isAnnotationPresent(DeleteMapping.class)) return "DELETE";
        if (method.isAnnotationPresent(RequestMapping.class)) {
            RequestMapping mapping = method.getAnnotation(RequestMapping.class);
            if (mapping.method().length > 0) {
                return mapping.method()[0].name(); // Retorna o primeiro método HTTP definido
            }
        }
        return "UNKNOWN"; // Se não encontrou nenhuma anotação específica
    }    

}
