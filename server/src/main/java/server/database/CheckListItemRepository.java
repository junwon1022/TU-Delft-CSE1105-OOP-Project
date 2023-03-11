package server.database;

import commons.CheckListItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("check")
public interface CheckListItemRepository extends JpaRepository<CheckListItem, Long> {
}
