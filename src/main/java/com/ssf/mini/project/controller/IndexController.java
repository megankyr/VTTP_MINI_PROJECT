package com.ssf.mini.project.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.ssf.mini.project.model.Comment;
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
            List<User> eventMembers = eventRepo.getEventMembers(name);
            model.addAttribute("event", event);
            System.out.println("Event Name: " + event.getEventName());
            System.out.println("Event Place: " + event.getEventPlace());
            System.out.println("Event Date: " + event.getEventDate());
            System.out.println("Event Time: " + event.getEventTime());
            System.out.println("Event Host: " + event.getEventHost());
            System.out.println("Event Movie: " + event.getEventMovie());
            System.out.println("Event Movie: " + event.getEventMembers());
            model.addAttribute("eventMembers", eventMembers);
            for (User member : eventMembers) {
                System.out.println(member);
            }
            if (eventRepo.getComments(name) != null) {
                List<Comment> comments = eventRepo.getComments(name);
                model.addAttribute("comments", comments);
                for (Comment comment : comments) {
                    System.out.println("Comment: " + comment + " By: " + name);
                }
            }

            return "event";
        } else {
            Event event = new Event();
            event.setEventHost(name);
            session.setAttribute("eventHost", name);
            System.out.println(session.getAttribute("eventHost"));

            return "create";
        }
    }

    @GetMapping("/comment")
    public String loadCommentForm(Model model) {
        model.addAttribute("comment", new Comment());
        return "comment";
    }

    @PostMapping("/comment")
    public ModelAndView processComment(@ModelAttribute Comment comment,
            HttpSession session) {
        ModelAndView mav = new ModelAndView("added");
        String author = (String) session.getAttribute("name");
        comment.setAuthor(author);
        mav.addObject("author", author);
        mav.addObject("comment", comment);
        eventRepo.saveComment(comment);
        return mav;
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

    @PostMapping("/postguests")
    public String postGuests() {
        return "redirect:/final";
    }

    @GetMapping("/final")
    public ModelAndView showFinal(HttpSession session) {

        Event event = (Event) session.getAttribute("event");
        List<User> eventMembers = (List<User>) session.getAttribute("eventMembers");
        ModelAndView mav = new ModelAndView("final");
        mav.addObject("event", event);
        mav.addObject("eventMembers", eventMembers);
        System.out.println("Event Name: " + event.getEventName());
        System.out.println("Event Place: " + event.getEventPlace());
        System.out.println("Event Date: " + event.getEventDate());
        System.out.println("Event Time: " + event.getEventTime());
        System.out.println("Event Host: " + event.getEventHost());
        System.out.println("Event Movie: " + event.getEventMovie());
        return mav;

    }

    @PostMapping("/final")
    public ModelAndView save(HttpSession session) {

        ModelAndView mav = new ModelAndView("save");
        Event event = (Event) session.getAttribute("event");
        List<User> eventMembers = (List<User>) session.getAttribute("eventMembers");
        event.setEventMembers(eventMembers);
        System.out.println("Event Name: " + event.getEventName());
        System.out.println("Event Place: " + event.getEventPlace());
        System.out.println("Event Date: " + event.getEventDate());
        System.out.println("Event Time: " + event.getEventTime());
        System.out.println("Event Host: " + event.getEventHost());
        System.out.println("Event Movie: " + event.getEventMovie());

        if (event.getEventMembers() == null || event.getEventMembers().isEmpty()) {
            return new ModelAndView("usererror");
        }

        if (event.getEventName() == null || event.getEventName().isEmpty() ||
                event.getEventPlace() == null || event.getEventPlace().isEmpty() ||
                event.getEventDate() == null ||
                event.getEventTime() == null ||
                event.getEventHost() == null || event.getEventHost().isEmpty() ||
                event.getEventMovie() == null || event.getEventMovie().isEmpty()) {
            return new ModelAndView("errordetails");
        }

        eventRepo.saveRecord(event);
        session.invalidate();
        return mav;

    }

}
