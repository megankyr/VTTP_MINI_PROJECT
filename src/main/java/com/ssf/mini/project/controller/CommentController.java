package com.ssf.mini.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import com.ssf.mini.project.model.Comment;
import com.ssf.mini.project.repo.EventRepo;

import jakarta.servlet.http.HttpSession;

@Controller
public class CommentController {

    @Autowired
    EventRepo eventRepo;

    @GetMapping("/comment")
    public String showCommentForm(Model model) {
        model.addAttribute("comment", new Comment());
        return "comment";
    }

    @PostMapping("/comment")
    public ModelAndView processComment(@ModelAttribute Comment comment,
            HttpSession session) {
        ModelAndView mav = new ModelAndView("commentadded");
        String author = (String) session.getAttribute("name");
        comment.setAuthor(author);
        eventRepo.saveComment(comment);
        mav.addObject("name", author);
        return mav;
    }
}