package com.example.demo.service;

import com.example.demo.domain.enums.TipoTransacao;
import com.example.demo.domain.model.Pedido;
import com.example.demo.domain.model.Usuario;
import com.example.demo.domain.model.carteira.Carteira;
import com.example.demo.domain.model.carteira.Transacao;
import com.example.demo.dto.projection.carteira.CarteiraView;
import com.example.demo.exception.eureka.EurekaException;
import com.example.demo.repository.CarteiraRepository;
import com.example.demo.repository.TransacaoRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class CarteiraService {

    private final CarteiraRepository repository;
    private final TransacaoRepository transacaoRepository;

    public CarteiraService(CarteiraRepository repository, TransacaoRepository transacaoRepository) {
        this.repository = repository;
        this.transacaoRepository = transacaoRepository;
    }

    /**
     * Busca a Carteira via UUID, retornando a projeção CarteiraView.
     */
    public CarteiraView buscarPorAlunoUuid(UUID alunoUuid) {
        return repository.findByAlunoUuid(alunoUuid, CarteiraView.class)
                .orElseThrow(() -> EurekaException.ofNotFound("Carteira não encontrada."));
    }

    /**
     * Método para debitar uma compra da carteira.
     */
    public void debitarCompra(UUID alunoUuid, BigDecimal valor, Pedido pedido, Usuario usuarioResponsavel) {
        Carteira carteira = repository.findByAlunoUuid(alunoUuid)
                .orElseThrow(() -> EurekaException.ofNotFound("Carteira não encontrada."));

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
        Carteira carteira = repository.findByAlunoUuid(alunoUuid)
                .orElseThrow(() -> EurekaException.ofNotFound("Carteira não encontrada."));

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
        Carteira carteira = repository.findByAlunoUuid(alunoUuid)
                .orElseThrow(() -> EurekaException.ofNotFound("Carteira não encontrada."));

        Transacao transacao = new Transacao();
        transacao.setTipoTransacao(TipoTransacao.CREDITO);
        transacao.setCarteira(carteira);
        transacao.setValor(valor);

        // TODO Calcular taxa futura sobpagamento

        transacaoRepository.save(transacao);

        return "OK";
    }
}
