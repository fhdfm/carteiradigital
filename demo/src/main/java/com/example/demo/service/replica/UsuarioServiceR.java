package com.example.demo.service.replica;

import com.example.demo.domain.enums.MetodoAutenticacao;
import com.example.demo.domain.enums.Perfil;
import com.example.demo.domain.enums.Status;
import com.example.demo.domain.model.Aluno;
import com.example.demo.domain.model.Escola;
import com.example.demo.domain.model.Usuario;
import com.example.demo.dto.AlunoRequest;
import com.example.demo.dto.TrocarSenhaRequest;
import com.example.demo.dto.UsuarioRequest;
import com.example.demo.dto.UsuarioView;
import com.example.demo.dto.projection.aluno.AlunoSummary;
import com.example.demo.dto.projection.usuario.UsuarioSummary;
import com.example.demo.exception.escola.EscolaException;
import com.example.demo.repository.AlunoRepository;
import com.example.demo.repository.UsuarioRepository;
import com.example.demo.repository.specification.AlunoSpecification;
import com.example.demo.repository.specification.UsuarioSpecification;
import com.example.demo.security.SecurityUtils;
import com.example.demo.security.UsuarioLogado;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class UsuarioServiceR {
    
    private final PasswordEncoder passwordEncoder;
    private final EscolaUsuarioService escolaUsuarioService;
    private final UsuarioRepository usuarioRepository;
    private final AlunoRepository alunoRepository;

    public UsuarioServiceR(UsuarioRepository usuarioRepository,
                           PasswordEncoder passwordEncoder, EscolaUsuarioService escolaUsuarioService,
                           AlunoRepository alunoRepository) {
        
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.escolaUsuarioService = escolaUsuarioService;
        this.alunoRepository = alunoRepository;
    }

    public UUID createUser(UsuarioRequest request) {

        UsuarioLogado currentUser = SecurityUtils.getUsuarioLogado();
        Perfil perfil = request.perfil();
        validateProfileAssignmentPermission(currentUser, perfil);

        String email = request.email().trim();
        if (this.usuarioRepository.existsByEmail(email))
            throw EscolaException.ofValidation(email + " já está cadastrado");

        String cpf = request.cpf().trim().replaceAll("\\D", "");
        if (this.usuarioRepository.existsByCpf(cpf))
            throw EscolaException.ofValidation(cpf + " já está cadastrado");

        String senha = this.gerarSenhaTemporaria();

        Escola escola = this.getEscola(request.escolaId());

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
                        -> EscolaException.ofNotFound("Usuário não encontrado."));
        return newUser.getUuid();
    }

    public void updateUser(UUID uuid, UsuarioRequest request) {

        Usuario user = this.findUserByUuid(uuid);

        UsuarioLogado currentUser = SecurityUtils.getUsuarioLogado();
        Perfil perfil = request.perfil();
        validateProfileAssignmentPermission(currentUser, perfil);

        String email = request.email().trim();
        if (this.usuarioRepository.existsByEmailAndUuidNot(email, uuid))
            throw EscolaException.ofValidation(email + " já está cadastrado");

        String cpf = request.cpf().trim().replaceAll("\\D", "");
        if (this.usuarioRepository.existsByCpfAndUuidNot(cpf, uuid))
            throw EscolaException.ofValidation(cpf + " já está cadastrado");

        Escola escola = this.getEscola(request.escolaId());
        
        user.setEscola(escola);
        user.setNome(request.nome());
        user.setEmail(email);
        user.setPerfil(perfil);
        user.setCpf(cpf);
        user.setTelefone(request.telefone());

        usuarioRepository.save(user);
    }    

    public Usuario findUserByUuid(UUID uuid) {
        return this.usuarioRepository.findByUuid(uuid).orElseThrow(
                () -> EscolaException.ofNotFound("Usuário não encontrado."));
    }

    public void changeUserStatus(UUID uuid, Status status) {
        
        Usuario user = this.findUserByUuid(uuid);

        if (user.getStatus() != status) {
            
            if (status == Status.INATIVO) {
                // Verificar se possui dependentes...
                if (alunoRepository.existsByResponsavelId(user.getUuid()))
                    throw EscolaException.ofConflict("Não é possível inativar o Reponsável, pois possui Alunos vinculados.");

            }

            user.setStatus(status);
            this.usuarioRepository.save(user);
        }
    }

    public Usuario findByEmailComEscola(String email) {
        return this.usuarioRepository.buscarUsuarioAtivoComEscolaPorEmail(email).orElseThrow(
                () -> EscolaException.ofNotFound("Usuário não encontrado."));
    }

    public Page<UsuarioSummary> findAllUsers(UsuarioSpecification specification, Pageable pageable) {
        return this.usuarioRepository.findAllProjected(specification, pageable, UsuarioSummary.class);
    }

    /**
     * 
     * @param <T>
     * @param uuid
     * @param clazz
     * @return
     */
    public <T> T findUserByUuid(UUID uuid, Class<T> clazz) {

        Object usuario = this.usuarioRepository.findByUuid(uuid).orElseThrow(
                () -> EscolaException.ofNotFound("Usuario não encontrado."));

        return clazz.cast(usuario);
    }

    public Aluno findStudentByUuid(UUID alunoId) {
        return this.alunoRepository.findByUuid(alunoId).orElseThrow(
                () -> EscolaException.ofNotFound("Aluno não encontrado."));
    }

    public Aluno findStudentWithResponsavelByUuid(UUID alunoId) {
        return this.alunoRepository.findWithResponsavelByUuid(alunoId).orElseThrow(
                () -> EscolaException.ofNotFound("Aluno não encontrado."));
    }

    public UUID createStudent(AlunoRequest request) {
        
        String email = request.email().trim();
        if (this.alunoRepository.existsByEmail(email))
            throw EscolaException.ofValidation(email + " já está cadastrado");

        String cpf = request.cpf().trim().replaceAll("\\D", "");
        if (this.alunoRepository.existsByCpf(cpf))
            throw EscolaException.ofValidation(cpf + " já está cadastrado");

        String senha = this.gerarSenhaTemporaria();

        Escola escola = this.getEscola(request.escolaId());
        Usuario responsavel = this.findUserByUuid(request.responsavelId());

        Aluno student = new Aluno();
        student.setEscola(escola);
        student.setResponsavel(responsavel);
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
                        -> EscolaException.ofNotFound("Aluno não encontrado."));
        return newStudent.getUuid();
    }

    public void updateStudent(UUID uuid, AlunoRequest request) {
        
        Aluno student = this.findStudentByUuid(uuid);

        String email = request.email().trim();
        if (this.alunoRepository.existsByEmailAndUuidNot(email, uuid))
            throw EscolaException.ofValidation(email + " já está cadastrado");

        String cpf = request.cpf().trim().replaceAll("\\D", "");
        if (this.alunoRepository.existsByCpfAndUuidNot(cpf, uuid))
            throw EscolaException.ofValidation(cpf + " já está cadastrado");

        Usuario responsavel = this.findUserByUuid(request.responsavelId());
        
        student.setResponsavel(responsavel);
        student.setNome(request.nome());
        student.setEmail(email);
        student.setCpf(cpf);
        student.setTelefone(request.telefone());
        student.setMatricula(request.matricula());

        // TODO - integrar com o s3
        //student.setFoto(request.foto());

        this.alunoRepository.save(student);        
    }

    public Page<AlunoSummary> findAllStudents(AlunoSpecification specification, Pageable pageable) {
        return this.alunoRepository.findAllProjected(specification, pageable, AlunoSummary.class);
    }

    public <T> T findStudentByUuid(UUID uuid, Class<T> clazz) {
        Object usuario = this.alunoRepository.findByUuid(uuid).orElseThrow(
            () -> new EntityNotFoundException("Aluno não encontrado"));

        return clazz.cast(usuario);
    }

    public void changeStudentStatus(UUID uuid, Status status) {
        
        Aluno student = this.findStudentByUuid(uuid);

        if (student.getStatus() != status) {
            student.setStatus(status);
            this.alunoRepository.save(student);
        }
    }

    /**
     * 
     * @param request
     */
    public void changePassword(TrocarSenhaRequest request) {
        
        UsuarioLogado currentUser = SecurityUtils.getUsuarioLogado();
        
        if (currentUser.possuiPerfil(Perfil.ALUNO)) {
            Boolean alunoPodeTrocarSenha =
                this.usuarioRepository.isPrimeiroAcessoResponsavel(currentUser.getUuid());
            if (alunoPodeTrocarSenha != null && !alunoPodeTrocarSenha)
                EscolaException.ofValidation("O responsável ainda não habilitou sua conta.");
        }

        if (!passwordEncoder.matches(request.senhaAntiga(), currentUser.getPassword()))
            EscolaException.ofValidation("Senha incorreta.");

        if (!request.novaSenha().equals(request.confirmarNovaSenha()))
            EscolaException.ofValidation("Senha e confirmação não conferem.");

        Usuario usuario = this.findUserByUuid(currentUser.getUuid());
        usuario.setPrimeiroAcesso(false);
        usuario.setSenha(passwordEncoder.encode(request.novaSenha()));

        this.usuarioRepository.save(usuario);     
    }

    public Page<UsuarioView> findAllByEscolaUuid(UUID escolaId, Pageable pageable) {
        return usuarioRepository.findAllByEscolaUuid(escolaId, pageable);
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
        return this.escolaUsuarioService.findByUuid(uuid);
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
