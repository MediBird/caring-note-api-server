package com.springboot.api.counselcard.entity.information.living;

import com.springboot.api.counselcard.dto.information.living.ExerciseDTO;
import com.springboot.enums.ExercisePattern;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
public class Exercise {
    @Column(nullable = false)
    private String exerciseNote;

    @Enumerated(EnumType.STRING)
    private ExercisePattern exercisePattern;

    public static Exercise initializeDefault() {
        Exercise exercise = new Exercise();
        exercise.exerciseNote = "";
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
        this.exerciseNote = Objects.requireNonNullElse(exerciseDTO.exerciseNote(), this.exerciseNote);
        this.exercisePattern = Objects.requireNonNullElse(exerciseDTO.exercisePattern(), this.exercisePattern);
    }
}
