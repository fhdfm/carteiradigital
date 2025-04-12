package com.example.demo.service;

import com.example.demo.domain.enums.MetodoAutenticacao;
import com.example.demo.domain.enums.Perfil;
import com.example.demo.domain.enums.Status;
import com.example.demo.domain.model.*;
import com.example.demo.domain.model.carteira.Carteira;
import com.example.demo.dto.AlunoRequest;
import com.example.demo.dto.email.EmailDto;
import com.example.demo.dto.projection.aluno.AlunoSummary;
import com.example.demo.exception.eureka.EurekaException;
import com.example.demo.repository.AlunoRepository;
import com.example.demo.repository.CarteiraRepository;
import com.example.demo.repository.specification.AlunoSpecification;
import com.example.demo.security.SecurityUtils;
import com.example.demo.security.UsuarioLogado;
import com.example.demo.util.SenhaUtil;
import com.example.demo.util.Util;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class AlunoService {

    private final AlunoRepository alunoRepository;
    private final PasswordEncoder passwordEncoder;
    private final CarteiraRepository carteiraRepository;
    private final EmailService emailService;


    public AlunoService(AlunoRepository alunoRepository, PasswordEncoder passwordEncoder, CarteiraRepository carteiraRepository, EmailService emailService) {
        this.alunoRepository = alunoRepository;
        this.passwordEncoder = passwordEncoder;
        this.carteiraRepository = carteiraRepository;
        this.emailService = emailService;
    }

    public Aluno findByUuid(UUID alunoId) {
        return this.alunoRepository.findByUuid(alunoId).orElseThrow(
                () -> EurekaException.ofNotFound("Aluno não encontrado."));
    }

    public Aluno findStudentWithResponsaveisByUuid(UUID alunoId) {
        return this.alunoRepository.findWithResponsaveisByUuid(alunoId).orElseThrow(
                () -> EurekaException.ofNotFound("Aluno não encontrado."));
    }

    public UUID create(AlunoRequest request) {
        UsuarioLogado usuarioLogado = SecurityUtils.getUsuarioLogado();

        String email = null;
        if (request.email() != null) {
            email = request.email().trim();
            if (this.alunoRepository.existsByEmail(email))
                throw EurekaException.ofValidation(email + " já está cadastrado");
        }

        String cpf = request.cpf().trim().replaceAll("\\D", "");
        if (this.alunoRepository.existsByCpf(cpf))
            throw EurekaException.ofValidation(cpf + " já está cadastrado");

        String senha = Util.gerarSenhaTemporaria();

        Escola escola = usuarioLogado.getEscola();
//        escola.setUuid(.getUuid());
//        escola.setId(usuarioLogado.getEscola().getId());

        Aluno student = new Aluno();
        student.setEscola(escola);
        student.setNome(request.nome());
        student.setEmail(email);
        student.setPerfil(Perfil.ALUNO);
        student.setMetodoAutenticacao(MetodoAutenticacao.SENHA);
        student.setCpf(cpf);
        student.setMatricula(request.matricula());
        student.setStatus(Status.ATIVO);
        student.setTelefone(request.telefone());
        student.setPrimeiroAcesso(true);
        student.setSenha(passwordEncoder.encode(senha));
        // TODO: integrar com o s3
        //student.setFoto(senha);

        alunoRepository.save(student);

        Carteira carteira = new Carteira();
        carteira.setAluno(student);

        String senhaCartao = SenhaUtil.gerarSenhaTemporariaPin();

        Cartao novoCartao = new Cartao();
        novoCartao.setCarteira(carteira);
        novoCartao.setNumero(request.numeroCartao());
        novoCartao.setSenha(passwordEncoder.encode(senhaCartao));
        novoCartao.setStatus(Status.ATIVO);

        carteira.getCartoes().add(novoCartao);

        carteiraRepository.save(carteira);

        Aluno newStudent = this.alunoRepository.findByEmail(email).orElseThrow(()
                -> EurekaException.ofNotFound("Aluno não encontrado."));

        enviaEmailNovoUsuario(student.getNome(), student.getEmail(), senha);

        List<String> emails = new ArrayList<>();
        emails.add(student.getEmail());
        emails.addAll(student.getResponsaveis().stream().map(ResponsavelAluno::getResponsavel).map(Usuario::getEmail).toList());
        enviaEmailNovoCartao(student.getNome(), emails, novoCartao.getNumero(), senhaCartao);

        return newStudent.getUuid();
    }

    public void update(UUID uuid, AlunoRequest request) {

        Aluno student = this.findByUuid(uuid);

        String email = request.email().trim();
        if (this.alunoRepository.existsByEmailAndUuidNot(email, uuid))
            throw EurekaException.ofValidation(email + " já está cadastrado");

        String cpf = request.cpf().trim().replaceAll("\\D", "");
        if (this.alunoRepository.existsByCpfAndUuidNot(cpf, uuid))
            throw EurekaException.ofValidation(cpf + " já está cadastrado");

        student.setNome(request.nome());
        student.setEmail(email);
        student.setCpf(cpf);
        student.setTelefone(request.telefone());
        student.setMatricula(request.matricula());

        // TODO - integrar com o s3
        //student.setFoto(request.foto());

        this.alunoRepository.save(student);
    }

    public Page<AlunoSummary> findAll(AlunoSpecification specification, Pageable pageable) {
        return this.alunoRepository.findAllProjected(specification, pageable, AlunoSummary.class);
    }

    public <T> T findByUuid(UUID uuid, Class<T> clazz) {
        Object usuario = this.alunoRepository.findByUuid(uuid).orElseThrow(
                () -> new EntityNotFoundException("Aluno não encontrado"));

        return clazz.cast(usuario);
    }

    public void changeStudentStatus(UUID uuid, Status status) {

        Aluno student = this.findByUuid(uuid);

        if (student.getStatus() != status) {
            student.setStatus(status);
            this.alunoRepository.save(student);
        }
    }

    private void enviaEmailNovoUsuario(String nome, String email, String senha) {
        String body = String.format("Olá, %s! Sua senha temporária é:%n%s", nome, senha);
        emailService.sendEmail(
                new EmailDto(
                        body,
                        List.of(email),
                        List.of(),
                        List.of(),
                        "Suas credenciais chegaram!",
                        List.of(),
                        null
                )
        );
    }

    private void enviaEmailNovoCartao(String nome, List<String> emails, String numero, String senha) {
        String body = String.format("Olá, %s! A senha do seu cartão (%s) é:%n%s", nome, numero, senha);
        emailService.sendEmail(
                new EmailDto(
                        body,
                        emails,
                        List.of(),
                        List.of(),
                        "Seu cartão foi cadastrado!",
                        List.of(),
                        null
                )
        );
    }

}
