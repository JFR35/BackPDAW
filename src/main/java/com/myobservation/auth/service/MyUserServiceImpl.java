package com.myobservation.auth.service;


import com.myobservation.auth.entity.MyUser;
import com.myobservation.auth.repository.MyUserRepository;
import com.myobservation.auth.service.exception.EmailAlreadyExistsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MyUserServiceImpl implements MyUserService {

    private final MyUserRepository myUserRepository;
    private final PasswordEncoder passwordEncoder;
    public MyUserServiceImpl(MyUserRepository myUserRepository, PasswordEncoder passwordEncoder) {
        this.myUserRepository = myUserRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public List<MyUser> getAllUsers() {
        return myUserRepository.findAll();
    }

    @Override
    public Optional<MyUser> getUserById(Long userId) {
        return myUserRepository.findById(userId);
    }

    @Override
    public MyUser createUser(MyUser user) {
        Optional<MyUser> existingUser = myUserRepository.findByEmail(user.getEmail());
        if (existingUser.isPresent()) {
            throw new EmailAlreadyExistsException("Su email ya está registrado");
        }
        // Encripta la contraseña antes de guardar
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        return myUserRepository.save(user);
    }

    public Optional<MyUser> updateUserById(Long id, MyUser updatedUser) {
        // Verify if the user exits, then update
        return myUserRepository.findById(id).map(user -> {
            user.setFirstName(updatedUser.getFirstName());
            user.setLastName(updatedUser.getLastName());
            user.setEmail(updatedUser.getEmail());
            user.setPassword(updatedUser.getPassword());
            return myUserRepository.save(user);
        });
    }

        @Override
        public boolean deleteUserById (Long userId){
            return myUserRepository.findById(userId).map(user -> {
                myUserRepository.delete(user);
                return true;
            }).orElse(false);
        }
    }
