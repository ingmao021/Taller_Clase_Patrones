package com.academia.model;

import java.io.Serializable;

/**
 * Represents an academic group: the combination of a subject, a teacher and a schedule.
 */
public class Group implements Serializable, Cloneable {

    private String   id;
    private String   name;
    private Subject  subject;
    private Teacher  teacher;
    private Schedule schedule;
    private int      maxSlots;
    private int      occupiedSlots;

    public Group(String id, String name, Subject subject, Teacher teacher,
                 Schedule schedule, int maxSlots) {
        this.id            = id;
        this.name          = name;
        this.subject       = subject;
        this.teacher       = teacher;
        this.schedule      = schedule;
        this.maxSlots      = maxSlots;
        this.occupiedSlots = 0;
    }

    /**
     * Performs a deep clone of this group, also cloning its nested objects.
     * The cloned group starts with zero occupied slots.
     */
    @Override
    public Group clone() {
        try {
            Group copy          = (Group) super.clone();
            copy.subject        = this.subject.clone();
            copy.teacher        = this.teacher.clone();
            copy.schedule       = this.schedule.clone();
            copy.occupiedSlots  = 0;
            return copy;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Error cloning Group", e);
        }
    }

    public int getAvailableSlots() {
        return maxSlots - occupiedSlots;
    }

    // ── Getters & Setters ────────────────────────────────────────────────────
    public String getId()               { return id; }
    public void   setId(String id)      { this.id = id; }

    public String getName()             { return name; }
    public void   setName(String name)  { this.name = name; }

    public Subject getSubject()                 { return subject; }
    public void    setSubject(Subject subject)  { this.subject = subject; }

    public Teacher getTeacher()                 { return teacher; }
    public void    setTeacher(Teacher teacher)  { this.teacher = teacher; }

    public Schedule getSchedule()                   { return schedule; }
    public void     setSchedule(Schedule schedule)  { this.schedule = schedule; }

    public int  getMaxSlots()               { return maxSlots; }
    public void setMaxSlots(int maxSlots)   { this.maxSlots = maxSlots; }

    public int  getOccupiedSlots()                  { return occupiedSlots; }
    public void setOccupiedSlots(int occupiedSlots) { this.occupiedSlots = occupiedSlots; }

    @Override
    public String toString() {
        return name + " | " + subject.getName() + " | " + teacher.getFullName();
    }
}