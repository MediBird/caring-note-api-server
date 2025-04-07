package com.springboot.api.counselcard.dto.information.living;

import com.springboot.api.counselcard.entity.information.living.Exercise;
import com.springboot.enums.ExercisePattern;

public record ExerciseDTO(
    ExercisePattern exercisePattern,
    String exerciseNote) {

    public ExerciseDTO(Exercise exercise) {
        this(
                exercise != null ? exercise.getExercisePattern() : null,
                exercise != null ? exercise.getExerciseNote() : null
        );
    }
}
