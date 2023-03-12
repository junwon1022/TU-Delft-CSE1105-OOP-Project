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

        Board b = new Board("My Schedule", "#111111", "read", "write", new ArrayList<>());

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


        Tag t = new Tag("Tag 2","#555555",new HashSet<>());

        c.addTag(t);

        tagService.createTag(t,c);

        Mockito.verify(tagRepo).save(t);

        //EMPTY

        Tag t2 = new Tag("","#555555",new HashSet<>());
        c.addTag(t2);

        assertThatThrownBy(() -> {
            tagService.createTag(t2,c);
        }).isInstanceOf(Exception.class);


        Tag t3 = new Tag(null,"#555555",new HashSet<>());
        c.addTag(t3);

        assertThatThrownBy(() -> {
            tagService.createTag(t3,c);
        }).isInstanceOf(Exception.class);


    }

    @Test
    public void editTagTitleTest() throws Exception {

        Board b = new Board("My Schedule", "#111111", "read", "write", new ArrayList<>());


        ListOfCards l = new ListOfCards("My List","#555555",b,new ArrayList<>());


        Card c = new Card("CG","Finish CG Study","#555555",l,new ArrayList<>(),new HashSet<>());

        Tag t = new Tag("Tag 2","#555555",new HashSet<>());


        boardService.createBoard(b);
        listService.createListOfCards(l,b);
        cardService.createCard(c,l,b);
        tagService.createTag(t,c);


        when(boardRepo.findById(b.id)).thenReturn(Optional.of(b));
        when(listRepo.findById(l.id)).thenReturn(Optional.of(l));
        when(cardRepo.findById(c.id)).thenReturn(Optional.of(c));
        when(tagRepo.findById(t.id)).thenReturn(Optional.of(t));

        b.addList(l);
        l.addCard(c);
        c.addTag(t);

        tagService.editTagName(t.id,"My New Text");

        assertThat(t.name).isEqualTo("My New Text");


        tagService.editTagColour(t.id,"#333333");

        assertThat(t.colour).isEqualTo("#333333");

        assertThatThrownBy(() -> {
            tagService.editTagName(t.id,"");
        }).isInstanceOf(Exception.class);
        assertThatThrownBy(() -> {
            tagService.editTagName(t.id,null);
        }).isInstanceOf(Exception.class);

        assertThatThrownBy(() -> {
            tagService.editTagColour(t.id,"");
        }).isInstanceOf(Exception.class);
        assertThatThrownBy(() -> {
            tagService.editTagColour(t.id,null);
        }).isInstanceOf(Exception.class);

    }

    @Test
    public void deleteTagTest() throws Exception {

        Board b = new Board("My Schedule", "#111111", "read", "write", new ArrayList<>());

        ListOfCards l = new ListOfCards("My List","#555555",b,new ArrayList<>());

        b.addList(l);

        Card c = new Card("CG","Finish CG Study","#555555",l,new ArrayList<>(),new HashSet<>());

        l.addCard(c);

        Tag t = new Tag("Tag 2","#555555",new HashSet<>());

        c.addTag(t);

        boardService.createBoard(b);
        listService.createListOfCards(l,b);
        cardService.createCard(c,l,b);
        tagService.createTag(t,c);
        tagService.deleteTagById(t.id);

        Mockito.verify(tagRepo).deleteById(t.id);
    }


    @Test
    public void getAllTagsTest() throws Exception {

        Board b = new Board("My Schedule", "#111111", "read", "write", new ArrayList<>());

        ListOfCards l = new ListOfCards("My List","#555555",b,new ArrayList<>());

        Card c = new Card("CG","Finish CG Study","#555555",l,new ArrayList<>(),new HashSet<>());

        Tag t = new Tag("Tag ","#555555",new HashSet<>());

        Tag t2 = new Tag("Tag 3ij","#555555",new HashSet<>());

        Set<Tag> k = new HashSet<>();

        b.addList(l);
        l.addCard(c);

        c.addTag(t);
        c.addTag(t2);

        k.add(t);
        k.add(t2);

        System.out.println("Equal to:" + k);

        assertThat(tagService.getTags(c).size()).isEqualTo(k.size());


    }


    @Test
    void tagInCardTest() {
        Board b = new Board("My Schedule", "#111111", "read", "write", new ArrayList<>());
        ListOfCards l = new ListOfCards("List 1","#555555",b,new ArrayList<>());
        Card c = new Card("Card 1","Finish CG Study","#555555",l,new ArrayList<>(),new HashSet<>());
        Tag t = new Tag("Tag 2","#555555",new HashSet<>());

        c.addTag(t);

        assertThat(tagService.tagInCard(t, c)).isTrue();
    }

}
