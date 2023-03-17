package server.api;

import commons.Board;
import commons.Card;
import commons.ListOfCards;
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

        Board b = new Board("My Schedule", "#111111", "pass", new ArrayList<>());

        boardService.createBoard(b);

        Mockito.verify(boardRepo).save(b);

        ListOfCards l = new ListOfCards("My List","#555555",b,new ArrayList<>());

        b.addList(l);

        listService.createListOfCards(l,b);

        Mockito.verify(listRepo).save(l);

        Card c = new Card("CG","Finish CG Study","#555555",l,new ArrayList<>(),new HashSet<>());

        l.addCard(c);


        cardService.createCard(c,l,b);

        Mockito.verify(cardRepo).save(c);


        //EMPTY
        Card c2 = new Card("","Finish CG Study","#555555",l,new ArrayList<>(),new HashSet<>());
        l.addCard(c2);
        assertThatThrownBy(() -> {
            cardService.createCard(c2,l,b);
        }).isInstanceOf(Exception.class);

        //NULL
        Card c3 = new Card(null,"Finish CG Study","#555555",l,new ArrayList<>(),new HashSet<>());
        l.addCard(c3);
        assertThatThrownBy(() -> {
            cardService.createCard(c2,l,b);
        }).isInstanceOf(Exception.class);





    }

    @Test
    public void editCardTitleTest() throws Exception {

        Board b = new Board("My Schedule", "#111111", "pass", new ArrayList<>());


        ListOfCards l = new ListOfCards("My List","#555555",b,new ArrayList<>());


        Card c = new Card("CG","Finish CG Study","#555555",l,new ArrayList<>(),new HashSet<>());

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
    public void deleteCardTest() throws Exception {

        Board b = new Board("My Schedule", "#111111", "pass", new ArrayList<>());

        ListOfCards l = new ListOfCards("My List","#555555",b,new ArrayList<>());

        b.addList(l);

        Card c = new Card("CG","Finish CG Study","#555555",l,new ArrayList<>(),new HashSet<>());

        l.addCard(c);

        boardService.createBoard(b);
        listService.createListOfCards(l,b);
        cardService.createCard(c,l,b);
        cardService.deleteCardById(c.id);

        Mockito.verify(cardRepo).deleteById(c.id);


    }

    @Test
    public void getAllCardsTest() throws Exception {

        Board b = new Board("My Schedule", "#111111", "pass", new ArrayList<>());

        ListOfCards l = new ListOfCards("My List","#555555",b,new ArrayList<>());

        Card c = new Card("CG","Finish CG Study","#555555",l,new ArrayList<>(),new HashSet<>());

        Card c2 = new Card("CG2","Finish CG Study","#555555",l,new ArrayList<>(),new HashSet<>());



        List<Card> k = new ArrayList<>();

        b.addList(l);
        l.addCard(c);
        l.addCard(c2);

        k.add(c);
        k.add(c2);

        assertThat(cardService.getCards(l)).isEqualTo(k);


    }


}
