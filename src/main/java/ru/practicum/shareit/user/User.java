package ru.practicum.shareit.user;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Email;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Entity
@Table(name = "users", schema = "public")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Email
    private String email;
}
