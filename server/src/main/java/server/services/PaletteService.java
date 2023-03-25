package server.services;

import commons.Board;
import commons.Palette;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import server.database.PaletteRepository;

import java.util.Objects;
import java.util.Set;

@Service
public class PaletteService {

    private PaletteRepository paletteRepository;

    @Autowired
    public PaletteService(@Qualifier("palette") PaletteRepository paletteRepository) {
        this.paletteRepository = paletteRepository;
    }

    public Palette getPaletteById(long id) throws Exception{
        return paletteRepository.findById(id)
                .orElseThrow(() -> new Exception("Palette not found with id " + id));
    }

    public boolean paletteInBoard(Palette palette, Board board){
        return Objects.equals(palette.board, board);
    }

    public Set<Palette> getPalettes(Board board){
        return board.palettes;
    }

    public Palette renamePalette(Long paletteId, String newTitle) throws Exception{
        if(newTitle == null)
            throw new Exception("Palette title cannot be null");
        Palette p = getPaletteById(paletteId);
        p.title = newTitle;
        paletteRepository.save(p);
        return p;
    }

    public Palette createPalette(Palette palette, Board board){
        palette.board = board;
        return paletteRepository.save(palette);
    }

    public void removePaletteById(long id){
        paletteRepository.deleteById(id);
    }

    public Palette editPaletteBackground(long id, String background) throws Exception{
        if(background == null)
            throw new Exception("Colour should not be empty");
        Palette p = getPaletteById(id);
        p.background = background;
        return paletteRepository.save(p);
    }

    public Palette editPaletteFont(long id, String colour) throws Exception{
        if(colour == null)
            throw new Exception("Colour should not be empty");
        Palette p = getPaletteById(id);
        p.font = colour;
        return paletteRepository.save(p);
    }

    public Palette getDefault(Board board) throws Exception{
        Set<Palette> all = getPalettes(board);
        for(Palette p: all){
            if(p.isDefault)
                return p;
        }
        throw new Exception("Default is not found");
    }

    public Palette setDefault(Palette palette, Board board) throws Exception{
        Palette oldDefault = getDefault(board);

        palette.setDefault();
        paletteRepository.save(palette);

        oldDefault.isDefault = false;
        paletteRepository.save(oldDefault);

        return palette;
    }



}
