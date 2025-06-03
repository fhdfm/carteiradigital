package com.example.demo.migracao_escola.service;

import com.example.demo.domain.enums.MetodoAutenticacao;
import com.example.demo.domain.enums.Perfil;
import com.example.demo.domain.enums.Status;
import com.example.demo.domain.model.Escola;
import com.example.demo.dto.email.EmailDto;
import com.example.demo.exception.eureka.EurekaException;
import com.example.demo.migracao_escola.domain.Professor;
import com.example.demo.migracao_escola.dto.ProfessorRequest;
import com.example.demo.migracao_escola.dto.projection.professor.ProfessorSummary;
import com.example.demo.migracao_escola.repository.ProfessorRepository;
import com.example.demo.migracao_escola.repository.specification.ProfessorSpecification;
import com.example.demo.security.SecurityUtils;
import com.example.demo.security.UsuarioLogado;
import com.example.demo.service.EmailService;
import com.example.demo.util.Util;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProfessorService {

    private final ProfessorRepository professorRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public ProfessorService(ProfessorRepository professorRepository, PasswordEncoder passwordEncoder,  EmailService emailService) {
        this.professorRepository = professorRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    public Professor findByUuid(UUID professorId) {
        return this.professorRepository.findByUuid(professorId).orElseThrow(
                () -> EurekaException.ofNotFound("Professor não encontrado."));
    }

    @Transactional
    public UUID create(ProfessorRequest request) {
        UsuarioLogado usuarioLogado = SecurityUtils.getUsuarioLogado();

        String email = null;
        if (request.email() != null) {
            email = request.email().trim();
            if (this.professorRepository.existsByEmail(email))
                throw EurekaException.ofValidation(email + " já está cadastrado");
        }

        String cpf = request.cpf().trim().replaceAll("\\D", "");
        if (this.professorRepository.existsByCpf(cpf))
            throw EurekaException.ofValidation(cpf + " já está cadastrado");

        String senha = Util.gerarSenhaTemporaria();

        Escola escola = usuarioLogado.getEscola();

        Professor professor = new Professor();
        professor.setEscola(escola);
        professor.setNome(request.nome());
        professor.setEmail(email);
        professor.setPerfil(Perfil.PROFESSOR);
        professor.setMetodoAutenticacao(MetodoAutenticacao.SENHA);
        professor.setCpf(cpf);
        professor.setStatus(Status.ATIVO);
        professor.setTelefone(request.telefone());
        professor.setPrimeiroAcesso(true);
        professor.setSenha(passwordEncoder.encode(senha));
        // TODO: integrar com o S3 se necessário
        // professor.setFoto(...);

        professorRepository.save(professor);

//        Carteira carteira = new Carteira();
//        carteira.setProfessor(professor);
//
//        String senhaCartao = SenhaUtil.gerarSenhaTemporariaPin();
//        Cartao novoCartao = new Cartao();
//        if (request.numeroCartao() != null) {
//            novoCartao.setCarteira(carteira);
//            novoCartao.setNumero(request.numeroCartao());
//            novoCartao.setSenha(passwordEncoder.encode(senhaCartao));
//            novoCartao.setStatus(Status.ATIVO);
//
//            carteira.getCartoes().add(novoCartao);
//        }
//
//        carteiraRepository.save(carteira);

        Professor newProfessor = this.professorRepository.findByEmail(email)
                .orElseThrow(() -> EurekaException.ofNotFound("Professor não encontrado."));

        enviaEmailNovoUsuario(professor.getNome(), professor.getEmail(), senha);

//    if (request.numeroCartao() != null) {
//            List<String> emails = List.of(professor.getEmail());
//            enviaEmailNovoCartao(professor.getNome(), emails, novoCartao.getNumero(), senhaCartao);
//        }

        return newProfessor.getUuid();
    }

    public void update(UUID uuid, ProfessorRequest request) {

        Professor student = this.findByUuid(uuid);

        String email = request.email().trim();
        if (this.professorRepository.existsByEmailAndUuidNot(email, uuid))
            throw EurekaException.ofValidation(email + " já está cadastrado");

        String cpf = request.cpf().trim().replaceAll("\\D", "");
        if (this.professorRepository.existsByCpfAndUuidNot(cpf, uuid))
            throw EurekaException.ofValidation(cpf + " já está cadastrado");

        student.setNome(request.nome());
        student.setEmail(email);
        student.setCpf(cpf);
        student.setTelefone(request.telefone());

        // TODO - integrar com o s3
        //student.setFoto(request.foto());

        this.professorRepository.save(student);
    }

    public Page<ProfessorSummary> findAll(ProfessorSpecification specification, Pageable pageable) {
        return this.professorRepository.findAllProjected(specification, pageable, ProfessorSummary.class);
    }



    public <T> T findByUuid(UUID uuid, Class<T> clazz) {
        Object usuario = this.professorRepository.findByUuid(uuid).orElseThrow(
                () -> new EntityNotFoundException("Professor não encontrado"));

        return clazz.cast(usuario);
    }

    public void changeStudentStatus(UUID uuid, Status status) {

        Professor student = this.findByUuid(uuid);

        if (student.getStatus() != status) {
            student.setStatus(status);
            this.professorRepository.save(student);
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

}
