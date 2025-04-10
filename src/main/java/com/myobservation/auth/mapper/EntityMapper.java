package com.myobservation.auth.mapper;

import com.myobservation.auth.dto.EntityUserDTO;
import com.myobservation.auth.entity.MyUser;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EntityMapper {

    EntityUserDTO toDTO(MyUser myUser);
    MyUser toEntity (EntityUserDTO entityUserDTO);
}
