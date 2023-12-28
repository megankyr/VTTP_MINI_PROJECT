package com.ssf.mini.project.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ssf.mini.project.model.Comment;
import com.ssf.mini.project.model.Event;
import com.ssf.mini.project.model.User;
import com.ssf.mini.project.repo.EventRepo;

import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {

    @Autowired
    EventRepo eventRepo;

     @GetMapping("/")
    public String getIndex() {
        return "index";
    }

    @GetMapping("/login")
    public String getLogin(@RequestParam String name, Model model, HttpSession session) {
        session.setAttribute("name", name);
        if (eventRepo.isHost(name) || eventRepo.isMember(name)) {
            Event event = eventRepo.getEvent(name);
            List<User> eventMembers = eventRepo.getEventMembers(name);
            model.addAttribute("event", event);
            model.addAttribute("eventMembers", eventMembers);
            if (eventRepo.getComments(name) != null) {
                List<Comment> comments = eventRepo.getComments(name);
                model.addAttribute("comments", comments);
            }

            return "existingevent";
        } else {
            Event event = new Event();
            event.setEventHost(name);
            session.setAttribute("eventHost", name);

            return "newevent";
        }
    }

    
}
