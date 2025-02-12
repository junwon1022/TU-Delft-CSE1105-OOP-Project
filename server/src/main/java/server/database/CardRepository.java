package server.database;

import commons.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("card")
public interface CardRepository extends JpaRepository<Card, Long> {
}
