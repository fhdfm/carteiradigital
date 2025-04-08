package com.example.demo.service;

import java.util.Objects;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.demo.domain.enums.Perfil;
import com.example.demo.domain.model.Aluno;
import com.example.demo.domain.model.ResponsavelAluno;
import com.example.demo.domain.model.Usuario;
import com.example.demo.dto.ResponsavelAlunoRequest;
import com.example.demo.dto.UsuarioRequest;
import com.example.demo.exception.eureka.EurekaException;
import com.example.demo.repository.ResponsavelAlunoRepository;

import jakarta.transaction.Transactional;

@Service
public class ResponsavelAlunoService {
    
    private final ResponsavelAlunoRepository responsavelAlunoRepository;
    private final UsuarioService usuarioService;
    private final AlunoService alunoService;

    public ResponsavelAlunoService(ResponsavelAlunoRepository responsavelAlunoRepository, 
        UsuarioService usuarioService, AlunoService alunoService) {
        this.usuarioService = usuarioService;
        this.responsavelAlunoRepository = responsavelAlunoRepository;
        this.alunoService = alunoService;
    }

    @Transactional
    public Long create(ResponsavelAlunoRequest request, UUID alunoId) {
        
        UsuarioRequest responsavelRequest = request.responsavel();

        if (Objects.isNull(request.grauParentesco())) 
            throw EurekaException.ofValidation("Grau de parentesco é obrigatório.");

        if (Objects.isNull(responsavelRequest.perfil()))
            throw EurekaException.ofValidation("Perfil é obrigatório.");

        if (!Objects.equals(responsavelRequest.perfil(), Perfil.RESPONSAVEL))
            throw EurekaException.ofValidation("O usuário deve ser do tipo Responsável.");

        UUID responsavelId = this.usuarioService.createUser(responsavelRequest);
        Usuario responsavel = this.usuarioService.findByUuid(responsavelId);

        Aluno aluno = this.alunoService.findByUuid(alunoId);

        if (this.responsavelAlunoRepository.existsByResponsavelAndAluno(responsavel.getId(), aluno.getId()))
            throw EurekaException.ofValidation("Responsável já cadastrado para o aluno.");

        ResponsavelAluno responsavelAluno = new ResponsavelAluno();
        responsavelAluno.setResponsavel(responsavel);
        responsavelAluno.setAluno(aluno);
        responsavelAluno.setGrauParentesco(request.grauParentesco());

        this.responsavelAlunoRepository.save(responsavelAluno);

        return responsavelAluno.getId(); // Retornar o UUID do Responsável Aluno criado
    }

}
