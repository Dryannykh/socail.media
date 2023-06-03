package test.socail.media.db.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "correspondence")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Correspondence {

    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;

    @Column(name = "chat_id")
    private UUID chatId;

    private String sender;

    private String recipient;

    private String message;
}
