package tk.project.globus.hw.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "currencies")
@EntityListeners(AuditingEntityListener.class)
public class CurrencyEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(name = "char_code", updatable = false, nullable = false, unique = true)
  private String charCode = "RUB";

  @Column(name = "name", updatable = false, nullable = false)
  private String name;

  @Column(name = "vunit_rate", nullable = false)
  private BigDecimal vunitRate;

  @CreatedDate
  @Column(name = "create_date_time", updatable = false, nullable = false)
  private Instant createDateTime;

  @LastModifiedDate
  @Column(name = "update_date_time", nullable = false)
  private Instant updateDateTime;
}
