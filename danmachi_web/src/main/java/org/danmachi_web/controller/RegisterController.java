package org.danmachi_web.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.apache.juli.logging.Log;
import org.danmachi_web.model.LoginForm;
import org.danmachi_web.model.User;
import org.danmachi_web.service.EmailService;
import org.danmachi_web.service.RegisterService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@RestController
public class RegisterController {

    private final RegisterService registerService;
    private final EmailService emailService;

    public RegisterController(RegisterService registerService, EmailService emailService) {
        this.registerService = registerService;
        this.emailService = emailService;
    }

    @GetMapping("/login")
    public ModelAndView showLogin(Model model) {
        model.addAttribute("loginForm", new LoginForm());
        return new ModelAndView("login");
    }

    @PostMapping("/login-user")
    public ModelAndView login(@Valid LoginForm loginForm, HttpSession session) {
        ModelAndView modelAndView = new ModelAndView();

        User user = registerService.login(loginForm.getEmail(), loginForm.getPassword());

        if (user != null) {
            session.setAttribute("user", user);
            System.out.println("User object set in session: " + user);
            modelAndView.setViewName("redirect:/success"); // Redirect to success page
        } else {
            modelAndView.setViewName("login"); // Return login page with error message
            modelAndView.addObject("errorMessage", "Email ou mot de passe invalide");
        }

        return modelAndView;
    }

    @GetMapping("/signup")
    public ModelAndView showSignUp(Model model) {
        model.addAttribute("user", new User());
        return new ModelAndView("signup");
    }

    @PostMapping("/user-signup")
    public String signupUser(@Valid User user) {
        registerService.signup(user);

        String confirmationUrl = "http://localhost:8080/confirm?id=" + user.getId();
        String emailContent = "Please click the following link to confirm your email address: " + confirmationUrl;

        emailService.sendEmail(user.getEmail(), emailContent, "Confirmer adresse mail");

        return "redirect:/login";
    }

    @PostMapping("/checkUnique")
    public Map<String, Boolean> checkUnique(@RequestBody Map<String, String> data) {
        String username = data.get("username");
        String email = data.get("email");

        return registerService.checkUnique(username, email);
    }

    @GetMapping("/confirm")
    public ModelAndView confirmEmail(@RequestParam("id") long id) {
        if (registerService.confirmEmail(id)) {
            return new ModelAndView("login");
        } else {
            return new ModelAndView("error"); //set up error
        }
    }
}
