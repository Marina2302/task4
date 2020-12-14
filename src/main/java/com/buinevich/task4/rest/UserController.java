package com.buinevich.task4.rest;

import com.buinevich.task4.model.UserMapper;
import com.buinevich.task4.model.dto.UserResponse;
import com.buinevich.task4.model.entities.User;
import com.buinevich.task4.model.enums.Status;
import com.buinevich.task4.model.repositories.UserRepo;
import com.buinevich.task4.services.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserRepo userRepo;
    private final UserService userService;
    private final SessionRegistry sessionRegistry;

    public UserController(UserRepo userRepo, UserService userService, SessionRegistry sessionRegistry) {
        this.userRepo = userRepo;
        this.userService = userService;
        this.sessionRegistry = sessionRegistry;
    }

    @GetMapping
    public Collection<UserResponse> getUsers() {
        return UserMapper.ListUsersToUserResponses(userRepo.findAll());
    }

    @PutMapping("/unblock")
    public void unblockUser(@RequestBody List<Integer> ids) {
        userRepo.findAllById(ids).forEach(user -> {
            user.setStatus(Status.ACTIVE);
            userRepo.save(user);
        });
    }

    @PutMapping("/block")
    public void blockUser(@RequestBody List<Integer> ids) {
        User currentUser = userService.getCurrentUser();
        List<User> loggedUsers = sessionRegistry.getAllPrincipals().stream().map(userService::getLoggedUser).collect(Collectors.toList());
        userRepo.findAllById(ids).forEach(user -> {
            user.setStatus(Status.BLOCK);
            userRepo.save(user);
        });
        closeSessionsForBlockOrDeleteUsers(ids, currentUser, loggedUsers);
    }

    @DeleteMapping("/delete")
    public void deleteUser(@RequestBody List<Integer> ids) {
        User currentUser = userService.getCurrentUser();
        List<User> loggedUsers = sessionRegistry.getAllPrincipals().stream().map(userService::getLoggedUser).collect(Collectors.toList());
        ids.forEach(userRepo::deleteById);
        closeSessionsForBlockOrDeleteUsers(ids, currentUser, loggedUsers);
    }

    private void closeSessionsForBlockOrDeleteUsers(List<Integer> ids, User currentUser, List<User> loggedUsers) {
        if (ids.contains(currentUser.getId())) {
            SecurityContextHolder.clearContext();
        }
        List<User> loggedUsersToLogOut = loggedUsers.stream().filter(user -> ids.contains(user.getId())).collect(Collectors.toList());
        loggedUsersToLogOut.forEach(user -> sessionRegistry.getAllSessions(user.getMail(), true).forEach(SessionInformation::expireNow));
    }
}
