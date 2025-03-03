package com.example.demo.service;

import java.security.SecureRandom;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.domain.enums.Perfil;
import com.example.demo.domain.enums.Status;
import com.example.demo.domain.model.Escola;
import com.example.demo.domain.model.Usuario;
import com.example.demo.dto.TrocarSenhaRequest;
import com.example.demo.dto.UsuarioRequest;
import com.example.demo.dto.projection.usuario.UsuarioSummary;
import com.example.demo.exception.escola.EscolaException;
import com.example.demo.repository.UsuarioRepository;
import com.example.demo.repository.specification.UsuarioSpecification;
import com.example.demo.security.SecurityUtils;
import com.example.demo.security.UsuarioLogado;

import jakarta.persistence.EntityNotFoundException;

@Service
public class UsuarioService {
    
    private final PasswordEncoder passwordEncoder;
    private final UsuarioRepository repository;
    private final EscolaService escolaService;
    private final AlunoService alunoService;

    public UsuarioService(UsuarioRepository repository, 
        PasswordEncoder passwordEncoder, EscolaService escolaService, 
        AlunoService alunoService) {
        
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.escolaService = escolaService;
        this.alunoService = alunoService;
    }

    public UUID create(UsuarioRequest request) {

        UsuarioLogado currentUser = SecurityUtils.getUsuarioLogado();
        Perfil perfil = request.perfil();
        validateProfileAssignmentPermission(currentUser, perfil);

        String email = request.email().trim();
        if (this.repository.existsByEmail(email))
            throw EscolaException.ofValidation(email + " já está cadastrado");

        String cpf = request.cpf().trim().replaceAll("\\D", "");
        if (this.repository.existsByCpf(cpf))
            throw EscolaException.ofValidation(cpf + " já está cadastrado");

        String senha = this.gerarSenhaTemporaria();

        Escola escola = this.getEscola(request.escolaId());

        Usuario user = new Usuario();
        user.setEscola(escola);
        user.setNome(request.nome());
        user.setEmail(email);
        user.setPerfil(perfil);
        user.setMetodoAutenticacao(request.metodoAutenticacao());
        user.setCpf(cpf);
        user.setStatus(Status.ATIVO);
        user.setTelefone(request.telefone());
        user.setPrimeiroAcesso(true);
        user.setSenha(passwordEncoder.encode(senha));

        // TODO - Envia e-mail para o usuário.

        repository.save(user);

        Usuario newUser = this.repository.findByEmail(email).orElseThrow(() 
                        -> EscolaException.ofNotFound("Usuário não encontrado."));
        return newUser.getUuid();
    }

    public void update(UUID uuid, UsuarioRequest request) {

        Usuario user = this.findByUuid(uuid);

        UsuarioLogado currentUser = SecurityUtils.getUsuarioLogado();
        Perfil perfil = request.perfil();
        validateProfileAssignmentPermission(currentUser, perfil);

        String email = request.email().trim();
        if (this.repository.existsByEmailAndUuidNot(email, uuid))
            throw EscolaException.ofValidation(email + " já está cadastrado");

        String cpf = request.cpf().trim().replaceAll("\\D", "");
        if (this.repository.existsByCpfAndUuidNot(cpf, uuid))
            throw EscolaException.ofValidation(cpf + " já está cadastrado");

        Escola escola = this.getEscola(request.escolaId());
        
        user.setEscola(escola);
        user.setNome(request.nome());
        user.setEmail(email);
        user.setPerfil(perfil);
        user.setMetodoAutenticacao(request.metodoAutenticacao());
        user.setCpf(cpf);
        user.setTelefone(request.telefone());

        repository.save(user);
    }    

    public Usuario findByUuid(UUID uuid) {
        return this.repository.findByUuid(uuid).orElseThrow(
                () -> EscolaException.ofNotFound("Usuário não encontrado."));
    }

    public void atualizarStatus(UUID uuid, Status status) {
        
        Usuario user = this.findByUuid(uuid);

        if (user.getStatus() != status) {
            
            if (status == Status.INATIVO) {
                // Verificar se possui dependentes...
                if (alunoService.existsByResponsavel(user))
                    throw EscolaException.ofConflict("Não é possível inativar o Reponsável, pois possui Alunos vinculados.");

            }

            user.setStatus(status);
            this.repository.save(user);
        }
    }

    public Usuario findByEmailComEscola(String email) {
        return this.repository.buscarUsuarioAtivoComEscolaPorEmail(email).orElseThrow(
                () -> EscolaException.ofNotFound("Usuário não encontrado."));
    }

    public Page<UsuarioSummary> findAll(UsuarioSpecification specification, Pageable pageable) {
        return this.repository.findAllProjected(specification, pageable, UsuarioSummary.class);
    }

    /**
     * 
     * @param <T>
     * @param uuid
     * @param clazz
     * @return
     */
    public <T> T findByUuid(UUID uuid, Class<T> clazz) {

        Object usuario = this.repository.findByUuid(uuid).orElseThrow(
                () -> new EntityNotFoundException("Usuário não encontrado"));

        return clazz.cast(usuario);
    }

    /**
     * 
     * @param request
     */
    public void changePassword(TrocarSenhaRequest request) {
        
        UsuarioLogado currentUser = SecurityUtils.getUsuarioLogado();
        
        if (currentUser.possuiPerfil(Perfil.ALUNO)) {
            Boolean alunoPodeTrocarSenha =
                this.repository.isPrimeiroAcessoResponsavel(currentUser.getUuid());
            if (alunoPodeTrocarSenha != null && !alunoPodeTrocarSenha)
                EscolaException.ofValidation("O responsável ainda não habilitou sua conta.");
        }

        if (!passwordEncoder.matches(request.senhaAntiga(), currentUser.getPassword()))
            EscolaException.ofValidation("Senha incorreta.");

        if (!request.novaSenha().equals(request.confirmarNovaSenha()))
            EscolaException.ofValidation("Senha e confirmação não conferem.");

        Usuario usuario = this.findByUuid(currentUser.getUuid());
        usuario.setPrimeiroAcesso(false);
        usuario.setSenha(passwordEncoder.encode(request.novaSenha()));

        this.repository.save(usuario);     
    }

    private void validateProfileAssignmentPermission(UsuarioLogado currentUser, Perfil requestedPerfil) {
        if (currentUser.possuiPerfil(Perfil.ADMIN) && requestedPerfil == Perfil.MASTER) {
            throw EscolaException.ofValidation("Operação não permitida");
        }

        if (currentUser.possuiPerfil(Perfil.FUNCIONARIO) && requestedPerfil == Perfil.ADMIN) {
            throw EscolaException.ofValidation("Operação não permitida");
        }
    }    

    private Escola getEscola(UUID uuid) {
        return this.escolaService.findByUuid(uuid);
    }

    /**
    * Gera uma senha temporária aleatória com tamanho fixo de 8 caracteres.
    *
    * <p>A senha gerada atende aos seguintes requisitos:
    * <ul>
    *   <li>Contém pelo menos um caractere maiúsculo</li>
    *   <li>Contém pelo menos um caractere minúsculo</li>
    *   <li>Contém pelo menos um dígito numérico</li>
    *   <li>Contém pelo menos um caractere especial</li>
    * </ul>
    * Os demais caracteres são escolhidos aleatoriamente dentre todos os caracteres permitidos.
    * A ordem dos caracteres é embaralhada para evitar qualquer padrão previsível.
    * </p>
    *
    * @return uma String contendo a senha temporária gerada.
    */
    private String gerarSenhaTemporaria() {

        int tamanhoSenha = 8;

        String maiusculas = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String minusculas = "abcdefghijklmnopqrstuvwxyz";
        String numeros = "0123456789";
        String especiais = "!@#$%^&*()-_=+[]{}|;:,.<>?";
        String todos = maiusculas + minusculas + numeros + especiais;

        SecureRandom random = new SecureRandom();
        
        List<Character> senha = new java.util.ArrayList<>();
        
        // Adiciona obrigatoriamente um caractere de cada tipo
        senha.add(maiusculas.charAt(random.nextInt(maiusculas.length())));
        senha.add(minusculas.charAt(random.nextInt(minusculas.length())));
        senha.add(numeros.charAt(random.nextInt(numeros.length())));
        senha.add(especiais.charAt(random.nextInt(especiais.length())));

        for (int i = 4; i < tamanhoSenha; i++) {
            senha.add(todos.charAt(random.nextInt(todos.length())));
        }

        Collections.shuffle(senha, random);
        
        // Converte a lista de caracteres para String
        StringBuilder resultado = new StringBuilder(tamanhoSenha);
        for (char c : senha) {
            resultado.append(c);
        }

        return resultado.toString();
    }

}
