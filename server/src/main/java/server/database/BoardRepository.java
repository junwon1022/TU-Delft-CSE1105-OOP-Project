package server.database;

import commons.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("board")
public interface BoardRepository extends JpaRepository<Board, Long> {
    Object findByKey(String key);
}
