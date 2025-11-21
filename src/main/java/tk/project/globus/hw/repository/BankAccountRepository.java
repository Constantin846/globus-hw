package tk.project.globus.hw.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tk.project.globus.hw.entity.BankAccountEntity;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccountEntity, UUID> {

  List<BankAccountEntity> findAllByUserId(UUID userId, Pageable pageable);
}
