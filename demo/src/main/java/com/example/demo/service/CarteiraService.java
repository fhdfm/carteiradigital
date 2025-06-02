package com.example.demo.service;

import com.example.demo.domain.enums.Status;
import com.example.demo.domain.enums.TipoTransacao;
import com.example.demo.domain.model.Aluno;
import com.example.demo.domain.model.Cartao;
import com.example.demo.domain.model.Pedido;
import com.example.demo.domain.model.Usuario;
import com.example.demo.domain.model.carteira.Carteira;
import com.example.demo.domain.model.carteira.Transacao;
import com.example.demo.dto.AlteracaoPinRequest;
import com.example.demo.dto.AlunoUsuarioResponse;
import com.example.demo.dto.CartaoCadastroRequest;
import com.example.demo.dto.email.EmailDto;
import com.example.demo.dto.projection.carteira.CarteiraView;
import com.example.demo.exception.eureka.EurekaException;
import com.example.demo.repository.AlunoRepository;
import com.example.demo.repository.CartaoRepository;
import com.example.demo.repository.CarteiraRepository;
import com.example.demo.repository.TransacaoRepository;
import com.example.demo.util.SenhaUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class CarteiraService {

    private final CarteiraRepository repository;
    private final CartaoRepository cartaoRepository;
    private final TransacaoRepository transacaoRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final AlunoRepository alunoRepository;
    private final CarteiraRepository carteiraRepository;

    public CarteiraService(CarteiraRepository repository, CartaoRepository cartaoRepository, TransacaoRepository transacaoRepository, PasswordEncoder passwordEncoder, EmailService emailService, AlunoRepository alunoRepository, CarteiraRepository carteiraRepository) {
        this.repository = repository;
        this.cartaoRepository = cartaoRepository;
        this.transacaoRepository = transacaoRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.alunoRepository = alunoRepository;
        this.carteiraRepository = carteiraRepository;
    }

    /**
     * Busca a Carteira via UUID, retornando a projeção CarteiraView.
     */
    public CarteiraView buscarPorAlunoUuid(UUID alunoUuid) {
        return repository.findByAluno_Uuid(alunoUuid, CarteiraView.class)
                .orElseThrow(() -> EurekaException.ofNotFound("Carteira não encontrada."));
    }

    /**
     * Método para debitar uma compra da carteira.
     */
    public void debitarCompra(UUID alunoUuid, BigDecimal valor, Pedido pedido, Usuario usuarioResponsavel) {
        Carteira carteira = getCarteira(alunoUuid);

        BigDecimal novoSaldo = carteira.getSaldo().subtract(valor);
        if (novoSaldo.compareTo(BigDecimal.ZERO) < 0) {
            throw EurekaException.ofValidation("Saldo insuficiente para realizar a compra.");
        }

        Transacao transacao = new Transacao();
        transacao.setTipoTransacao(TipoTransacao.DEBITO);
        transacao.setCarteira(carteira);
        transacao.setValor(valor);
        transacao.setPedido(pedido);
        transacao.setUsuario(usuarioResponsavel);

        transacaoRepository.save(transacao);
    }

    /**
     * Método para atualizar salto da carteira a partir de uma recarga.
     */
    public void realizarRecarga(UUID alunoUuid, BigDecimal valor, Usuario usuarioResponsavel) {
        Carteira carteira = getCarteira(alunoUuid);

        Transacao transacao = new Transacao();
        transacao.setTipoTransacao(TipoTransacao.CREDITO);
        transacao.setCarteira(carteira);
        transacao.setValor(valor);
        transacao.setUsuario(usuarioResponsavel);

        transacaoRepository.save(transacao);
    }

    /**
     * Método para atualizar salto da carteira a partir de uma recarga.
     */
    public String realizarRecargaManual(UUID alunoUuid, BigDecimal valor) {
        Carteira carteira = getCarteira(alunoUuid);

        Transacao transacao = new Transacao();
        transacao.setTipoTransacao(TipoTransacao.CREDITO);
        transacao.setCarteira(carteira);
        transacao.setValor(valor);

        // TODO Calcular taxa futura sobpagamento

        transacaoRepository.save(transacao);

        return "OK";
    }

    /**
     * Realiza o cadastro de um novo cartão para a carteira informada.
     *
     * <p>Esse método faz as seguintes operações:
     * <ol>
     *     <li>Busca a carteira a partir do UUID.</li>
     *     <li>Desativa todos os cartões associados à carteira.</li>
     *     <li>Gera uma nova senha temporária, criptografa e cadastra um novo cartão com status ATIVO.</li>
     * </ol>
     *
     * @param request objeto contendo o UUID da carteira e o número do novo cartão.
     * @return
     */
    @Transactional
    public String cadastrarCartao(CartaoCadastroRequest request) {
        Carteira carteira = getCarteira(request.uuid());

        carteira.getCartoes().forEach(cartao -> cartao.setStatus(Status.INATIVO));

        String senha = SenhaUtil.gerarSenhaTemporariaPin();

        Cartao novoCartao = new Cartao();
        novoCartao.setCarteira(carteira);
        novoCartao.setNumero(request.numero());
        novoCartao.setSenha(passwordEncoder.encode(senha));
        novoCartao.setStatus(Status.ATIVO);

        carteira.getCartoes().add(novoCartao);

        repository.save(carteira);

        enviaEmailNovoCartao(
                carteira.getAluno().getNome(),
                carteira.getAluno().getEmail(),
                novoCartao.getNumero(),
                senha
        );

        return "OK";
    }

    /**
     * Altera a senha do cartão ativo de um aluno.
     *
     * <p>Este método faz as seguintes ações:
     * <ul>
     *     <li>Busca a carteira pelo UUID do aluno.</li>
     *     <li>Procura o cartão com status ATIVO.</li>
     *     <li>Altera a senha do cartão para a nova senha informada (criptografada).</li>
     * </ul>
     *
     * @param request objeto contendo o UUID do aluno e a nova senha.
     * @return
     * @throws IllegalStateException se não houver cartão ativo associado à carteira.
     */
    @Transactional
    public String alterarSenhaCartao(AlteracaoPinRequest request) {
        Cartao cartaoAtivo = cartaoRepository.findByStatusAndCarteira_Aluno_Uuid(Status.ATIVO, request.uuid())
                .orElseThrow(() -> new IllegalStateException("Nenhum cartão ativo encontrado para o aluno"));

        cartaoAtivo.setSenha(passwordEncoder.encode(request.senha()));

        cartaoRepository.save(cartaoAtivo);

        return "Ok";
    }


    private Carteira getCarteira(UUID alunoUuid) {
        return repository.findByAluno_Uuid(alunoUuid)
                .orElseThrow(() -> EurekaException.ofNotFound("Carteira não encontrada."));
    }

    private void enviaEmailNovoCartao(String nome, String email, String numero, String senha) {
        String body = String.format("Olá, %s! A senha do seu cartão (%s) é:%n%s", nome, numero, senha);
        emailService.sendEmail(
                new EmailDto(
                        body,
                        List.of(email),
                        List.of(),
                        List.of(),
                        "Seu cartão foi cadastrado!",
                        List.of(),
                        null
                )
        );
    }
    public boolean verificarSenhaCartao(UUID usuarioId, String senha) {
        // Buscar o aluno com base no usuárioId
        Aluno aluno = alunoRepository.findByUuid(usuarioId)
                .orElseThrow(() -> EurekaException.ofNotFound("Aluno não encontrado"));

        // Buscar carteira vinculada ao aluno
        Carteira carteira = carteiraRepository.findByAluno_Id(aluno.getId())
                .orElseThrow(() -> EurekaException.ofNotFound("Carteira não encontrada"));

        // Buscar o cartão ativo da carteira
        Cartao cartao = carteira.getCartoes().stream()
                .filter(c -> c.getStatus() == Status.ATIVO) // ajuste conforme seu enum
                .findFirst()
                .orElseThrow(() -> EurekaException.ofNotFound("Cartão não encontrado ou inativo"));

        if (!cartao.getSenha().equals(senha)) {
            throw EurekaException.ofValidation("Senha inválida");
        }

        return true;
    }


    public AlunoUsuarioResponse buscarCartaoPorNumero(String numero) {
        return carteiraRepository.buscarAlunoPorNumeroCartao(numero);
    }
}
