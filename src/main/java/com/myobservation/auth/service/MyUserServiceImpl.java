package com.myobservation.auth.service;

import com.myobservation.auth.dto.UserRequest;
import com.myobservation.auth.dto.UserResponse;
import com.myobservation.auth.entity.MyUser;
import com.myobservation.auth.entity.Role;
import com.myobservation.auth.mapper.EntityMapper;
import com.myobservation.auth.repository.MyUserRepository;
import com.myobservation.auth.repository.RoleRepository;
import com.myobservation.auth.service.exception.EmailAlreadyExistsException;
import com.myobservation.auth.service.exception.ResourceNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MyUserServiceImpl implements UserService {

    private final MyUserRepository myUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final EntityMapper entityMapper;
    private final RoleRepository roleRepository;
    public MyUserServiceImpl(MyUserRepository myUserRepository, PasswordEncoder passwordEncoder, EntityMapper entityMapper, RoleRepository roleRepository) {
        this.myUserRepository = myUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.entityMapper = entityMapper;
        this.roleRepository = roleRepository;
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

    public UserResponse createPractitioner(UserRequest userRequest) {
        MyUser practitioner = entityMapper.toMyUser(userRequest);

        Role practitionerRole = roleRepository.findByName("ROLE_PRACTITIONER")
                .orElseThrow(() -> new ResourceNotFoundException("Role 'ROLE_PRACTITIONER' not found"));

        practitioner.setRoles(Set.of(practitionerRole));
        practitioner.setPassword(passwordEncoder.encode(practitioner.getPassword()));

        MyUser savedPractitioner = myUserRepository.save(practitioner);
        return entityMapper.toUserResponse(savedPractitioner);
    }
}