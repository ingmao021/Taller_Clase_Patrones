package com.academia.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an academic study plan.
 *
 * Implements the PROTOTYPE pattern via the {@link Cloneable} interface,
 * allowing existing plans to be deep-cloned as the starting point for
 * a new academic period without rebuilding them from scratch.
 */
public class StudyPlan implements Serializable, Cloneable {

    private String      id;
    private String      name;
    private String      period;       // e.g. "2024-I", "2024-II"
    private String      program;      // e.g. "Ingeniería de Sistemas"
    private Subject.Modality modality;
    private LocalDate   startDate;
    private LocalDate   endDate;
    private String      description;
    private List<Group> groups;

    /**
     * Public constructor — accessible through the Builder or clone().
     */
    public StudyPlan(String id, String name, String period, String program,
              Subject.Modality modality, LocalDate startDate,
              LocalDate endDate, String description, List<Group> groups) {
        this.id          = id;
        this.name        = name;
        this.period      = period;
        this.program     = program;
        this.modality    = modality;
        this.startDate   = startDate;
        this.endDate     = endDate;
        this.description = description;
        this.groups      = new ArrayList<>(groups);
    }

    // ── PROTOTYPE ────────────────────────────────────────────────────────────

    /**
     * Creates a deep copy of this study plan to be used as the base for a new period.
     * All nested {@link Group} objects are also cloned individually.
     * The returned copy has a {@code null} ID — it must be assigned before persisting.
     *
     * @return a new {@link StudyPlan} with independent copies of all groups.
     */
    @Override
    public StudyPlan clone() {
        try {
            StudyPlan copy = (StudyPlan) super.clone();
            // Deep copy of the groups list
            copy.groups = new ArrayList<>();
            for (Group g : this.groups) {
                copy.groups.add(g.clone());
            }
            copy.id = null; // the clone requires a new ID
            return copy;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Error cloning StudyPlan", e);
        }
    }

    // ── Business methods ─────────────────────────────────────────────────────

    public void addGroup(Group group) {
        groups.add(group);
    }

    public void removeGroup(Group group) {
        groups.remove(group);
    }

    /** Returns the sum of credits of all groups in this plan. */
    public int getTotalCredits() {
        return groups.stream()
                .mapToInt(g -> g.getSubject().getCredits())
                .sum();
    }

    // ── Getters & Setters ────────────────────────────────────────────────────
    public String getId()               { return id; }
    public void   setId(String id)      { this.id = id; }

    public String getName()             { return name; }
    public void   setName(String name)  { this.name = name; }

    public String getPeriod()               { return period; }
    public void   setPeriod(String period)  { this.period = period; }

    public String getProgram()              { return program; }
    public void   setProgram(String prog)   { this.program = prog; }

    public Subject.Modality getModality()               { return modality; }
    public void             setModality(Subject.Modality m) { this.modality = m; }

    public LocalDate getStartDate()                 { return startDate; }
    public void      setStartDate(LocalDate date)   { this.startDate = date; }

    public LocalDate getEndDate()               { return endDate; }
    public void      setEndDate(LocalDate date) { this.endDate = date; }

    public String getDescription()                      { return description; }
    public void   setDescription(String description)    { this.description = description; }

    public List<Group> getGroups()              { return new ArrayList<>(groups); }
    public void        setGroups(List<Group> g) { this.groups = new ArrayList<>(g); }

    @Override
    public String toString() {
        return name + " | " + period + " | " + program;
    }
}