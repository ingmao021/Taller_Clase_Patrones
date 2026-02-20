package com.academia.patterns.builder;

import com.academia.model.Group;
import com.academia.model.Subject;
import com.academia.model.StudyPlan;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Concrete implementation of the BUILDER pattern for {@link StudyPlan}.
 *
 * Constructs a complex {@link StudyPlan} object step by step, ensuring that
 * all required fields are validated before the final object is assembled.
 *
 * <p>Usage example:</p>
 * <pre>
 *   StudyPlan plan = new StudyPlanBuilder()
 *       .setId("P001")
 *       .setName("Plan 2024-I")
 *       .setPeriod("2024-I")
 *       .setProgram("Ingeniería de Sistemas")
 *       .setModality(Subject.Modality.IN_PERSON)
 *       .setStartDate(LocalDate.of(2024, 2, 1))
 *       .setEndDate(LocalDate.of(2024, 6, 30))
 *       .setDescription("First semester study plan.")
 *       .addGroup(group1)
 *       .addGroup(group2)
 *       .build();
 * </pre>
 */
public class StudyPlanBuilder implements IStudyPlanBuilder {

    private String          id;
    private String          name;
    private String          period;
    private String          program;
    private Subject.Modality modality;
    private LocalDate       startDate;
    private LocalDate       endDate;
    private String          description;
    private List<Group>     groups;

    public StudyPlanBuilder() {
        reset();
    }

    @Override
    public StudyPlanBuilder setId(String id) {
        this.id = id;
        return this;
    }

    @Override
    public StudyPlanBuilder setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public StudyPlanBuilder setPeriod(String period) {
        this.period = period;
        return this;
    }

    @Override
    public StudyPlanBuilder setProgram(String program) {
        this.program = program;
        return this;
    }

    @Override
    public StudyPlanBuilder setModality(Subject.Modality modality) {
        this.modality = modality;
        return this;
    }

    @Override
    public StudyPlanBuilder setStartDate(LocalDate startDate) {
        this.startDate = startDate;
        return this;
    }

    @Override
    public StudyPlanBuilder setEndDate(LocalDate endDate) {
        this.endDate = endDate;
        return this;
    }

    @Override
    public StudyPlanBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    @Override
    public StudyPlanBuilder addGroup(Group group) {
        if (group != null) {
            this.groups.add(group);
        }
        return this;
    }

    /**
     * Validates all required fields and constructs the {@link StudyPlan}.
     * The builder is automatically reset after a successful build.
     */
    @Override
    public StudyPlan build() {
        validate();
        StudyPlan plan = new StudyPlan(
                id, name, period, program,
                modality, startDate, endDate,
                description, groups
        );
        reset();
        return plan;
    }

    @Override
    public void reset() {
        this.id          = null;
        this.name        = null;
        this.period      = null;
        this.program     = null;
        this.modality    = Subject.Modality.IN_PERSON;
        this.startDate   = null;
        this.endDate     = null;
        this.description = "";
        this.groups      = new ArrayList<>();
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private void validate() {
        List<String> errors = new ArrayList<>();

        if (name == null      || name.isBlank())    errors.add("El nombre es obligatorio.");
        if (period == null    || period.isBlank())  errors.add("El período es obligatorio.");
        if (program == null   || program.isBlank()) errors.add("El programa es obligatorio.");
        if (startDate == null)                       errors.add("La fecha de inicio es obligatoria.");
        if (endDate   == null)                       errors.add("La fecha de fin es obligatoria.");

        if (startDate != null && endDate != null && endDate.isBefore(startDate)) {
            errors.add("La fecha de fin no puede ser anterior a la de inicio.");
        }

        if (!errors.isEmpty()) {
            throw new IllegalStateException("No se puede construir el plan:\n"
                    + String.join("\n", errors));
        }
    }
}