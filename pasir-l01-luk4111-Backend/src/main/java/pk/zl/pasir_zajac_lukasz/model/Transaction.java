package pk.zl.pasir_zajac_lukasz.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "transactions")
@SuppressWarnings("JpaDataSourceORMInspection")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double amount;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    private String tags;

    private String notes;

    private LocalDateTime timestamp = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Transaction(LocalDateTime timestamp, String notes, String tags, TransactionType type, Double amount, Long id, User user) {
        this.timestamp = timestamp;
        this.notes = notes;
        this.tags = tags;
        this.type = type;
        this.amount = amount;
        this.id = id;
    }
}