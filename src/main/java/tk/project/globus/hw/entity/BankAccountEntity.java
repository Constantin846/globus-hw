package tk.project.globus.hw.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Data
@Entity
@Table(name = "bank_accounts")
@EntityListeners(AuditingEntityListener.class)
public class BankAccountEntity implements CurrencyChangeable {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(name = "balance", precision = 20, scale = 10, nullable = false)
  private BigDecimal balance;

  @Column(name = "currency_char_code", nullable = false)
  private String currencyCharCode;

  @ManyToOne
  @JoinColumn(name = "user_id", updatable = false, nullable = false)
  private UserEntity user;

  @CreatedDate
  @Column(name = "create_date_time", updatable = false, nullable = false)
  private Instant createDateTime;

  @LastModifiedDate
  @Column(name = "update_date_time", nullable = false)
  private Instant updateDateTime;
}
