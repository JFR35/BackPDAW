package com.myobservation.user.service;

import com.myobservation.user.entity.MyUser;

import java.util.List;
import java.util.Optional;

public interface MyUserService {
    List<MyUser> getAllUsers();
    Optional getUserById(Long userId);
    MyUser createUser(MyUser user);
    Optional<MyUser> updateUserById(Long userId,MyUser updatedUser);
    boolean deleteUserById(Long userId);
}
