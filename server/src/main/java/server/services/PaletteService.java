package server.services;

import commons.Board;
import commons.Card;
import commons.Palette;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import server.database.PaletteRepository;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Service
public class PaletteService {

    private PaletteRepository paletteRepository;

    /**
     * Constructor for the service
     * @param paletteRepository
     */
    @Autowired
    public PaletteService(@Qualifier("palette") PaletteRepository paletteRepository) {
        this.paletteRepository = paletteRepository;
    }

    /**
     * Method that gets the palette by its id
     * @param id
     * @return the palette
     * @throws Exception
     */
    public Palette getPaletteById(long id) throws Exception{
        return paletteRepository.findById(id)
                .orElseThrow(() -> new Exception("Palette not found with id " + id));
    }

    /**
     * Method that checks if the palette is in the board
     * @param palette
     * @param board
     * @return a boolean
     */
    public boolean paletteInBoard(Palette palette, Board board){
        return Objects.equals(palette.board, board);
    }

    /**
     * Method that gets all the palettes
     * @param board
     * @return a set of palettes
     */
    public Set<Palette> getPalettes(Board board){
        return board.palettes;
    }

    /**
     * Method that renames a palette
     * @param paletteId
     * @param newTitle
     * @return the renamed palette
     * @throws Exception
     */
    public Palette renamePalette(Long paletteId, String newTitle) throws Exception{
        if(newTitle == null)
            throw new Exception("Palette title cannot be null");
        Palette p = getPaletteById(paletteId);
        p.title = newTitle;
        paletteRepository.save(p);
        return p;
    }

    /**
     * Method that creates a palette
     * @param palette
     * @param board
     * @return the new palette
     */
    public Palette createPalette(Palette palette, Board board){
        palette.board = board;
        return paletteRepository.save(palette);
    }

    /**
     * Method that removes a palette by its id
     * @param id
     */
    public void removePaletteById(long id){
        paletteRepository.deleteById(id);
    }

    /**
     * Method that edits the palette's background
     * @param id
     * @param background
     * @return the edited palette
     * @throws Exception
     */
    public Palette editPaletteBackground(long id, String background) throws Exception{
        if(background == null)
            throw new Exception("Colour should not be empty");
        Palette p = getPaletteById(id);
        p.background = background;
        return paletteRepository.save(p);
    }

    /**
     * Method that changes the palette's font colour
     * @param id
     * @param colour
     * @return the edited palette
     * @throws Exception
     */
    public Palette editPaletteFont(long id, String colour) throws Exception{
        if(colour == null)
            throw new Exception("Colour should not be empty");
        Palette p = getPaletteById(id);
        p.font = colour;
        return paletteRepository.save(p);
    }

    /**
     * Method that gets the default palette
     * @param board
     * @return the default palette
     * @throws Exception
     */
    public Palette getDefault(Board board) throws Exception{
        Set<Palette> all = getPalettes(board);
        for(Palette p: all){
            if(p.isDefault)
                return p;
        }
        return null;
    }

    /**
     * Method that sets the given palette as default
     * @param palette
     * @param board
     * @return the default palette
     * @throws Exception
     */
    public Palette setDefault(Palette palette, Board board) throws Exception{
        Palette oldDefault = getDefault(board);

        palette.isDefault = true;
        paletteRepository.save(palette);

        if(oldDefault != null  && !oldDefault.equals(palette)){
            oldDefault.isDefault = false;
            paletteRepository.save(oldDefault);
        }

        return palette;
    }

    /**
     * Method that gets the cards of a palette
     * @param paletteId
     * @return the cards
     * @throws Exception
     */
    public Set<Card> getCardsOfPalette(long paletteId) throws Exception{
        Palette palette = getPaletteById(paletteId);
        return palette.cards;
    }

    /**
     * Method that gets the cards of a palette
     * @param paletteId
     * @param oldPaletteId
     * @throws Exception
     */
    public void changeOfPalette(long paletteId, long oldPaletteId) throws Exception{
        Palette palette = getPaletteById(paletteId);
        Palette oldPalette = getPaletteById(oldPaletteId);

        Set<Card> cards = oldPalette.cards;
        palette.cards = cards;
        oldPalette.cards = new HashSet<>();
        paletteRepository.save(palette);
        paletteRepository.save(oldPalette);
    }
}
