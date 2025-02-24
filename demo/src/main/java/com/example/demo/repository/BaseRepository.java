package com.example.demo.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface BaseRepository<E, ID> 
        extends JpaRepository<E, ID>, JpaSpecificationExecutor<E> {
    
        default <R> Page<R> findAllProjected(Specification<E> spec, 
                                            Pageable pageable, 
                                            Class<R> projectionClass) {
            return this.findBy(spec, query ->
                query.as(projectionClass).page(pageable));
        }
    
}
