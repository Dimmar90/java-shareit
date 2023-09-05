package ru.practicum.shareit.item.comment;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "comments", schema = "public")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String text;
    @Column(name = "author")
    private String authorName;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date created;
    @Column(name = "item_id")
    private Long itemId;
}
