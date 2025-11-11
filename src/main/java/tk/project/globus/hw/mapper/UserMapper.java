package tk.project.globus.hw.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import tk.project.globus.hw.dto.user.UserCreateDto;
import tk.project.globus.hw.dto.user.UserInfoDto;
import tk.project.globus.hw.dto.user.UserUpdateDto;
import tk.project.globus.hw.entity.UserEntity;

@Mapper(componentModel = "spring")
public interface UserMapper {

  UserMapper MAPPER = Mappers.getMapper(UserMapper.class);

  UserEntity toUserEntity(UserCreateDto user);

  UserEntity toUserEntity(UserUpdateDto user);

  UserInfoDto toUserInfoDto(UserEntity user);
}
