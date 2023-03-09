package server.database;

import commons.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("tag")
public interface TagRepository extends JpaRepository<Tag, Long> {
}
