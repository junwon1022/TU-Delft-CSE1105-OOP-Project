package server.api;

import commons.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import server.database.*;
import server.services.*;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@SuppressWarnings({"MissingJavadocMethod","JavadocMethod"})
@SpringBootTest
public class TagServiceTest {

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
    private transient TagService tagService;

    @Autowired
    private transient TagRepository tagRepo;

    @BeforeEach
    public void setup() {
        boardRepo.deleteAll();
        listRepo.deleteAll();
        cardRepo.deleteAll();
        tagRepo.deleteAll();

        boardRepo = Mockito.mock(BoardRepository.class);
        boardService = new BoardService(boardRepo);

        listRepo = Mockito.mock(ListOfCardsRepository.class);
        listService = new ListOfCardsService(listRepo);

        cardRepo = Mockito.mock(CardRepository.class);
        cardService = new CardService(cardRepo);

        tagRepo = Mockito.mock(TagRepository.class);
        tagService = new TagService(tagRepo);
    }


    @Test
    public void addTagTest() throws Exception {

        Board b = new Board("My Schedule", "#111111","#111111",
                "#111111","#111111", "pass", new ArrayList<>(),
                new HashSet<>(), new HashSet<>());
        boardService.createBoard(b);
        Mockito.verify(boardRepo).save(b);


        ListOfCards l = new ListOfCards("My List",b,new ArrayList<>());
        b.addList(l);
        listService.createListOfCards(l,b);
        Mockito.verify(listRepo).save(l);
        Card c = new Card("CG","Finish CG Study","#555555",l,new ArrayList<>(),
                new HashSet<>(), null);

        l.addCard(c);
        cardService.createCard(c,l,b);

        Mockito.verify(cardRepo).save(c);
        Tag t = new Tag("Tag 2","#555555", "#555555", b, new HashSet<>());

        c.addTag(t);

        tagService.createTag(t,b);

        Mockito.verify(tagRepo).save(t);

        //EMPTY

        Tag t2 = new Tag("","#555555", "#555555", b, new HashSet<>());
        c.addTag(t2);

        assertThatThrownBy(() -> {
            tagService.createTag(t2,b);
        }).isInstanceOf(Exception.class);


        Tag t3 = new Tag(null,"#555555", "#555555", b, new HashSet<>());
        c.addTag(t3);

        assertThatThrownBy(() -> {
            tagService.createTag(t3, b);
        }).isInstanceOf(Exception.class);


    }

    @Test
    public void editTagTest() throws Exception {

        Board b = new Board("My Schedule", "#111111","#111111",
                "#111111","#111111", "pass", new ArrayList<>(),
                new HashSet<>(), new HashSet<>());
        ListOfCards l = new ListOfCards("My List", b, new ArrayList<>());
        Card c = new Card("CG","Finish CG Study","#555555", l, new ArrayList<>(),
                new HashSet<>(), null);
        Tag t = new Tag("Tag 2","#555555", "#555555", b, new HashSet<>());

        boardService.createBoard(b);
        listService.createListOfCards(l, b);
        cardService.createCard(c, l, b);
        tagService.createTag(t, b);

        when(boardRepo.findById(b.id)).thenReturn(Optional.of(b));
        when(listRepo.findById(l.id)).thenReturn(Optional.of(l));
        when(cardRepo.findById(c.id)).thenReturn(Optional.of(c));
        when(tagRepo.findById(t.id)).thenReturn(Optional.of(t));

        b.addList(l);
        l.addCard(c);
        c.addTag(t);
        tagService.editTag(t.id, new Tag("My New Text","#333333", "#343333", b, new HashSet<>()));

        assertThat(t.name).isEqualTo("My New Text");
        assertThat(t.colour).isEqualTo("#333333");
        assertThat(t.font).isEqualTo("#343333");
    }

//    @Test
//    public void deleteTagTest() throws Exception {
//
//        Board b = new Board("My Schedule", "#111111","#111111",
//                "#111111","#111111", "pass", new ArrayList<>(),
//                new HashSet<>(), new HashSet<>());
//
//        ListOfCards l = new ListOfCards("My List", b, new ArrayList<>());
//
//        Card c = new Card("CG","Finish CG Study","#555555", l, new ArrayList<>(),
//                new HashSet<>(), null);
//
//        Tag t = new Tag("Tag 2","#555555", "#555555", b, new HashSet<>());
//
//        boardService.createBoard(b);
//        listService.createListOfCards(l, b);
//        cardService.createCard(c, l, b);
//        tagService.createTag(t, b);
//
//        when(boardRepo.findById(b.id)).thenReturn(Optional.of(b));
//        when(listRepo.findById(l.id)).thenReturn(Optional.of(l));
//        when(cardRepo.findById(c.id)).thenReturn(Optional.of(c));
//        when(tagRepo.findById(t.id)).thenReturn(Optional.of(t));
//
//        b.addList(l);
//        l.addCard(c);
//        c.addTag(t);
//
//        tagService.deleteTagById(t.id);
//
//        Mockito.verify(tagRepo).deleteById(t.id);
//    }


    @Test
    public void getAllTagsTest() throws Exception {

        Board b = new Board("My Schedule", "#111111","#111111",
                "#111111","#111111", "pass", new ArrayList<>(),
                new HashSet<>(), new HashSet<>());

        ListOfCards l = new ListOfCards("My List", b, new ArrayList<>());

        Card c = new Card("CG","Finish CG Study","#555555", l, new ArrayList<>(),
                new HashSet<>(), null);

        Tag t = new Tag("Tag ","#555555", "#555555", b, new HashSet<>());

        Tag t2 = new Tag("Tag 3ij","#555555", "#555555", b, new HashSet<>());

        Set<Tag> k = new HashSet<>();

        b.addList(l);
        l.addCard(c);

        b.addTag(t);
        b.addTag(t2);

        k.add(t);
        k.add(t2);

        System.out.println("Equal to:" + k);

        assertThat(tagService.getTags(b).size()).isEqualTo(k.size());
    }


    @Test
    void tagInCardTest() {
        Board b = new Board("My Schedule", "#111111", "#111111",
                "#111111","#111111","pass", new ArrayList<>(),
                new HashSet<>(), new HashSet<>());
        ListOfCards l = new ListOfCards("List 1", b, new ArrayList<>());
        Card c = new Card("Card 1","Finish CG Study",
                "#555555", l, new ArrayList<>(),new HashSet<>(), null);
        Tag t = new Tag("Tag 2","#555555", "#555555", b,new HashSet<>());

        c.addTag(t);

        assertThat(tagService.tagInBoard(t, b)).isTrue();
    }

}
