package tk.project.globus.hw.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import tk.project.globus.hw.dto.account.AccountCreateDto;
import tk.project.globus.hw.dto.account.AccountInfoDto;
import tk.project.globus.hw.dto.account.AccountUpdateDto;
import tk.project.globus.hw.entity.BankAccountEntity;

@Mapper(componentModel = "spring")
public interface BankAccountMapper {

  BankAccountMapper MAPPER = Mappers.getMapper(BankAccountMapper.class);

  @Mapping(target = "user", ignore = true)
  BankAccountEntity toBankAccountEntity(AccountCreateDto account);

  BankAccountEntity toBankAccountEntity(AccountUpdateDto account);

  AccountInfoDto toAccountInfoDto(BankAccountEntity account);

  List<AccountInfoDto> toAccountsInfoDto(List<BankAccountEntity> accounts);
}
