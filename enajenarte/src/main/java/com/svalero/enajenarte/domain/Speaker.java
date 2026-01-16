package com.svalero.enajenarte.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "Speaker")
@Table(name = "speakers")
public class Speaker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "first_name")
    @NotNull(message = "firstName is mandatory")
    private String firstName;

    @Column(name = "last_name")
    @NotNull(message = "lastName is mandatory")
    private String lastName;

    @Column
    @NotNull(message = "email is mandatory")
    private String email;

    @Column
    @NotNull(message = "speciality is mandatory")
    private String speciality;

    @Column
    private String bio;

    @Column(name = "profile_image_url")
    @NotNull(message = "profileImageUrl is mandatory")
    private String profileImageUrl;

    @Column(name = "linkedin_url")
    @NotNull(message = "linkedinUrl is mandatory")
    private String linkedinUrl;

    @OneToMany(mappedBy = "speaker")
    @JsonBackReference
    private List<Workshop> workshops;
}
