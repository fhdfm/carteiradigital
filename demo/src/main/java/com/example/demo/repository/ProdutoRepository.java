package com.example.demo.repository;

import com.example.demo.domain.model.Produto;
import com.example.demo.dto.ProdutoRequest;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProdutoRepository extends BaseRepository<Produto, Long> {
    <T> Optional<T> findByUuid(UUID uuid, Class<T> projectionClass);
    <T> List<T> findByCategoria_Uuid(UUID categoriaUuid, Class<T> projectionClass);
    Optional<Produto> findByUuid(UUID uuid);

    @Query("""
    select new com.example.demo.dto.ProdutoRequest(
        p.nome,
        p.foto,
        p.preco,
        p.categoria.uuid,
        p.departamento
    )
    from Produto p
    where p.uuid = :uuid
""")
    ProdutoRequest BuscarProdutoPorUuid(@Param("uuid") UUID uuid);

}
