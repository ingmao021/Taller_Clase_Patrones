package com.academia.controller;

import com.academia.model.*;
import com.academia.model.Subject.Modality;
import com.academia.patterns.builder.StudyPlanBuilder;

import java.time.LocalDate;
import java.util.*;

/**
 * Central controller for the Academic Management System.
 *
 * Manages all in-memory data collections and coordinates the use of the
 * {@link StudyPlanBuilder} (Builder pattern) and {@link StudyPlan#clone()}
 * (Prototype pattern) to create study plans.
 *
 * Implemented as a Singleton to ensure a single data source during the
 * application's lifecycle.
 */
public class AcademyController {

    // ── Singleton (thread-safe) ─────────────────────────────────────────────────
    private static volatile AcademyController instance;

    public static AcademyController getInstance() {
        if (instance == null) {
            synchronized (AcademyController.class) {
                if (instance == null) {
                    instance = new AcademyController();
                }
            }
        }
        return instance;
    }

    // ── In-memory storage ─────────────────────────────────────────────────────
    private final Map<String, StudyPlan> studyPlans = new LinkedHashMap<>();
    private final Map<String, Subject>   subjects   = new LinkedHashMap<>();
    private final Map<String, Teacher>   teachers   = new LinkedHashMap<>();
    private final Map<String, Group>     groups     = new LinkedHashMap<>();

    private final StudyPlanBuilder builder = new StudyPlanBuilder();

    private int planCounter    = 1;
    private int subjectCounter = 1;
    private int teacherCounter = 1;
    private int groupCounter   = 1;

    private AcademyController() {
        loadSampleData();
    }

    // ── Subject operations ────────────────────────────────────────────────────

    public Subject createSubject(String name, int credits,
                                 String description, Modality modality) {
        String id  = "A" + String.format("%03d", subjectCounter++);
        Subject s  = new Subject(id, name, credits, description, modality);
        subjects.put(id, s);
        return s;
    }

    public void updateSubject(Subject subject) {
        subjects.put(subject.getId(), subject);
    }

    public void deleteSubject(String id) {
        // Verificar si hay grupos que usan esta materia
        for (Group g : groups.values()) {
            if (g.getSubject().getId().equals(id)) {
                throw new IllegalStateException("No se puede eliminar la materia: hay grupos asignados a ella.");
            }
        }
        subjects.remove(id);
    }

    public List<Subject> getSubjects() {
        return new ArrayList<>(subjects.values());
    }

    // ── Teacher operations ────────────────────────────────────────────────────

    public Teacher createTeacher(String firstName, String lastName,
                                 String speciality, String email, String phone) {
        String id  = "D" + String.format("%03d", teacherCounter++);
        Teacher t  = new Teacher(id, firstName, lastName, speciality, email, phone);
        teachers.put(id, t);
        return t;
    }

    public void updateTeacher(Teacher teacher) {
        teachers.put(teacher.getId(), teacher);
    }

    public void deleteTeacher(String id) {
        // Verificar si hay grupos que tienen este docente
        for (Group g : groups.values()) {
            if (g.getTeacher().getId().equals(id)) {
                throw new IllegalStateException("No se puede eliminar el docente: hay grupos asignados a él.");
            }
        }
        teachers.remove(id);
    }

    public List<Teacher> getTeachers() {
        return new ArrayList<>(teachers.values());
    }

    // ── Group operations ──────────────────────────────────────────────────────

    public Group createGroup(String name, Subject subject, Teacher teacher,
                             Schedule schedule, int maxSlots) {
        String id = "G" + String.format("%03d", groupCounter++);
        Group g   = new Group(id, name, subject, teacher, schedule, maxSlots);
        groups.put(id, g);
        return g;
    }

    public List<Group> getGroups() {
        return new ArrayList<>(groups.values());
    }

    // ── BUILDER pattern — create a StudyPlan from scratch ─────────────────────

    /**
     * Uses the {@link StudyPlanBuilder} to assemble and persist a new study plan.
     *
     * @return the newly created {@link StudyPlan}.
     */
    public StudyPlan createStudyPlan(String name, String period, String program,
                                     Modality modality, LocalDate startDate,
                                     LocalDate endDate, String description,
                                     List<Group> selectedGroups) {
        String id = "P" + String.format("%03d", planCounter++);

        builder.reset();
        builder.setId(id)
               .setName(name)
               .setPeriod(period)
               .setProgram(program)
               .setModality(modality)
               .setStartDate(startDate)
               .setEndDate(endDate)
               .setDescription(description);

        for (Group g : selectedGroups) {
            builder.addGroup(g);
        }

        StudyPlan plan = builder.build();
        studyPlans.put(plan.getId(), plan);
        return plan;
    }

    // ── PROTOTYPE pattern — clone an existing StudyPlan ───────────────────────

    /**
     * Clones an existing study plan using the Prototype pattern.
     * The clone receives a new ID, name, period and date range.
     *
     * @param sourceId   ID of the plan to clone.
     * @param newName    Display name for the cloned plan.
     * @param newPeriod  Academic period for the cloned plan.
     * @param newStart   Start date for the cloned plan.
     * @param newEnd     End date for the cloned plan.
     * @return the newly created (cloned) {@link StudyPlan}.
     * @throws IllegalArgumentException if the source plan does not exist.
     */
    public StudyPlan cloneStudyPlan(String sourceId, String newName,
                                    String newPeriod, LocalDate newStart,
                                    LocalDate newEnd) {
        StudyPlan source = studyPlans.get(sourceId);
        if (source == null) {
            throw new IllegalArgumentException("Plan no encontrado con ID: " + sourceId);
        }

        // Prototype: deep clone the whole plan
        StudyPlan copy = source.clone();
        copy.setId("P" + String.format("%03d", planCounter++));
        copy.setName(newName);
        copy.setPeriod(newPeriod);
        copy.setStartDate(newStart);
        copy.setEndDate(newEnd);
        copy.setDescription("Clonado de: " + source.getName());

        studyPlans.put(copy.getId(), copy);
        return copy;
    }

    // ── StudyPlan operations ──────────────────────────────────────────────────

    public List<StudyPlan> getStudyPlans() {
        return new ArrayList<>(studyPlans.values());
    }

    public StudyPlan getStudyPlanById(String id) {
        return studyPlans.get(id);
    }

    public void deleteStudyPlan(String id) {
        studyPlans.remove(id);
    }

    // ── Sample data ───────────────────────────────────────────────────────────

    private void loadSampleData() {
        // Teachers
        Teacher t1 = createTeacher("Carlos", "Ramírez", "Algoritmos",    "c.ramirez@uni.edu", "3001234567");
        Teacher t2 = createTeacher("Laura",  "Torres",  "Base de Datos", "l.torres@uni.edu",  "3107654321");
        Teacher t3 = createTeacher("Andrés", "Molina",  "Redes",         "a.molina@uni.edu",  "3209876543");

        // Subjects
        Subject s1 = createSubject("Algoritmos y Estructuras de Datos", 4,
                "Fundamentos algorítmicos y estructuras de datos avanzadas.", Modality.IN_PERSON);
        Subject s2 = createSubject("Bases de Datos", 3,
                "Diseño y administración de bases de datos relacionales.", Modality.IN_PERSON);
        Subject s3 = createSubject("Redes de Computadores", 3,
                "Protocolos y arquitecturas de redes.", Modality.HYBRID);
        Subject s4 = createSubject("Ingeniería de Software", 4,
                "Metodologías y buenas prácticas de desarrollo.", Modality.ONLINE);

        // Schedules
        Schedule sc1 = new Schedule(Schedule.WeekDay.MONDAY,    "07:00", "09:00", "Aula 201");
        Schedule sc2 = new Schedule(Schedule.WeekDay.TUESDAY,   "09:00", "11:00", "Lab 102");
        Schedule sc3 = new Schedule(Schedule.WeekDay.WEDNESDAY, "14:00", "16:00", "Aula 305");
        Schedule sc4 = new Schedule(Schedule.WeekDay.THURSDAY,  "11:00", "13:00", "Aula 210");

        // Groups
        Group g1 = createGroup("Grupo A - Algoritmos",     s1, t1, sc1, 30);
        Group g2 = createGroup("Grupo B - Bases de Datos", s2, t2, sc2, 25);
        Group g3 = createGroup("Grupo C - Redes",          s3, t3, sc3, 28);
        Group g4 = createGroup("Grupo D - Ing. Software",  s4, t1, sc4, 30);

        // Initial study plan (using Builder)
        createStudyPlan(
                "Plan 2024-I",
                "2024-I",
                "Ingeniería de Sistemas",
                Modality.IN_PERSON,
                LocalDate.of(2024, 2, 5),
                LocalDate.of(2024, 6, 28),
                "Plan académico del primer semestre 2024.",
                Arrays.asList(g1, g2, g3, g4)
        );
    }
}