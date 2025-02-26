package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.example.demo.domain.enums.MetodoAutenticacao;
import com.example.demo.domain.enums.Perfil;
import com.example.demo.dto.UsuarioRequest;
import com.example.demo.service.UsuarioService;

@SpringBootApplication
public class DemoApplication /*implements CommandLineRunner*/ {

	@Autowired
	private UsuarioService usuarioService;

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	public void run(String... args) {

		UsuarioRequest user = new UsuarioRequest(null, 
				"Harley Diniz", "fhdfm85@gmail.com", 
				"abc123", "96813822349", "85982021985", MetodoAutenticacao.SENHA, Perfil.MASTER);

		usuarioService.create(user);
	}

}
