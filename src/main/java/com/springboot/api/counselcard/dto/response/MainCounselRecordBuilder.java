package com.springboot.api.counselcard.dto.response;

import com.springboot.api.counselcard.entity.CounselCard;
import java.util.List;
import java.util.function.Function;

public class MainCounselRecordBuilder {

    private MainCounselRecordBuilder() {
    }

    public static <T> MainCounselRecord<T> build(
        List<CounselCard> sortedCards,
        Function<CounselCard, T> extractor
    ) {
        if (sortedCards == null || sortedCards.isEmpty()) {
            return new MainCounselRecord<>(null, List.of());
        }

        T current = extractor.apply(sortedCards.getFirst());

        List<TimeRecordedRes<T>> history = sortedCards.stream()
            .skip(1)
            .map(counselCard -> new TimeRecordedRes<>(
                counselCard.getCounselSession().getScheduledStartDateTime().toLocalDate().toString(),
                extractor.apply(counselCard)))
            .toList();

        return new MainCounselRecord<>(current, history);
    }
}
