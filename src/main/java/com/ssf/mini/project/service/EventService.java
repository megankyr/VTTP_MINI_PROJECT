package com.ssf.mini.project.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ssf.mini.project.model.User;

@Service
public class EventService {

    public boolean isGuest(List<User> eventMembers, String guestName) {
        for (User eventMember : eventMembers) {
            String memberName = eventMember.getName().toLowerCase();
            String addedName = guestName.toLowerCase();
            if (memberName.equals(addedName)) {
                return true;
            }
        }

        return false;
    }
}