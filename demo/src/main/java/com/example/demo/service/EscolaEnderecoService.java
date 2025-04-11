package com.example.demo.service;

import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.example.demo.domain.EscolaEndereco;
import com.example.demo.domain.model.Escola;
import com.example.demo.dto.EscolaEnderecoRequest;
import com.example.demo.exception.eureka.EurekaException;
import com.example.demo.repository.EscolaEnderecoRepository;
import com.example.demo.repository.EscolaRepository;

@Service
public class EscolaEnderecoService {
    
    private final EscolaEnderecoRepository escolaEnderecoRepository;
    private final EscolaRepository escolaRepository;

    public EscolaEnderecoService(EscolaEnderecoRepository escolaEnderecoRepository, EscolaRepository escolaRepository) {
        this.escolaEnderecoRepository = escolaEnderecoRepository;
        this.escolaRepository = escolaRepository;
    }
    
    public EscolaEndereco findByEscola(UUID escolaId) {
        return escolaEnderecoRepository.findByEscola_Uuid(escolaId)
            .orElseThrow(() -> EurekaException.ofNotFound("Endereco não encontrado para a escola com ID: " + escolaId));
    }

    public boolean existsByEscola(UUID escolaId) {
        return escolaEnderecoRepository.existsByEscola_Uuid(escolaId);
    }

    public void save(UUID escolaId, EscolaEnderecoRequest request) {

        this.validateRequest(request);
    
        Escola escola = escolaRepository.findByUuid(escolaId)
            .orElseThrow(() -> EurekaException.ofNotFound("Escola não encontrada com ID: " + escolaId));
    
        EscolaEndereco escolaEndereco = escolaEnderecoRepository.findByEscola_Uuid(escolaId)
            .orElseGet(() -> {
                EscolaEndereco novoEndereco = new EscolaEndereco();
                novoEndereco.setEscola(escola);
                return novoEndereco;
            });
    
        escolaEndereco.setEndereco(request.endereco());
        escolaEndereco.setNumero(request.numero());
        escolaEndereco.setBairro(request.bairro());
        escolaEndereco.setCidade(request.cidade());
        escolaEndereco.setEstado(request.estado());
        escolaEndereco.setCep(request.cep());
        escolaEndereco.setComplemento(request.complemento());
    
        escolaEnderecoRepository.save(escolaEndereco);
    }

    private void validateRequest(EscolaEnderecoRequest request) {
        
        if (StringUtils.isBlank(request.endereco())) {
            throw EurekaException.ofValidation("Endereço não informado.");
        }
        
        if (StringUtils.isBlank(request.numero())) {
            throw EurekaException.ofValidation("Número não informado.");
        }
        
        if (StringUtils.isBlank(request.bairro())) {
            throw EurekaException.ofValidation("Bairro não informado.");
        }
        
        if (StringUtils.isBlank(request.cidade())) {
            throw EurekaException.ofValidation("Cidade não informada.");
        }
        
        if (StringUtils.isBlank(request.estado())) {
            throw EurekaException.ofValidation("Estado não informado.");
        }
        
        if (StringUtils.isBlank(request.cep())) {
            throw EurekaException.ofValidation("CEP não informado.");
        }

        if (StringUtils.isBlank(request.complemento())) {
            throw EurekaException.ofValidation("Complemento não informado.");
        }
    }
}
