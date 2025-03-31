package com.springboot.api.counselcard.entity.information.living;

import java.util.Objects;

import com.springboot.api.counselcard.dto.information.living.ExerciseDTO;
import com.springboot.enums.ExercisePattern;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
public class Exercise {

    private String exerciseNote;

    @Enumerated(EnumType.STRING)
    private ExercisePattern exercisePattern;

    public static Exercise initializeDefault() {
        Exercise exercise = new Exercise();
        exercise.exerciseNote = null;
        exercise.exercisePattern = null;
        return exercise;
    }

    public static Exercise copy(Exercise exercise) {
        Exercise copiedExercise = new Exercise();
        copiedExercise.exerciseNote = exercise.exerciseNote;
        copiedExercise.exercisePattern = exercise.exercisePattern;
        return copiedExercise;
    }

    public void update(ExerciseDTO exerciseDTO) {
        if(Objects.isNull(exerciseDTO)) {
            return;
        }

        this.exerciseNote = exerciseDTO.exerciseNote();
        this.exercisePattern = exerciseDTO.exercisePattern();
    }
}
