package de.lall.tomgrabber.repository.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDate;

@Data
@EqualsAndHashCode
@ToString
public class Tom {
    private final String id;

    @EqualsAndHashCode.Exclude
    private final LocalDate date;
}
