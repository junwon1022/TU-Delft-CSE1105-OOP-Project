package server.database;

import commons.ListOfCards;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("list")
public interface ListOfCardsRepository extends JpaRepository<ListOfCards, Long> {
}
