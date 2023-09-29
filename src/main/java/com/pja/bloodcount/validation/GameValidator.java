package com.pja.bloodcount.validation;

import com.pja.bloodcount.exceptions.GameNotFoundException;
import com.pja.bloodcount.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public class GameValidator extends EntityValidator<Game, Long>{

    public GameValidator(JpaRepository<Game, Long> repository) {
        super(repository);
    }

    @Override
    protected RuntimeException getCollectionIsEmptyException() {
        return null;
    }

    @Override
    protected RuntimeException getNotFoundException(Long id) {
        return new GameNotFoundException(id);
    }
}
