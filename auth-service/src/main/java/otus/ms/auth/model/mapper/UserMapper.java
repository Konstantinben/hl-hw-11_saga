package otus.ms.auth.model.mapper;

import otus.ms.auth.model.dto.UserSignupDto;
import otus.ms.auth.model.entity.User;
import org.mapstruct.*;

@Mapper(componentModel =  "spring")
public interface UserMapper {

    @Mapping(target = "uuid", expression = "java(java.util.UUID.randomUUID())")
    @Mapping(target = "role", expression = "java(otus.ms.auth.model.Role.USER)")
    @Mapping(target = "password", source = "encodedPassword")
    User toUser(UserSignupDto userDto, String encodedPassword);
}
