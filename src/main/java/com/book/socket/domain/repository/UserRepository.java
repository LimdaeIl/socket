package com.book.socket.domain.repository;

import com.book.socket.domain.repository.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends CrudRepository<User, Long> {

    Optional<User> findByName(String name);

    boolean existsByName(String name);

    @Query("""
            SELECT u.name
            FROM User AS u
            WHERE LOCATE(LOWER(:pattern), LOWER(u.name)) > 0
                 AND u.name != :user
            """)
    List<String> findNameByNameMatch(@Param("pattern") String pattern, @Param("user") String user);
}
