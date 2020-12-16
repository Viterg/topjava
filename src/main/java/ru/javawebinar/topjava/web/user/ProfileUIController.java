package ru.javawebinar.topjava.web.user;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import ru.javawebinar.topjava.to.UserTo;
import ru.javawebinar.topjava.web.SecurityUtil;

import javax.validation.Valid;

@Controller
@RequestMapping("/profile")
public class ProfileUIController extends AbstractUserController {

    @GetMapping
    public String profile(Model model) {
        return "profile";
    }

    @GetMapping("/register")
    public String register(ModelMap model) {
        model.addAttribute("userTo", new UserTo());
        model.addAttribute("register", true);
        return "profile";
    }

    @PostMapping("/register")
    public String saveRegister(@Valid @ModelAttribute("userTo") UserTo userTo, SessionStatus status, ModelMap model) {
        create(userTo);
        status.setComplete();
        return "redirect:/login?message=app.registered&username=" + userTo.getEmail();
    }

    @PostMapping
    public String updateProfile(@Valid @ModelAttribute("userTo") UserTo userTo, SessionStatus status, ModelMap model) {
        update(userTo, SecurityUtil.authUserId());
        SecurityUtil.get().update(userTo);
        status.setComplete();
        return "redirect:/meals";
    }
}