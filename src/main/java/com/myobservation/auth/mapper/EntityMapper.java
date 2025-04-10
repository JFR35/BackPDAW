package com.myobservation.auth.mapper;

import com.myobservation.auth.dto.UserRequest;
import com.myobservation.auth.dto.UserResponse;
import com.myobservation.auth.entity.MyUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface EntityMapper {

    @Mapping(target = "roles", expression = "java(myUser.getRoles().stream().map(com.myobservation.auth.entity.Role::getName).collect(java.util.stream.Collectors.toSet()))")
    UserResponse toUserResponse(MyUser myUser);

    MyUser toMyUser(UserRequest userRequest);

    void updateUserFromRequest(UserRequest userRequest, @MappingTarget MyUser myUser);
}