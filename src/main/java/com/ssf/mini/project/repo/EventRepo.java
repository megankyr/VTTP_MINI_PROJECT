package com.ssf.mini.project.repo;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import com.ssf.mini.project.model.Comment;
import com.ssf.mini.project.model.Event;
import com.ssf.mini.project.model.User;

@Repository
public class EventRepo {

    @Autowired
    @Qualifier("redis")
    private RedisTemplate<String, String> template;

    private static final String EVENT_HASH_KEY = "film club";
    private static final String MEMBER_SET_KEY = "members";
    private static final String COMMENT_HASH_KEY = "comments";

    public void saveEvent(Event event) {

        String key = EVENT_HASH_KEY + ":" + event.getEventName();
        template.opsForHash().put(key, "eventName", event.getEventName());
        template.opsForHash().put(key, "eventPlace", event.getEventPlace());
        template.opsForHash().put(key, "eventDate", event.getEventDate().toString());
        template.opsForHash().put(key, "eventTime", event.getEventTime().toString());
        template.opsForHash().put(key, "eventHost", event.getEventHost());
        template.opsForHash().put(key, "eventMovie", event.getEventMovie());

        LocalDate eventDate = event.getEventDate();
        long expiry = calculateExpiry(eventDate);
        template.expire(key, expiry, TimeUnit.SECONDS);

        String hostName = event.getEventHost();
        String eventName = event.getEventName();

        template.opsForValue().set(hostName, eventName);
        template.expire(hostName, expiry, TimeUnit.SECONDS);

        List<User> members = event.getEventMembers();
        for (User member : members) {
            String memberName = member.getName();

            template.opsForValue().set(memberName, eventName);
            template.expire(memberName, expiry, TimeUnit.SECONDS);

            String memberKey = MEMBER_SET_KEY + ":" + eventName;

            template.opsForSet().add(memberKey, memberName);
            template.expire(memberKey, expiry, TimeUnit.SECONDS);

        }

    }

    public boolean isHost(String name) {
        return template.hasKey(name);
    }

    public boolean isMember(String name) {
        return template.hasKey(name);
    }

    public boolean eventNameExists(String eventName) {
        String key = EVENT_HASH_KEY + ":" + eventName;
        return template.hasKey(key);
    }

    public Event getEvent(String name) {
        String eventName = template.opsForValue().get(name);
        String key = EVENT_HASH_KEY + ":" + eventName;
        Map<Object, Object> eventData = template.opsForHash().entries(key);
        Event event = new Event();
        event.setEventName(eventData.get("eventName").toString());
        event.setEventPlace(eventData.get("eventPlace").toString());
        event.setEventDate(LocalDate.parse(eventData.get("eventDate").toString()));
        event.setEventTime(LocalTime.parse(eventData.get("eventTime").toString()));
        event.setEventMovie(eventData.get("eventMovie").toString());
        event.setEventHost(eventData.get("eventHost").toString());

        String memberKey = MEMBER_SET_KEY + ":" + eventName;
        Set<String> memberSet = template.opsForSet().members(memberKey);
        List<User> members = new ArrayList<>();
        for (String m : memberSet) {
            User member = new User(m);
            members.add(member);
        }

        event.setEventMembers(members);

        return event;
    }

    public List<User> getEventMembers(String name) {
        String eventName = template.opsForValue().get(name);
        String memberKey = MEMBER_SET_KEY + ":" + eventName;
        Set<String> memberSet = template.opsForSet().members(memberKey);
        List<User> members = new ArrayList<>();
        for (String m : memberSet) {
            User member = new User(m);
            members.add(member);
        }

        return members;

    }

    public void saveComment(Comment comment) {
        String eventName = template.opsForValue().get(comment.getAuthor());
        String key = COMMENT_HASH_KEY + ":" + eventName;

        template.opsForHash().put(key, comment.getAuthor(), comment.getComment());
        String eventKey = EVENT_HASH_KEY + ":" + eventName;
        String eventDateString = (String) template.opsForHash().get(eventKey, "eventDate");
        LocalDate eventDate = LocalDate.parse(eventDateString);
        long expiry = calculateExpiry(eventDate);
        template.expire(key, expiry, TimeUnit.SECONDS);

    }

    public List<Comment> getComments(String name) {
        List<Comment> comments = new ArrayList<>();
        String eventName = template.opsForValue().get(name);
        String key = COMMENT_HASH_KEY + ":" + eventName;
        if (template.hasKey(name)) {
            Map<Object, Object> entries = template.opsForHash().entries(key);
            for (Map.Entry<Object, Object> entry : entries.entrySet()) {
                String author = (String) entry.getKey();
                String comment = (String) entry.getValue();
                comments.add(new Comment(comment, author));
            }
        }
        return comments;

    }

    public long calculateExpiry(LocalDate eventDate) {
        LocalDate now = LocalDate.now();
        LocalDateTime nowStartOfDay = now.atStartOfDay();
        LocalDateTime eventEndOfDay = eventDate.atTime(LocalTime.MAX);

        Duration duration = Duration.between(nowStartOfDay, eventEndOfDay);

        long expiry = duration.getSeconds();

        return expiry;
    }

}
