package com.example.demo.service;

import com.example.demo.dto.email.EmailDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TesteService {
    @Autowired
    EmailService service;
    @Scheduled(fixedDelay = 10000)
    public void testeEnvio() {
        service.sendEmail(
                new EmailDto(
                        "Sua senha é CLÉBINHO",
                        List.of("igoredm@gmail.com"),
                        List.of(),
                        List.of(),
                        "Sua senha chegou!",
                        List.of(),
                        null
                )
        );
    }
}
