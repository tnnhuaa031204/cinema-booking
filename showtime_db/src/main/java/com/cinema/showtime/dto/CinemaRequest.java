package com.cinema.showtime.dto;

import jakarta.validation.constraints.NotBlank;

public record CinemaRequest(@NotBlank String name, String address) {
}