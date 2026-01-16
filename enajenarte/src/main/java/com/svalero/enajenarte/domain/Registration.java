package com.svalero.enajenarte.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "Registration")
@Table(name = "registrations")
public class Registration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "registration_date")
    private LocalDate registrationDate;

    @Column
    @NotNull(message = "status is mandatory")
    private String status;

    @Column(name = "amount_paid")
    @Min(value = 0, message = "amountPaid must be a positive number")
    private double amountPaid;

    @Column(name = "payment_confirmed")
    private boolean paymentConfirmed;

    @Column
    @Min(value = 0, message = "rating must be a positive number")
    private int rating;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "workshop_id")
    private Workshop workshop;
}
