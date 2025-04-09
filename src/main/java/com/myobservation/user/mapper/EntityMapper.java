package com.myobservation.user.mapper;

import com.myobservation.user.dto.EntityUserDTO;
import com.myobservation.user.entity.MyUser;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EntityMapper {

    EntityUserDTO toDTO(MyUser myUser);
    MyUser toEntity (EntityUserDTO entityUserDTO);
}
