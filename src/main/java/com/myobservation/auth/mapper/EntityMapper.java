package com.myobservation.auth.mapper;

import com.myobservation.auth.dto.UserRequest;
import com.myobservation.auth.dto.UserResponse;
import com.myobservation.auth.entity.MyUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * Mapeador de entidades para la conversión entre objetos DTO y entidades.
 *
 */
@Mapper(componentModel = "spring")
public interface EntityMapper {

    /**
     * Conviernte la entidad {@link MyUser} en un objeto de respuesta {@link UserResponse}.
     * >Incluye la conversión de los roles del usuario en un conjunto de nombres de roles.
     * @param myUser Entidad de usuario a transformar.
     * @return Objeto {@link UserResponse} con los datos del usuario.
     */
    @Mapping(target = "roles", expression = "java(myUser.getRoles().stream().map(com.myobservation.auth.entity.Role::getName).collect(java.util.stream.Collectors.toSet()))")
    UserResponse toUserResponse(MyUser myUser);

    /**
     * Convierte un objeto de solicitud {@link UserRequest} en una entidad {@link MyUser}
     * @param userRequest DTO de solicitud con los datos del user.
     * @return Entidad {@link MyUser} con los datos mapeados.
     */
    MyUser toMyUser(UserRequest userRequest);

    /**
     * Actualiza la entidad de usuario {@link MyUser} con los datos del objeto {@link  UserRequest}
     * @param userRequest DTO con los datos que se actualizarán en la entidad.
     * @param myUser Entidad de usuario a actualizar.
     */
    void updateUserFromRequest(UserRequest userRequest, @MappingTarget MyUser myUser);
}