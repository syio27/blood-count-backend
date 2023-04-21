package com.pja.bloodcount.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Abstract Base Entity Validator
 * @param <E> desired entity
 * @param <T> desired entities unique identifier
 */
@RequiredArgsConstructor
public abstract class EntityValidator<E, T> {

    protected final JpaRepository<E, T> repository;

    /**
     * Validates if E entity with id exists, if not throws Runtime Exception (NotFoundException)
     * @param id of type String
     * @return E object instance
     */
    public E validateIfExistsAndGet(T id){
        return repository.findById(id).orElseThrow(() -> getNotFoundException(id));
    }

    /**
     * Validates if List<E> is not empty, if not throws Runtime Exception (CollectionIsEmpty)
     * @return List<E>
     */
    public List<E> validateIfAnyExistsAndGet(){
        List<E> list = repository.findAll();
        if(list.isEmpty()) throw getCollectionIsEmptyException();
        return list;
    }

    protected abstract RuntimeException getCollectionIsEmptyException();

    protected abstract RuntimeException getNotFoundException(T id);
}
