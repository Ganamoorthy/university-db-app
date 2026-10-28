package com.example.universitydb.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "lecturer")
@Getter
@Setter
@NoArgsConstructor
public class Lecturer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @ManyToOne(optional = false)
    @JoinColumn(name = "department_id")
    private Department department;
}