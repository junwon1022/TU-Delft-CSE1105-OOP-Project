package server.database;

import commons.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("board")
public interface BoardRepository extends JpaRepository<Board, Long> {
    /**
     * Find a board by its key
     * @param key
     * @return - the board
     */
    Object findByKey(String key);
}
