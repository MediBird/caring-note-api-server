package com.springboot.api.counselcard.dto.information.living;

import com.springboot.api.common.annotation.ValidEnum;
import com.springboot.api.counselcard.entity.information.living.Exercise;
import com.springboot.enums.ExercisePattern;

public record ExerciseDTO(
    @ValidEnum(enumClass = ExercisePattern.class)
    ExercisePattern exercisePattern,
    String exerciseNote) {

    public ExerciseDTO(Exercise exercise) {
        this(
            exercise.getExercisePattern(),
            exercise.getExerciseNote()
        );
    }
}
