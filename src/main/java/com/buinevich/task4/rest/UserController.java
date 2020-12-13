package com.buinevich.task4.rest;

import com.buinevich.task4.model.UserMapper;
import com.buinevich.task4.model.dto.UserResponse;
import com.buinevich.task4.model.enums.Status;
import com.buinevich.task4.model.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserRepo userRepo;

    @GetMapping
    public Collection<UserResponse> getUsers() {
        return UserMapper.ListUsersToUserResponses(userRepo.findAll());
    }

    @PutMapping("/block")
    public void blockUser(@RequestBody List<Integer> ids) {
        userRepo.findAllById(ids).forEach(user -> user.setStatus(Status.BLOCK));
    }

    @PutMapping("/unblock")
    public void unblockUser(@RequestBody List<Integer> ids) {
        userRepo.findAllById(ids).forEach(user -> user.setStatus(Status.ACTIVE));
    }

    @DeleteMapping("/delete")
    public void deleteUser(@RequestBody List<Integer> ids) {
        ids.forEach(id -> userRepo.deleteById(id));
    }
}
