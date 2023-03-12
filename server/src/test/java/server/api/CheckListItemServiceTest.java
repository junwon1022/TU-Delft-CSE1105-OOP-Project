package server.api;

import commons.Board;
import commons.Card;
import commons.CheckListItem;
import commons.ListOfCards;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import server.database.BoardRepository;
import server.database.CardRepository;
import server.database.CheckListItemRepository;
import server.database.ListOfCardsRepository;
import server.services.BoardService;
import server.services.CardService;
import server.services.CheckListItemService;
import server.services.ListOfCardsService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@SpringBootTest
public class CheckListItemServiceTest {

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

    @Autowired
    private transient CheckListItemService checkService;

    @Autowired
    private transient CheckListItemRepository checkRepo;

    @BeforeEach
    public void setup() {
        boardRepo.deleteAll();
        listRepo.deleteAll();
        cardRepo.deleteAll();
        checkRepo.deleteAll();

        boardRepo = Mockito.mock(BoardRepository.class);
        boardService = new BoardService(boardRepo);

        listRepo = Mockito.mock(ListOfCardsRepository.class);
        listService = new ListOfCardsService(listRepo);

        cardRepo = Mockito.mock(CardRepository.class);
        cardService = new CardService(cardRepo);

        checkRepo = Mockito.mock(CheckListItemRepository.class);
        checkService = new CheckListItemService(checkRepo);
    }


    @Test
    public void addCheckTest() throws Exception {

        Board b = new Board("My Schedule", "#111111", "read", "write", new ArrayList<>());
        boardService.createBoard(b);
        Mockito.verify(boardRepo).save(b);


        ListOfCards l = new ListOfCards("My List","#555555",b,new ArrayList<>());
        b.addList(l);

        listService.createListOfCards(l,b);


        Card c = new Card("CG","Finish CG Study","#555555",l,new ArrayList<>(),new HashSet<>());

        l.addCard(c);

        cardService.createCard(c,l,b);


        CheckListItem ch = new CheckListItem("Solve Phong Shading Questions",true,c);

        c.addCheckListItem(ch);

        checkService.createCheckListItem(ch,c);

        Mockito.verify(checkRepo).save(ch);

        //EMPTY

        CheckListItem ch2 = new CheckListItem(null,true,c);
        c.addCheckListItem(ch2);
        assertThatThrownBy(() -> {
            checkService.createCheckListItem(ch2,c);
        }).isInstanceOf(Exception.class);

        //NULL
        CheckListItem ch3 = new CheckListItem(null,true,c);
        c.addCheckListItem(ch3);
        assertThatThrownBy(() -> {
            checkService.createCheckListItem(ch3,c);
        }).isInstanceOf(Exception.class);


    }

    @Test
    public void editCheckTitleTest() throws Exception {

        Board b = new Board("My Schedule", "#111111", "read", "write", new ArrayList<>());


        ListOfCards l = new ListOfCards("My List","#555555",b,new ArrayList<>());


        Card c = new Card("CG","Finish CG Study","#555555",l,new ArrayList<>(),new HashSet<>());


        CheckListItem ch = new CheckListItem("Solve Phong Shading Questions",true,c);

        boardService.createBoard(b);
        listService.createListOfCards(l,b);
        cardService.createCard(c,l,b);
        checkService.createCheckListItem(ch,c);


        when(boardRepo.findById(b.id)).thenReturn(Optional.of(b));
        when(listRepo.findById(l.id)).thenReturn(Optional.of(l));
        when(cardRepo.findById(c.id)).thenReturn(Optional.of(c));
        when(checkRepo.findById(ch.id)).thenReturn(Optional.of(ch));

        b.addList(l);
        l.addCard(c);
        c.addCheckListItem(ch);

        checkService.editCheckText(ch.id,"My New Text");

        assertThat(ch.text).isEqualTo("My New Text");


        checkService.editCompletion(ch);

        assertThat(ch.completed).isEqualTo(false);

        assertThatThrownBy(() -> {
            checkService.editCheckText(ch.id,"");
        }).isInstanceOf(Exception.class);

        assertThatThrownBy(() -> {
            checkService.editCheckText(ch.id,null);
        }).isInstanceOf(Exception.class);

    }

    @Test
    public void deleteCheckTest() throws Exception {

        Board b = new Board("My Schedule", "#111111", "read", "write", new ArrayList<>());

        ListOfCards l = new ListOfCards("My List","#555555",b,new ArrayList<>());

        b.addList(l);

        Card c = new Card("CG","Finish CG Study","#555555",l,new ArrayList<>(),new HashSet<>());

        l.addCard(c);

        CheckListItem ch = new CheckListItem("Solve Phong Shading Questions",true,c);

        c.addCheckListItem(ch);

        boardService.createBoard(b);
        listService.createListOfCards(l,b);
        cardService.createCard(c,l,b);
        checkService.createCheckListItem(ch,c);
        checkService.deleteCheckListItemById(ch.id);

        Mockito.verify(checkRepo).deleteById(ch.id);
    }


    @Test
    public void getAllChecksTest() throws Exception {

        Board b = new Board("My Schedule", "#111111", "read", "write", new ArrayList<>());

        ListOfCards l = new ListOfCards("My List","#555555",b,new ArrayList<>());

        Card c = new Card("CG","Finish CG Study","#555555",l,new ArrayList<>(),new HashSet<>());

        CheckListItem ch = new CheckListItem("Solve Phong Shading Questions",true,c);

        CheckListItem ch2 = new CheckListItem("Solve Phong Shading Questions 2",true,c);

        List<CheckListItem> k = new ArrayList<>();

        b.addList(l);
        l.addCard(c);
        c.addCheckListItem(ch);
        c.addCheckListItem(ch2);


        k.add(ch);
        k.add(ch2);
        assertThat(checkService.getChecklist(c)).isEqualTo(k);


    }




}
