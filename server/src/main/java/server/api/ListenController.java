package server.api;

import commons.Board;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import server.services.BoardService;

import java.util.HashMap;

import java.util.Map;


@RestController
@RequestMapping("/api/listen")
public class ListenController {


    public long id;

    Map<Long,DeferredResult> requestMap = new HashMap();

    BoardService boardService;

        /**
         * Constructor with parameters
         */
        @Autowired
   public ListenController(
                           BoardService boardService) {
            this.boardService = boardService;
         }

    /**
     *
     * Connect - adding a new unique identifier
     * @return the id
     */

    @GetMapping(path = {"/connect"})
    public Long connect(){
        return id++;
    }
    /**
     *
     * Listen - getting the connection result message
     * @return a new board
     */
    @GetMapping(path = {"/listen"})
    public Board listen (
            @RequestParam("id") @Nullable Long id,
            @RequestParam("board") Board board)
            throws Exception {

       if (requestMap.containsKey(id)) {
             requestMap.get(id).setErrorResult(null);
        }
        DeferredResult<Board> msg = new DeferredResult();
       if(id == null) {
            msg.setResult(board);
            return (Board) msg.getResult();
        }
       else {
            requestMap.put(id, msg);
            return (Board) msg.getResult();
        }

    }

    /**
     *
     * @param id
     * @param message
     * @return send Message - returns the state of a board
     */
    @GetMapping(path = {"/sendmessage"})
    public boolean sendMessage(Long id, String message) {
       return requestMap.get(id).setResult(message);
    }



}
