package otus.ms.app.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import otus.ms.app.model.entity.Order;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends CrudRepository<Order, Long> {

    Optional<Order> getOrderByOrderUuid(UUID uuid);

    Optional<Order> getOrderByUserUuidAndEtagAndConfirmedIsTrue(UUID userUuid, Integer etag);
}
