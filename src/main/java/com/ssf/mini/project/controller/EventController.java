package com.ssf.mini.project.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
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
public class EventController {

    @Autowired
    EventRepo eventRepo;

    @GetMapping("/eventdetails")
    public String getEventDetails(Model model, HttpSession session) {
        model.addAttribute("event", new Event());
        model.addAttribute(session.getAttribute("eventHost"));
        model.addAttribute(session.getAttribute("selectedMovie"));
        return "eventdetails";
    }

    @PostMapping("/eventdetails")
    public String processEventDetails(@Valid @ModelAttribute Event event, BindingResult binding,
            HttpSession session) {
        if (binding.hasErrors()) {
            return "eventdetails";
        }

        String eventName = event.getEventName();
        if (eventRepo.eventNameExists(eventName)) {
            FieldError err = new FieldError("event", "eventName",
                    "Event name already exists, please pick another name");
            binding.addError(err);
            return "eventdetails";
        }

        session.setAttribute("event", event);
        return "redirect:/guests";
    }

    @GetMapping("/guests")
    public String showGuestForm(Model model) {
        model.addAttribute("user", new User());
        List<User> eventMembers = new ArrayList<>();
        model.addAttribute("eventMembers", eventMembers);
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

        String guestName = user.getName();
        if (eventRepo.isHost(guestName) || eventRepo.isMember(guestName)) {
            mav.addObject("error", "Guest is not free, please pick another guest");
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
    public ModelAndView showFinalDetails(HttpSession session) {

        Event event = (Event) session.getAttribute("event");
        List<User> eventMembers = (List<User>) session.getAttribute("eventMembers");
        ModelAndView mav = new ModelAndView("final");
        mav.addObject("event", event);
        mav.addObject("eventMembers", eventMembers);
        return mav;

    }

    @PostMapping("/final")
    public ModelAndView saveFinalDetails(HttpSession session) {

        ModelAndView mav = new ModelAndView("save");
        Event event = (Event) session.getAttribute("event");
        List<User> eventMembers = (List<User>) session.getAttribute("eventMembers");
        event.setEventMembers(eventMembers);

        if (event.getEventMembers() == null || event.getEventMembers().isEmpty()) {
            return new ModelAndView("guesterror");
        }

        if (event.getEventName() == null || event.getEventName().isEmpty() ||
                event.getEventPlace() == null || event.getEventPlace().isEmpty() ||
                event.getEventDate() == null ||
                event.getEventTime() == null ||
                event.getEventHost() == null || event.getEventHost().isEmpty() ||
                event.getEventMovie() == null || event.getEventMovie().isEmpty()) {
            return new ModelAndView("detailserror");
        }

        eventRepo.saveEvent(event);
        session.invalidate();
        return mav;

    }

}
