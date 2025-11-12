package tk.project.globus.hw.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tk.project.globus.hw.entity.CurrencyEntity;

@Repository
public interface CurrencyRepository extends JpaRepository<CurrencyEntity, UUID> {

  Optional<CurrencyEntity> findByCharCode(String charCode);

  List<CurrencyEntity> findAllByCharCodeIn(List<String> charCodes);

  default Map<String, CurrencyEntity> findMapByCharCodeIn(List<String> charCodes) {
    return findAllByCharCodeIn(charCodes).stream()
        .collect(Collectors.toMap(CurrencyEntity::getCharCode, Function.identity()));
  }
}
