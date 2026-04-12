package hr.algebra.iisbackend.dto;

import java.math.BigDecimal;

public record ProductInput(
        String naziv,
        BigDecimal cijena,
        String kategorija,
        String opis,
        Integer kolicina
) {}
