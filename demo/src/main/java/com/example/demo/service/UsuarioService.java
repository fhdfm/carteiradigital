package com.example.demo.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.domain.enums.MetodoAutenticacao;
import com.example.demo.domain.enums.Perfil;
import com.example.demo.domain.enums.Status;
import com.example.demo.domain.model.Escola;
import com.example.demo.domain.model.Usuario;
import com.example.demo.dto.TrocarSenhaRequest;
import com.example.demo.dto.UsuarioRequest;
import com.example.demo.dto.projection.usuario.UsuarioSummary;
import com.example.demo.exception.eureka.EurekaException;
import com.example.demo.repository.UsuarioRepository;
import com.example.demo.repository.specification.UsuarioSpecification;
import com.example.demo.security.SecurityUtils;
import com.example.demo.security.UsuarioLogado;
import com.example.demo.util.Util;

@Service
public class UsuarioService {
    
    private final PasswordEncoder passwordEncoder;
    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository, 
        PasswordEncoder passwordEncoder) {
        
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UUID createUser(UsuarioRequest request) {

        UsuarioLogado currentUser = SecurityUtils.getUsuarioLogado();
        Perfil perfil = request.perfil();
        validateProfileAssignmentPermission(currentUser, perfil);

        String email = request.email().trim();
        if (this.usuarioRepository.existsByEmail(email))
            throw EurekaException.ofValidation(email + " já está cadastrado");

        String cpf = request.cpf().trim().replaceAll("\\D", "");
        if (this.usuarioRepository.existsByCpf(cpf))
            throw EurekaException.ofValidation(cpf + " já está cadastrado");

        String senha = Util.gerarSenhaTemporaria();

        Escola escola = new Escola();
        escola.setUuid(request.escolaId());

        Usuario user = new Usuario();
        user.setEscola(escola);
        user.setNome(request.nome());
        user.setEmail(email);
        user.setPerfil(perfil);
        user.setMetodoAutenticacao(MetodoAutenticacao.SENHA);
        user.setCpf(cpf);
        user.setStatus(Status.ATIVO);
        user.setTelefone(request.telefone());
        user.setPrimeiroAcesso(true);
        user.setSenha(passwordEncoder.encode(senha));

        // TODO - Envia e-mail para o usuário.

        usuarioRepository.save(user);

        Usuario newUser = this.usuarioRepository.findByEmail(email).orElseThrow(() 
                        -> EurekaException.ofNotFound("Usuário não encontrado."));
        return newUser.getUuid();
    }

    public void updateUser(UUID uuid, UsuarioRequest request) {

        Usuario user = this.findByUuid(uuid);

        UsuarioLogado currentUser = SecurityUtils.getUsuarioLogado();
        Perfil perfil = request.perfil();
        validateProfileAssignmentPermission(currentUser, perfil);

        String email = request.email().trim();
        if (this.usuarioRepository.existsByEmailAndUuidNot(email, uuid))
            throw EurekaException.ofValidation(email + " já está cadastrado");

        String cpf = request.cpf().trim().replaceAll("\\D", "");
        if (this.usuarioRepository.existsByCpfAndUuidNot(cpf, uuid))
            throw EurekaException.ofValidation(cpf + " já está cadastrado");

        Escola escola = new Escola();
        escola.setUuid(request.escolaId());
        
        user.setEscola(escola);
        user.setNome(request.nome());
        user.setEmail(email);
        user.setPerfil(perfil);
        user.setCpf(cpf);
        user.setTelefone(request.telefone());

        usuarioRepository.save(user);
    }

    public Usuario findByUuid(UUID uuid) {
        return this.usuarioRepository.findByUuid(uuid).orElseThrow(
                () -> EurekaException.ofNotFound("Usuário não encontrado."));
    }

    public void changeUserStatus(UUID uuid, Status status) {
        
        Usuario user = this.findByUuid(uuid);

        if (user.getStatus() != status) {
            
            if (status == Status.INATIVO) {
                // Verificar se possui dependentes...
                // if (alunoRepository.existsByResponsavelId(user.getUuid()))
                //     throw EurekaException.ofConflict("Não é possível inativar o Reponsável, pois possui Alunos vinculados.");

            }

            user.setStatus(status);
            this.usuarioRepository.save(user);
        }
    }

    public Usuario findByEmailComEscola(String email) {
        return this.usuarioRepository.buscarUsuarioAtivoComEscolaPorEmail(email).orElseThrow(
                () -> EurekaException.ofNotFound("Usuário não encontrado."));
    }

    public Page<UsuarioSummary> findAll(UsuarioSpecification specification, Pageable pageable) {
        return this.usuarioRepository.findAllProjected(specification, pageable, UsuarioSummary.class);
    }

    /**
     * 
     * @param <T>
     * @param uuid
     * @param clazz
     * @return
     */
    public <T> T findByUuid(UUID uuid, Class<T> clazz) {

        Object usuario = this.usuarioRepository.findByUuid(uuid).orElseThrow(
                () -> EurekaException.ofNotFound("Usuario não encontrado."));

        return clazz.cast(usuario);
    }
    
    /**
     * 
     * @param request
     */
    public void changePassword(TrocarSenhaRequest request) {
        
        UsuarioLogado currentUser = SecurityUtils.getUsuarioLogado();
        
        if (currentUser.possuiPerfil(Perfil.ALUNO)) {
            // TODO - AQUI VERIFICAR SE O ALUNO TEM PERMISSÃO DE TROCAR A SENHA.
            Boolean alunoPodeTrocarSenha =
                this.usuarioRepository.isPrimeiroAcessoResponsavel(currentUser.getUuid());
            if (alunoPodeTrocarSenha != null && !alunoPodeTrocarSenha)
                EurekaException.ofValidation("O responsável ainda não habilitou sua conta.");
        }

        if (!passwordEncoder.matches(request.senhaAntiga(), currentUser.getPassword()))
            EurekaException.ofValidation("Senha incorreta.");

        if (!request.novaSenha().equals(request.confirmarNovaSenha()))
            EurekaException.ofValidation("Senha e confirmação não conferem.");

        Usuario usuario = this.findByUuid(currentUser.getUuid());
        usuario.setPrimeiroAcesso(false);
        usuario.setSenha(passwordEncoder.encode(request.novaSenha()));

        this.usuarioRepository.save(usuario);     
    }

    private void validateProfileAssignmentPermission(UsuarioLogado currentUser, Perfil requestedPerfil) {
        if (currentUser.possuiPerfil(Perfil.ADMIN) && requestedPerfil == Perfil.MASTER) {
            throw EurekaException.ofValidation("Operação não permitida");
        }

        if (currentUser.possuiPerfil(Perfil.FUNCIONARIO) && requestedPerfil == Perfil.ADMIN) {
            throw EurekaException.ofValidation("Operação não permitida");
        }
    }

}
