package otus.ms.app.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import otus.ms.app.model.entity.WarehouseItem;

import java.util.List;

@Repository
public interface WarehouseRepository extends CrudRepository<WarehouseItem, Long> {
    List<WarehouseItem> findAllByOrderByItemUuid();
}
