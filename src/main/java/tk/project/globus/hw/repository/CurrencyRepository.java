package tk.project.globus.hw.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tk.project.globus.hw.entity.CurrencyEntity;

@Repository
public interface CurrencyRepository extends JpaRepository<CurrencyEntity, UUID> {

  Optional<CurrencyEntity> findByCharCode(String charCode);

  @Query(
      value =
          """
          SELECT *
          FROM currencies c
          WHERE c.char_code IN :charCodes
          FOR UPDATE NOWAIT
          """,
      nativeQuery = true)
  List<CurrencyEntity> findAllByCharCodeInForUpdateNoWait(
      @Param("charCodes") List<String> charCodes);

  default Map<String, CurrencyEntity> findMapByCharCodeInForUpdateNoWait(List<String> charCodes) {
    return findAllByCharCodeInForUpdateNoWait(charCodes).stream()
        .collect(Collectors.toMap(CurrencyEntity::getCharCode, Function.identity()));
  }
}
