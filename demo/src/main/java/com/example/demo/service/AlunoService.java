package com.example.demo.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.domain.enums.MetodoAutenticacao;
import com.example.demo.domain.enums.Perfil;
import com.example.demo.domain.enums.Status;
import com.example.demo.domain.model.Aluno;
import com.example.demo.domain.model.Escola;
import com.example.demo.dto.AlunoRequest;
import com.example.demo.dto.projection.aluno.AlunoSummary;
import com.example.demo.exception.eureka.EurekaException;
import com.example.demo.repository.AlunoRepository;
import com.example.demo.repository.specification.AlunoSpecification;
import com.example.demo.util.Util;

import jakarta.persistence.EntityNotFoundException;

@Service
public class AlunoService {
    
    private final AlunoRepository alunoRepository;
    private final PasswordEncoder passwordEncoder;

    public AlunoService(AlunoRepository alunoRepository, PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
        this.alunoRepository = alunoRepository;
    }

    public Aluno findByUuid(UUID alunoId) {
        return this.alunoRepository.findByUuid(alunoId).orElseThrow(
                () -> EurekaException.ofNotFound("Aluno não encontrado."));
    }

    public Aluno findStudentWithResponsavelByUuid(UUID alunoId) {
        return this.alunoRepository.findWithResponsavelByUuid(alunoId).orElseThrow(
                () -> EurekaException.ofNotFound("Aluno não encontrado."));
    }

    public UUID create(AlunoRequest request) {
        
        String email = request.email().trim();
        if (this.alunoRepository.existsByEmail(email))
            throw EurekaException.ofValidation(email + " já está cadastrado");

        String cpf = request.cpf().trim().replaceAll("\\D", "");
        if (this.alunoRepository.existsByCpf(cpf))
            throw EurekaException.ofValidation(cpf + " já está cadastrado");

        String senha = Util.gerarSenhaTemporaria();

        Escola escola = new Escola();
        escola.setUuid(request.escolaId());

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

        // TODO: - Envia e-mail para o usuário.

        alunoRepository.save(student);

        Aluno newStudent = this.alunoRepository.findByEmail(email).orElseThrow(() 
                        -> EurekaException.ofNotFound("Aluno não encontrado."));
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

}
