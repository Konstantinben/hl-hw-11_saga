package otus.ms.app.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import otus.ms.app.model.entity.Delivery;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DeliveryRepository extends CrudRepository<Delivery, UUID> {

    Optional<Delivery> getDeliveryByOrderUuidAndConfirmedIsTrue(UUID uuid);
    List<Delivery> getDeliveriesByOrderUuid(UUID uuid);

    List<Delivery> findDeliveriesByDeliveryDateAndStartTimeIsGreaterThanEqualAndEndTimeIsLessThanEqualAndConfirmedIsTrue(
            LocalDate localDate, LocalTime startTime, LocalTime endTime);
}
