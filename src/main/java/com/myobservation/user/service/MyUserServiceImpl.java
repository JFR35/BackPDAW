package com.myobservation.user.service;


import com.myobservation.user.entity.MyUser;
import com.myobservation.user.repository.MyUserRepository;
import com.myobservation.user.service.exception.EmailAlreadyExistsException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MyUserServiceImpl implements MyUserService {

    private final MyUserRepository myUserRepository;

    public MyUserServiceImpl(MyUserRepository myUserRepository) {
        this.myUserRepository = myUserRepository;
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
        if(existingUser.isPresent()){
            throw new EmailAlreadyExistsException("Su email ya está registrado");
        }
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
