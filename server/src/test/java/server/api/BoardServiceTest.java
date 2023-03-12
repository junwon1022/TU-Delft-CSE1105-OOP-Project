package server.api;

import commons.Board;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import server.database.BoardRepository;
import server.services.BoardService;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;
@SpringBootTest
public class BoardServiceTest {

    @Autowired
    private transient BoardService boardService;

    @Autowired
    private transient BoardRepository boardRepo;

    @BeforeEach
    public void setup() {
        boardRepo.deleteAll();
        boardRepo = Mockito.mock(BoardRepository.class);
        boardService = new BoardService(boardRepo);
    }

    @Test
    public void addBoardTest() throws Exception {

        Board b = new Board("My Schedule", "#111111", "read", "write", new ArrayList<>());

        boardService.createBoard(b);

        Mockito.verify(boardRepo).save(b);


        Board nullBoard = new Board(null, "#111111", "read", "write", new ArrayList<>());

        assertThatThrownBy(() -> {
            boardService.createBoard(nullBoard);
        }).isInstanceOf(Exception.class);

        Board emptyBoard = new Board("", "#111111", "read", "write", new ArrayList<>());

        assertThatThrownBy(() -> {
            boardService.createBoard(emptyBoard);
        }).isInstanceOf(Exception.class);

    }

    @Test
    public void editBoardTitleTest() throws Exception {

        Board b = new Board("My Schedule", "#111111", "read", "write", new ArrayList<>());

        boardService.createBoard(b);

        when(boardRepo.findById(b.id)).thenReturn(Optional.of(b));

        boardService.editBoardTitle(b.id,"My New Schedule");

        assertThat(b.title).isEqualTo("My New Schedule");

        assertThatThrownBy(() -> {
            boardService.editBoardTitle(b.id,null);
        }).isInstanceOf(Exception.class);

        assertThatThrownBy(() -> {
            boardService.editBoardTitle(b.id,"");
        }).isInstanceOf(Exception.class);


    }

    @Test
    public void deleteBoardTest() throws Exception {

        Board b = new Board("My Schedule", "#111111", "read", "write", new ArrayList<>());

        boardService.createBoard(b);
        boardService.deleteBoardById(b.id);

        Mockito.verify(boardRepo).deleteById(b.id);



    }


}
