package otus.ms.app.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import otus.ms.app.model.entity.Balance;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BalanceRepository extends CrudRepository<Balance, UUID> {

    Optional<Balance> findBalanceByUserUuid(UUID userUuid);
}
