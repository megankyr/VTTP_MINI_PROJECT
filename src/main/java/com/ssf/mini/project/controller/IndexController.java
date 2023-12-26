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
    // (previous) select html's action is /eventdetails - loads the page
    @GetMapping("/eventdetails")
    public String showEventForm(Model model, HttpSession session) {
        model.addAttribute("event", new Event());
        model.addAttribute(session.getAttribute("eventHost"));
        model.addAttribute(session.getAttribute("selectedMovie"));
        return "eventdetails";
    }

    // (previous) eventdetails's action is /register
    // processes the event form
    // guests' action is /processguests
    @PostMapping("/register")
    public String processForm(@Valid @ModelAttribute Event event, BindingResult binding,
            HttpSession session) {
        if (binding.hasErrors()) {
            return "eventdetails";
        }
        session.setAttribute("event", event);
        return "redirect:/guests";
    }

    @GetMapping("/guests")
    public String showGuestForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("eventMembers", new ArrayList<User>()); // Add this line
        return "guests";
    }

    @PostMapping("/guests")
    public ModelAndView processGuests(@Valid @ModelAttribute User user, BindingResult binding,
            HttpSession session) {
        ModelAndView mav = new ModelAndView("guests");


        List<User> eventMembers = (List<User>) session.getAttribute("eventMembers");
        if (eventMembers == null) {
            eventMembers = new ArrayList<>();
        }

        if (binding.hasErrors()) {
            mav.addObject("user", user);
            return mav;
        }

        eventMembers.add(user);
        session.setAttribute("eventMembers", eventMembers);
        mav.addObject("user", new User());
        mav.addObject("eventMembers", eventMembers);

        return mav;
    }

    // done up till here
    @PostMapping("/postguests")
    public String postGuests() {
        return "redirect:/final";
    }

    @GetMapping("/final")
    public ModelAndView showFinal(HttpSession session) {

        Event event = (Event)session.getAttribute("event");
        List<User> eventMembers = (List<User>) session.getAttribute("eventMembers");
        ModelAndView mav = new ModelAndView("final");
        mav.addObject("event", event);
        mav.addObject("eventMembers", eventMembers);
        return mav;

    }

    @GetMapping("/done")
    public String showDonePage(){
        return "done";
    }
}