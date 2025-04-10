package com.myobservation.auth.service;

import com.myobservation.auth.dto.UserRequest;
import com.myobservation.auth.dto.UserResponse;
import com.myobservation.auth.entity.MyUser;
import com.myobservation.auth.mapper.EntityMapper;
import com.myobservation.auth.repository.MyUserRepository;
import com.myobservation.auth.service.exception.EmailAlreadyExistsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MyUserServiceImpl implements UserService {

    private final MyUserRepository myUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final EntityMapper entityMapper;

    public MyUserServiceImpl(MyUserRepository myUserRepository, PasswordEncoder passwordEncoder, EntityMapper entityMapper) {
        this.myUserRepository = myUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.entityMapper = entityMapper;
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return myUserRepository.findAll().stream()
                .map(entityMapper::toUserResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<UserResponse> getUserById(Long userId) {
        return myUserRepository.findById(userId)
                .map(entityMapper::toUserResponse);
    }

    @Override
    public UserResponse createUser(UserRequest userRequest) {
        Optional<MyUser> existingUser = myUserRepository.findByEmail(userRequest.getEmail());
        if (existingUser.isPresent()) {
            throw new EmailAlreadyExistsException("Su email ya está registrado");
        }
        MyUser user = entityMapper.toMyUser(userRequest);
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        MyUser savedUser = myUserRepository.save(user);
        return entityMapper.toUserResponse(savedUser);
    }

    @Override
    public Optional<Optional<UserResponse>> updateUserById(Long userId, UserRequest updatedUserRequest) {
        return myUserRepository.findById(userId).map(existingUser -> {
            entityMapper.updateUserFromRequest(updatedUserRequest, existingUser);
            if (updatedUserRequest.getPassword() != null && !updatedUserRequest.getPassword().isEmpty()) {
                existingUser.setPassword(passwordEncoder.encode(updatedUserRequest.getPassword()));
            }
            MyUser savedUser = myUserRepository.save(existingUser);
            return Optional.of(entityMapper.toUserResponse(savedUser));
        });
    }

    @Override
    public boolean deleteUserById(Long userId) {
        return myUserRepository.findById(userId).map(user -> {
            myUserRepository.delete(user);
            return true;
        }).orElse(false);
    }
}