package server.database;

import commons.Palette;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("palette")
public interface PaletteRepository extends JpaRepository<Palette, Long> {
}
