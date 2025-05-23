package com.example.demo.service;

import com.example.demo.domain.enums.StatusPedido;
import com.example.demo.domain.enums.TipoTransacao;
import com.example.demo.domain.model.*;
import com.example.demo.domain.model.carteira.Carteira;
import com.example.demo.domain.model.carteira.Transacao;
import com.example.demo.dto.pedido.ItemPedidoRequest;
import com.example.demo.dto.pedido.PedidoRequest;
import com.example.demo.dto.projection.pedido.PedidoView;
import com.example.demo.exception.eureka.EurekaException;
import com.example.demo.repository.*;
import com.example.demo.repository.specification.PedidoSpecification;
import com.example.demo.security.SecurityUtils;
import com.example.demo.security.UsuarioLogado;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ProdutoRepository produtoRepository;
    private final AlunoRepository alunoRepository;
    private final CarteiraRepository carteiraRepository;
    private final TransacaoRepository transacaoRepository;
    private final UsuarioService usuarioService;

    public PedidoService(
            PedidoRepository pedidoRepository, ProdutoRepository produtoRepository,
            AlunoRepository alunoRepository, CarteiraRepository carteiraRepository,
            TransacaoRepository transacaoRepository, UsuarioService usuarioService
    ) {
        this.pedidoRepository = pedidoRepository;
        this.produtoRepository = produtoRepository;
        this.alunoRepository = alunoRepository;
        this.carteiraRepository = carteiraRepository;
        this.transacaoRepository = transacaoRepository;
        this.usuarioService = usuarioService;
    }

    /* ───────────────────────── CRIAR ───────────────────────── */

    @Transactional
    public void salvar(PedidoRequest request) {

        Pedido pedido = montarPedido(request, new Pedido());

        pedidoRepository.save(pedido);
    }

    /* ───────────────────────── ATUALIZAR ───────────────────────── */

    @Transactional
    public void salvar(UUID uuid, PedidoRequest request) {

        Pedido pedido = pedidoRepository.findByUuid(uuid)
                .orElseThrow(() -> EurekaException.ofNotFound("Pedido não encontrado."));

        pedido.getItens().clear();
        pedidoRepository.flush();

        montarPedido(request, pedido);

        pedidoRepository.save(pedido);
    }

    /* ───────────────────────── BUSCAR ───────────────────────── */

    public PedidoView buscarPorUuid(UUID uuid) {
        return pedidoRepository.findByUuid(uuid, PedidoView.class)
                .orElseThrow(() -> EurekaException.ofNotFound("Pedido não encontrado."));
    }

    /* ───────────────────────── LISTAR ───────────────────────── */

    public Page<PedidoView> listar(PedidoSpecification specification, Pageable pageable) {
        Page<PedidoView> page =
                pedidoRepository.findAllProjected(specification, pageable, PedidoView.class);

        if (page.isEmpty()) {
            EurekaException.ofNoContent("Consulta com filtro informado não possui dados para retorno");
        }
        return page;
    }

    /* ───────────────────────── STATUS ───────────────────────── */

    @Transactional
    public void cancelar(UUID uuid) {
        alterarStatus(uuid, StatusPedido.CANCELADO);
    }

    @Transactional
    public void confirmar(UUID uuid) {

        Pedido pedido = pedidoRepository.findByUuid(uuid)
                .orElseThrow(() -> EurekaException.ofNotFound("Pedido não encontrado."));

        if (pedido.getStatus() == StatusPedido.CONCLUIDO) {
            throw EurekaException.ofValidation("Pedido já está concluído.");
        }

        pedido.setStatus(StatusPedido.CONCLUIDO);

        Transacao transacao = new Transacao();
        transacao.setTipoTransacao(TipoTransacao.DEBITO);
        transacao.setValor(pedido.getTotal());
        transacao.setPedido(pedido);

        Carteira carteira = carteiraRepository.findByAluno_Uuid(pedido.getComprador().getUuid())
                .orElseThrow(() -> EurekaException.ofNotFound("Carteira não encontrada."));
        transacao.setCarteira(carteira);

        transacao.setUsuario(pedido.getVendedor());

        transacaoRepository.save(transacao);

        pedidoRepository.save(pedido);
    }


    /**
     * Preenche o objeto Pedido (novo ou existente) a partir do DTO.
     * Retorna a própria instância para encadear persistência.
     */
    private Pedido montarPedido(PedidoRequest dto, Pedido pedido) {

        UsuarioLogado vendedorLogado = SecurityUtils.getUsuarioLogado();
        Escola escola = vendedorLogado.getEscola();

        Aluno comprador = alunoRepository.findByUuid(dto.compradorId())
                .orElseThrow(() -> EurekaException.ofNotFound("Comprador não encontrado."));

        pedido.setVendedor(usuarioService.findByUuid(vendedorLogado.getUuid()));
        pedido.setEscola(escola);
        pedido.setComprador(comprador);
        pedido.setStatus(StatusPedido.ABERTO);

        for (ItemPedidoRequest itemDto : dto.itens()) {

            Produto produto = produtoRepository.findByUuid(itemDto.produtoId())
                    .orElseThrow(() -> EurekaException.ofNotFound("Produto não encontrado."));

            ItemPedido item = new ItemPedido();
            item.setProduto(produto);
            item.setDescricao(produto.getNome());
            item.setQuantidade(itemDto.quantidade());
            item.setValorUnitario(itemDto.valorUnitario());



            pedido.addItem(item);
        }

        return pedido;
    }

    private void alterarStatus(UUID uuid, StatusPedido novoStatus) {
        Pedido pedido = pedidoRepository.findByUuid(uuid)
                .orElseThrow(() -> EurekaException.ofNotFound("Pedido não encontrado."));

        pedido.setStatus(novoStatus);
        pedidoRepository.save(pedido);
    }
}
