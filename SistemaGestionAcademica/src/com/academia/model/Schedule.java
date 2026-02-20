package com.academia.model;

import java.io.Serializable;

/**
 * Represents the schedule assigned to an academic group.
 */
public class Schedule implements Serializable, Cloneable {

    public enum WeekDay {
        MONDAY   ("Lunes"),
        TUESDAY  ("Martes"),
        WEDNESDAY("Miércoles"),
        THURSDAY ("Jueves"),
        FRIDAY   ("Viernes"),
        SATURDAY ("Sábado");

        private final String label;

        WeekDay(String label) {
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    private WeekDay day;
    private String  startTime; // format "HH:mm"
    private String  endTime;   // format "HH:mm"
    private String  classroom;

    public Schedule(WeekDay day, String startTime, String endTime, String classroom) {
        validateTimeFormat(startTime, "Hora de inicio");
        validateTimeFormat(endTime, "Hora de fin");
        this.day       = day;
        this.startTime = startTime;
        this.endTime   = endTime;
        this.classroom = classroom;
    }

    private void validateTimeFormat(String time, String fieldName) {
        if (time == null || !time.matches("^([01]?[0-9]|2[0-3]):[0-5][0-9]$")) {
            throw new IllegalArgumentException(fieldName + " debe tener formato HH:mm (ej. 07:00 o 14:30)");
        }
    }

    @Override
    public Schedule clone() {
        try {
            return (Schedule) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Error cloning Schedule", e);
        }
    }

    // ── Getters & Setters ────────────────────────────────────────────────────
    public WeekDay getDay()               { return day; }
    public void    setDay(WeekDay day)    { this.day = day; }

    public String getStartTime()                  { return startTime; }
    public void   setStartTime(String startTime)  { this.startTime = startTime; }

    public String getEndTime()                { return endTime; }
    public void   setEndTime(String endTime)  { this.endTime = endTime; }

    public String getClassroom()                  { return classroom; }
    public void   setClassroom(String classroom)  { this.classroom = classroom; }

    @Override
    public String toString() {
        return day + " " + startTime + " - " + endTime + " | Aula: " + classroom;
    }
}