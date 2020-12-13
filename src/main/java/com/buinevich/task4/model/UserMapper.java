package com.buinevich.task4.model;

import com.buinevich.task4.model.dto.UserRequest;
import com.buinevich.task4.model.dto.UserResponse;
import com.buinevich.task4.model.entities.User;

import java.util.List;
import java.util.stream.Collectors;

public class UserMapper {

    public static UserResponse UserToUserResponse(User user) {
        return new UserResponse(user.getId(),
                user.getName(),
                user.getMail(),
                user.getRegDate(),
                user.getLastLoginDate(),
                user.getStatus().toString());
    }

    public static User UserRequestToUser(UserRequest userRequest) {
        return new User(userRequest.getName(),
                userRequest.getMail(),
                userRequest.getPassword());
    }

    public static List<UserResponse> ListUsersToUserResponses(List<User> users) {
        return users.stream().map(UserMapper::UserToUserResponse).collect(Collectors.toList());
    }
}
