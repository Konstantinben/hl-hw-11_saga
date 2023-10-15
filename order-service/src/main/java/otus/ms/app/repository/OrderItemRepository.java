package otus.ms.app.repository;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import otus.ms.app.model.entity.OrderItem;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderItemRepository extends CrudRepository<OrderItem, Long> {

    @Modifying
    @Query(value = "INSERT INTO public.order_item (order_id, item_id, item_uuid, quantity, price) " +
            "VALUES (:orderId, :itemId, :itemUuid, :quantity, :price)")
    void saveOrderItem(Long orderId, Long itemId, UUID itemUuid, int quantity, int price);

    List<OrderItem> getByOrderId(Long orderId);
}
