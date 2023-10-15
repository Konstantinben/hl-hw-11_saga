package otus.ms.auth.repository;

import otus.ms.auth.model.entity.User;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByUuid(UUID uuid);
    Optional<User> findByEmail(String email);
}
