package org.example.controllers.securiItyControllers;


import org.example.model.AppUser;
import org.example.services.EmailService;
import org.example.services.UserService;
import org.example.spring.security.SecurityService;
import org.example.spring.security.pojo.JwtResponse;
import org.example.spring.security.pojo.LoginRequest;
import org.example.spring.security.pojo.MessageResponse;
import org.example.spring.security.pojo.SingUpRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    @Autowired
    private SecurityService securityService;
    @Autowired
    private UserService userService;
    @Autowired
    private EmailService emailService;


    @PostMapping("/singin")
    public ResponseEntity <?> authUser (@RequestBody LoginRequest loginRequest) {
        JwtResponse jwtResponse = securityService.authentication(loginRequest);
        return ResponseEntity.ok(jwtResponse);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SingUpRequest singUpRequest){
        securityService.registration(singUpRequest);
        return ResponseEntity.ok(new MessageResponse("User is created"));
    }

    @GetMapping("singin/forgotPassword")
    public ResponseEntity<?> sendEmailWithNewPassword(@RequestParam String email){
        AppUser appUser = userService.getOneUserByEmail(email);
        String newPassword = userService.generateRandomPassword();
        userService.changePasswordOfUser(appUser.getId(), newPassword);
        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("userName", appUser.getUserName());
        templateModel.put("password", newPassword);
        emailService.sendMessage(email, "Password recovering", templateModel);
        return ResponseEntity.ok(new MessageResponse("Email is sent"));
    }
}
