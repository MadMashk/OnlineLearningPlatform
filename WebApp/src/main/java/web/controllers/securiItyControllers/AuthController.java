package web.controllers.securiItyControllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spring.security.SecurityService;
import spring.security.pojo.JwtResponse;
import spring.security.pojo.LoginRequest;
import spring.security.pojo.MessageResponse;
import spring.security.pojo.SingUpRequest;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    @Autowired
    private SecurityService securityService;

    @PostMapping("/singin")
    public ResponseEntity <?> authUser (@RequestBody LoginRequest loginRequest) {
        JwtResponse jwtResponse = securityService.authentication(loginRequest);
        return ResponseEntity.ok(jwtResponse);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SingUpRequest singUpRequest){
        securityService.registration(singUpRequest);
        return ResponseEntity.ok(new MessageResponse("User created"));
    }
}
