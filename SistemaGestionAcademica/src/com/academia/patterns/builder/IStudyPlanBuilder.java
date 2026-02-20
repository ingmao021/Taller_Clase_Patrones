package com.academia.patterns.builder;

import com.academia.model.Group;
import com.academia.model.Subject;
import com.academia.model.StudyPlan;

import java.time.LocalDate;

/**
 * Builder interface for constructing a {@link StudyPlan} step by step.
 *
 * Each method configures one aspect of the plan and returns the builder
 * itself to allow method chaining (fluent API).
 */
public interface IStudyPlanBuilder {

    IStudyPlanBuilder setId(String id);

    IStudyPlanBuilder setName(String name);

    IStudyPlanBuilder setPeriod(String period);

    IStudyPlanBuilder setProgram(String program);

    IStudyPlanBuilder setModality(Subject.Modality modality);

    IStudyPlanBuilder setStartDate(LocalDate startDate);

    IStudyPlanBuilder setEndDate(LocalDate endDate);

    IStudyPlanBuilder setDescription(String description);

    IStudyPlanBuilder addGroup(Group group);

    /**
     * Validates all required fields and builds the {@link StudyPlan}.
     *
     * @return the fully constructed {@link StudyPlan}.
     * @throws IllegalStateException if any required field is missing or invalid.
     */
    StudyPlan build();

    /**
     * Resets the builder to its initial state so it can be reused.
     */
    void reset();
}