package com.ssf.mini.project.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.ssf.mini.project.model.Event;
import com.ssf.mini.project.model.User;
import com.ssf.mini.project.repo.EventRepo;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping
public class IndexController {

    @Autowired
    EventRepo eventRepo;

    // load login page
    @GetMapping("/")
    public String getIndex() {
        return "index";
    }

    // check if name entered is a host or member, if it is, load the existing event
    // page
    // else create new event page
    @GetMapping("/login")
    public String login(@RequestParam String name, Model model, HttpSession session) {
        session.setAttribute("name", name);
        if (eventRepo.isHost(name) || eventRepo.isMember(name)) {
            Event event = eventRepo.getEvent(name);
            model.addAttribute("event", event);
            return "event";
        } else {
            Event event = new Event();
            event.setEventHost(name);
            session.setAttribute("eventHost", name);
            System.out.println(session.getAttribute("eventHost"));

            return "create";
        }
    }

    // load search page with the options title, genre and country
    @GetMapping("/search")
    public String displaySearchOptions() {
        return "search";
    }

    // search movie by title
    @GetMapping("/search/title")
    public ModelAndView searchTitle() {
        ModelAndView mav = new ModelAndView("title");
        return mav;
    }

    // load event registration page with the fields event name, place, date
    // time, movie, host (preloaded from session attributes) and submit event
    // details button
    @GetMapping("/eventdetails")
    public String showEventForm(Model model, HttpSession session) {
        model.addAttribute("event", new Event());
        model.addAttribute(session.getAttribute("eventHost"));
        model.addAttribute(session.getAttribute("selectedMovie"));
        return "eventdetails";
    }

    @PostMapping("/register")
    public String processForm(@Valid @ModelAttribute Event event, BindingResult binding,
            HttpSession session) {
        if (binding.hasErrors()) {
            return "eventdetails";
        }
        session.setAttribute("event", event);
        return "guests";
    }

    @GetMapping("/guests")
    public String showGuestForm(Model model) {
        model.addAttribute("user", new User());
        return "guests";
    }

    @PostMapping("/processguests")
    public String processGuests(@Valid @ModelAttribute User user, BindingResult binding,
            HttpSession session) {

        if (binding.hasErrors()) {
            return "guests";
        }
        List<User> eventMembers = (List<User>) session.getAttribute("eventMembers");
        if (eventMembers == null) {
            eventMembers = new ArrayList<>();
        }
        eventMembers.add(user);
        session.setAttribute("eventMembers", eventMembers);
        return "done";
    }
}