package org.example.model.entities;

import lombok.*;

@AllArgsConstructor
@Data
@Builder
public class Post {
    int id;
    String owner;
    String name;
    String text;
    boolean liked;
    boolean isEditable;
}
