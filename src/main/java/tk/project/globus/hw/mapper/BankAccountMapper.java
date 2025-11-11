package tk.project.globus.hw.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import tk.project.globus.hw.dto.account.AccountCreateDto;
import tk.project.globus.hw.dto.account.AccountInfoDto;
import tk.project.globus.hw.dto.account.AccountUpdateDto;
import tk.project.globus.hw.entity.BankAccountEntity;

@Mapper(componentModel = "spring")
public interface BankAccountMapper {

  @Mapping(target = "user", ignore = true)
  BankAccountEntity toBankAccountEntity(AccountCreateDto account);

  BankAccountEntity toBankAccountEntity(AccountUpdateDto account);

  AccountInfoDto toAccountInfoDto(BankAccountEntity account);
}
