package server.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/checkServer")
public class ApiController {


    /**
     * Constructor with parameters
     *
     */
    @Autowired
    public ApiController() {

    }


    /**
     * Check if the server has been connected to
     *
     * @return
     */
    @GetMapping(path = {"/"})
    private ResponseEntity<String> checkServer() {
        try {
            return ResponseEntity.status(HttpStatus.OK).body("This is a TimeWise Server");
        }
        catch (Exception e) {

            return ResponseEntity.badRequest().build();
        }
    }

}
