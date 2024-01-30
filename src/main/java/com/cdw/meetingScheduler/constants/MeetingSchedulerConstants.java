package com.cdw.meetingScheduler.constants;

public final class MeetingSchedulerConstants {
    // Names
    public static final String NAME_EXTENSION_COLLABORATION_TEAM = " - Collaboration team";

    // Entity Not found
    public static final String EMPLOYEE_NOT_FOUND = "Employee not found !";
    public static final String TEAM_NOT_FOUND = "Team not found !";
    public static final String MEETING_NOT_FOUND = "Meeting not found !";
    public static final String ROOM_NOT_FOUND = "Room not found !";
    public static final String ORGANISER_NOT_FOUND = "Meeting organiser not found !";

    // Entity Deleted
    public static final String EMPLOYEE_DELETED = "Employee deleted !";
    public static final String TEAM_DELETED = "Team deleted !";
    public static final String MEETING_DELETED = "Meeting deleted !";
    public static final String ROOM_DELETED = "Room deleted !";

    // Employee
    public static final String EMPLOYEE_ALREADY_IN_MEETING = "Employee already in meeting !";
    public static final String EMPLOYEE_ALREADY_NOT_IN_MEETING = "Employee already not in meeting !";
    public static final String EMPLOYEE_BUSY = "Employee busy in some other meeting !";

    public static final String EMPLOYEE_ALREADY_IN_TEAM = "Employee already in team !";

    // Team
    public static final String COLLABORATION_TEAM_NOT_ALLOWED  = "Collaboration team can't be added to Team meeting !";
    public static final String TEAM_STRENGTH_HIGHER_THAN_ANY_ROOM_CAPACITY = "Team strength exceeds any room's capacity !";

    // Room
    public static final String ROOM_BUSY = "Room already busy for this schedule. Choose some other room !";
    public static final String ROOM_CAPACITY_LESSER = "Room capacity smaller than team strength. Choose some other room !";
    public static final String CHOOSE_ROOM = "Choose one among the following rooms' IDs : ";

    // Meeting
    public static final String UNADDED_UNAVAILABLE_COLLABORATORS = "Meeting created !\nUnavailable collaborators' IDs are : ";
    public static final String COLLABORATORS_NOT_FOUND = "1 or more collaborators not found !";
    public static final String ADD_COLLABORATORS = "Please add 1 or more collaborators to create collaboration meeting !";
    public static final String CANCEL_NOTICE_TIME_SHORTER = "Meeting can't be cancelled if it's scheduled in less than 30 minutes from now!";


    // Date validation
    public static final String PAST_START_DATETIME = "Meeting can be scheduled only for a future dateTime !";
    public static final String PAST_END_DATETIME = "Meeting end time is in past !";
    public static final String NEGATIVE_DURATION = "Meeting duration should be greater than a second !";
    public static final String TRUE = "true";
    public static final String END_DATETIME_REQUIRED =  "EndDateTime should be mentioned !";

    private MeetingSchedulerConstants(){
        throw new AssertionError("Don't try to instantiate App Constants !");
    }
}
