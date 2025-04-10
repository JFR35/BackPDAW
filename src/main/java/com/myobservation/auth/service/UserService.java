package com.myobservation.auth.service;


import com.myobservation.auth.dto.UserRequest;
import com.myobservation.auth.dto.UserResponse;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<UserResponse> getAllUsers();
    Optional<UserResponse> getUserById(Long userId);
    UserResponse createUser(UserRequest userRequest);
    Optional<Optional<UserResponse>> updateUserById(Long userId, UserRequest updatedUserRequest);
    boolean deleteUserById(Long userId);
}