package server.api;

import commons.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import server.database.BoardRepository;
import server.database.CardRepository;
import server.database.ListOfCardsRepository;
import server.services.BoardService;
import server.services.CardService;
import server.services.ListOfCardsService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@SuppressWarnings({"MissingJavadocMethod","JavadocMethod"})
@SpringBootTest
public class CardServiceTest {

    @Autowired
    private transient BoardService boardService;

    @Autowired
    private transient BoardRepository boardRepo;

    @Autowired
    private transient ListOfCardsService listService;

    @Autowired
    private transient ListOfCardsRepository listRepo;


    @Autowired
    private transient CardService cardService;

    @Autowired
    private transient CardRepository cardRepo;


    @BeforeEach
    public void setup() {
        boardRepo.deleteAll();
        listRepo.deleteAll();
        cardRepo.deleteAll();

        boardRepo = Mockito.mock(BoardRepository.class);
        boardService = new BoardService(boardRepo);

        listRepo = Mockito.mock(ListOfCardsRepository.class);
        listService = new ListOfCardsService(listRepo);

        cardRepo = Mockito.mock(CardRepository.class);
        cardService = new CardService(cardRepo);
    }


    @Test
    public void addCardTest() throws Exception {

        Board b = new Board("My Schedule", "#111111", "#111111",
                "#111111","#111111","pass", new ArrayList<>(),
                new HashSet(), new HashSet<>());

        boardService.createBoard(b);

        Mockito.verify(boardRepo).save(b);

        ListOfCards l = new ListOfCards("My List", b,new ArrayList<>());

        b.addList(l);

        listService.createListOfCards(l,b);

        Mockito.verify(listRepo).save(l);

        Card c = new Card("CG","Finish CG Study","#555555",l,
                new ArrayList<>(),new HashSet<>() ,null);

        l.addCard(c);


        cardService.createCard(c,l,b);

        Mockito.verify(cardRepo).save(c);


        //EMPTY
        Card c2 = new Card("","Finish CG Study","#555555",l,
                new ArrayList<>(),new HashSet<>(), null);
        l.addCard(c2);
        assertThatThrownBy(() -> {
            cardService.createCard(c2,l,b);
        }).isInstanceOf(Exception.class);

        //NULL
        Card c3 = new Card(null,"Finish CG Study","#555555",l,new ArrayList<>(),
                new HashSet<>(), null);
        l.addCard(c3);
        assertThatThrownBy(() -> {
            cardService.createCard(c2,l,b);
        }).isInstanceOf(Exception.class);





    }

    @Test
    public void editCardTitleTest() throws Exception {

        Board b = new Board("My Schedule", "#111111","#111111","#111111",
                "#111111", "pass", new ArrayList<>(),
                new HashSet<>(), new HashSet<>());


        ListOfCards l = new ListOfCards("My List",b,new ArrayList<>());


        Card c = new Card("CG","Finish CG Study","#555555",l,new ArrayList<>(),
                new HashSet<>(), null);

        boardService.createBoard(b);
        listService.createListOfCards(l,b);


        when(boardRepo.findById(b.id)).thenReturn(Optional.of(b));
        when(listRepo.findById(l.id)).thenReturn(Optional.of(l));
        when(cardRepo.findById(c.id)).thenReturn(Optional.of(c));

        b.addList(l);
        l.addCard(c);

        cardService.editCardTitle(c.id,"My New Card");

        assertThat(c.title).isEqualTo("My New Card");


        assertThatThrownBy(() -> {
            cardService.editCardTitle(c.id,"");
        }).isInstanceOf(Exception.class);

        assertThatThrownBy(() -> {
            cardService.editCardTitle(c.id,null);
        }).isInstanceOf(Exception.class);

    }

    @Test
    public void getAllCardsTest() throws Exception {

        Board b = new Board("My Schedule", "#111111","#111111",
                "#111111","#111111", "pass", new ArrayList<>(),
                new HashSet<>(), new HashSet<>());

        ListOfCards l = new ListOfCards("My List",b,new ArrayList<>());

        Card c = new Card("CG","Finish CG Study","#555555",l,new ArrayList<>(),
                new HashSet<>(), null);

        Card c2 = new Card("CG2","Finish CG Study","#555555",l,new ArrayList<>(),
                new HashSet<>(), null);



        List<Card> k = new ArrayList<>();

        b.addList(l);
        l.addCard(c);
        l.addCard(c2);

        k.add(c);
        k.add(c2);

        assertThat(cardService.getCards(l)).isEqualTo(k);
    }

    /**
     * Test addTagToCard method.
     */
    @Test
    public void addTagToCardTest() throws Exception {

        Board b = new Board("My Schedule", "#111111","#111111","#111111",
                "#111111", "pass", new ArrayList<>(),
                new HashSet<>(), new HashSet<>());

        ListOfCards l = new ListOfCards("My List",b,new ArrayList<>());

        Card c = new Card("CG","Finish CG Study","#555555",l,new ArrayList<>(),
                new HashSet<>(), null);
        Tag t = new Tag("Solve Phong Shading Questions","#555555", "#555555", b,new HashSet<>());

        b.addList(l);
        l.addCard(c);

        cardService.addTagToCard(c ,t);

        assertThat(c.tags.contains(t)).isTrue();
    }

    /**
     * Test removeTagFromCard method.
     */
    @Test
    public void removeTagFromCardTest() throws Exception {

        Board b = new Board("My Schedule", "#111111","#111111","#111111",
                "#111111", "pass", new ArrayList<>(),
                new HashSet<>(), new HashSet<>());

        ListOfCards l = new ListOfCards("My List",b,new ArrayList<>());

        Card c = new Card("CG","Finish CG Study","#555555",l,new ArrayList<>(),
                new HashSet<>(), null);
        Tag t = new Tag("Solve Phong Shading Questions","#555555", "#555555", b,new HashSet<>());

        b.addList(l);
        l.addCard(c);

        cardService.addTagToCard(c ,t);

        assertThat(c.tags.contains(t)).isTrue();

        cardService.removeTagFromCard(c,t);

        assertThat(c.tags.contains(t)).isFalse();
    }

    /**
     * Test removePaletteFromCard method.
     */
    @Test
    public void removePaletteFromCardTest() throws Exception {

        Board b = new Board("My Schedule", "#111111","#111111","#111111",
                "#111111", "pass", new ArrayList<>(),
                new HashSet<>(), new HashSet<>());

        ListOfCards l = new ListOfCards("My List",b,new ArrayList<>());

        Card c = new Card("CG","Finish CG Study","#555555",l,new ArrayList<>(),
                new HashSet<>(), null);
        Palette p = new Palette("default", "#0000000", "#0000000",
                true, b, new HashSet<>());

        b.addList(l);
        l.addCard(c);
        b.addPalette(p);

        cardService.addPaletteToCard(c,p);

        assertThat(c.palette).isEqualTo(p);

        cardService.removePaletteFromCard(c, p);

        assertThat(c.palette).isNull();
    }


}
