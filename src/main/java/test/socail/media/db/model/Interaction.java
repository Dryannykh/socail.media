package test.socail.media.db.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import test.socail.media.db.model.enums.Status;

import java.util.UUID;

@Entity
@Table(name = "interaction")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Interaction {

    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name = "request_id")
    private UUID requestId;

    private String sender;

    private String author;

    @Enumerated(EnumType.STRING)
    private Status status;
}
