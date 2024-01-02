package com.ssf.mini.project.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class Event {

    @NotEmpty(message = "Film club name must be provided")
    private String eventName;

    @NotEmpty(message = "Place must be provided")
    private String eventPlace;

    @NotNull(message = "Date must be provided")
    @Future(message = "Date must be in the future")
    private LocalDate eventDate;

    @NotNull(message = "Time must be provided")
    private LocalTime eventTime;

    public Event() {
    }

    public Event(@NotNull(message = "Name must be provided") String eventName,
            @NotNull(message = "Place must be provided") String eventPlace,
            @NotNull(message = "Date must be provided") @Future(message = "Date must be in the future") LocalDate eventDate,
            @NotNull(message = "Time must be provided") LocalTime eventTime) {
        this.eventName = eventName;
        this.eventPlace = eventPlace;
        this.eventDate = eventDate;
        this.eventTime = eventTime;
    }

    private String eventHost;

    private String eventMovie;

    private List<User> eventMembers;

    public Event(@NotNull(message = "Name must be provided") String eventName,
            @NotNull(message = "Place must be provided") String eventPlace,
            @NotNull(message = "Date must be provided") @Future(message = "Date must be in the future") LocalDate eventDate,
            @NotNull(message = "Time must be provided") LocalTime eventTime, String eventHost, String eventMovie,
            List<User> eventMembers) {
        this.eventName = eventName;
        this.eventPlace = eventPlace;
        this.eventDate = eventDate;
        this.eventTime = eventTime;
        this.eventHost = eventHost;
        this.eventMovie = eventMovie;
        this.eventMembers = eventMembers;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventPlace() {
        return eventPlace;
    }

    public void setEventPlace(String eventPlace) {
        this.eventPlace = eventPlace;
    }

    public LocalDate getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDate eventDate) {
        this.eventDate = eventDate;
    }

    public LocalTime getEventTime() {
        return eventTime;
    }

    public void setEventTime(LocalTime eventTime) {
        this.eventTime = eventTime;
    }

    public String getEventHost() {
        return eventHost;
    }

    public void setEventHost(String eventHost) {
        this.eventHost = eventHost;
    }

    public String getEventMovie() {
        return eventMovie;
    }

    public void setEventMovie(String eventMovie) {
        this.eventMovie = eventMovie;
    }

    public List<User> getEventMembers() {
        return eventMembers;
    }

    public void setEventMembers(List<User> eventMembers) {
        this.eventMembers = eventMembers;
    }
}