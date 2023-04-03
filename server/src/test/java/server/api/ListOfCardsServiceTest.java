package server.api;

import commons.Board;
import commons.ListOfCards;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import server.database.BoardRepository;
import server.database.ListOfCardsRepository;
import server.services.BoardService;
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
public class ListOfCardsServiceTest {

    @Autowired
    private transient BoardService boardService;

    @Autowired
    private transient BoardRepository boardRepo;

    @Autowired
    private transient ListOfCardsService listService;


    @Autowired
    private transient ListOfCardsRepository listRepo;


    @BeforeEach
    public void setup() {
        boardRepo.deleteAll();
        listRepo.deleteAll();
        boardRepo = Mockito.mock(BoardRepository.class);
        boardService = new BoardService(boardRepo);

        listRepo = Mockito.mock(ListOfCardsRepository.class);
        listService = new ListOfCardsService(listRepo);
    }

    @Test
    public void addListTest() throws Exception {

        Board b = new Board("My Schedule", "#111111", "#111111",
                "#111111","#111111","pass", new ArrayList<>(),
                new HashSet<>(), new HashSet<>());

        boardService.createBoard(b);

        Mockito.verify(boardRepo).save(b);

        ListOfCards l = new ListOfCards("My List",b,new ArrayList<>());

        b.addList(l);

        listService.createListOfCards(l,b);

        Mockito.verify(listRepo).save(l);
        assertThat(b.lists).contains(l);

        //EMPTY
        ListOfCards l2 = new ListOfCards("", b, new ArrayList<>());
        b.addList(l2);
        assertThatThrownBy(() -> {
            listService.createListOfCards(l2,b);
        }).isInstanceOf(Exception.class);

        //NULL
        ListOfCards l3 = new ListOfCards(null, b, new ArrayList<>());
        b.addList(l3);
        assertThatThrownBy(() -> {
            listService.createListOfCards(l3,b);
        }).isInstanceOf(Exception.class);



    }

    @Test
    public void editListTitleTest() throws Exception {

        Board b = new Board("My Schedule", "#111111", "#111111",
                "#111111","#111111","pass", new ArrayList<>(),
                new HashSet<>(), new HashSet<>());


        ListOfCards l = new ListOfCards("My List", b, new ArrayList<>());

        boardService.createBoard(b);

        when(boardRepo.findById(b.id)).thenReturn(Optional.of(b));
        when(listRepo.findById(l.id)).thenReturn(Optional.of(l));

        b.addList(l);

        listService.editListOfCardsTitle(l.id,"My New List");

        assertThat(l.title).isEqualTo("My New List");

        assertThatThrownBy(() -> {
            listService.editListOfCardsTitle(l.id,null);
        }).isInstanceOf(Exception.class);

        assertThatThrownBy(() -> {
            listService.editListOfCardsTitle(l.id,"");
        }).isInstanceOf(Exception.class);


    }

    @Test
    public void deleteListTest() throws Exception {

        Board b = new Board("My Schedule", "#111111", "#111111",
                "#111111","#111111","pass", new ArrayList<>(),
                new HashSet<>(), new HashSet<>());

        ListOfCards l = new ListOfCards("My List", b, new ArrayList<>());

        b.addList(l);

        boardService.createBoard(b);
        listService.createListOfCards(l,b);
        listService.deleteListOfCardsById(l.id);


        Mockito.verify(listRepo).deleteById(l.id);



    }

    @Test
    public void getAllListsTest() throws Exception {

        Board b = new Board("My Schedule", "#111111","#111111",
                "#111111","#111111", "pass", new ArrayList<>(),
                new HashSet<>(), new HashSet<>());

        ListOfCards l = new ListOfCards("My List", b, new ArrayList<>());

        ListOfCards l2 = new ListOfCards("My List 2", b, new ArrayList<>());

        List<ListOfCards> k = new ArrayList<>();

        b.addList(l);
        k.add(l);
        b.addList(l2);
        k.add(l2);

        assertThat(listService.getListsOfCards(b)).isEqualTo(k);


    }


}
