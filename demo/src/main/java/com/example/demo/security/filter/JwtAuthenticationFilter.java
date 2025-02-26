package com.example.demo.security.filter;

import java.io.IOException;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.demo.security.JwtService;
import com.example.demo.security.UsuarioLogado;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request, 
            @NonNull HttpServletResponse response, 
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        SecurityContext context = SecurityContextHolder.getContext();

        String jwtToken = request.getHeader("Authorization");

        if (jwtToken == null || context.getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        jwtToken = jwtToken.substring(7);

        String username = jwtService.extractUsername(jwtToken);
        if (username != null 
            && jwtService.isTokenValid(jwtToken)) {
            
            UsuarioLogado user = (UsuarioLogado) userDetailsService.loadUserByUsername(username);
            
            var authToken =
                new UsernamePasswordAuthenticationToken(
                    user, null, user.getAuthorities());
                    
            authToken.setDetails(
                    new WebAuthenticationDetailsSource()
                            .buildDetails(request));

            context.setAuthentication(authToken);
        }

        filterChain.doFilter(request, response);
            
    }

    
}
