package com.buinevich.task4.services;

import com.buinevich.task4.model.UserMapper;
import com.buinevich.task4.model.dto.UserRequest;
import com.buinevich.task4.model.entities.User;
import com.buinevich.task4.model.enums.Status;
import com.buinevich.task4.model.repositories.UserRepo;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;

@Service
public class UserService implements UserDetailsService {

    private final UserRepo userRepo;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepo userRepo, BCryptPasswordEncoder passwordEncoder) {
        super();
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User save(UserRequest userRequest) {
        userRequest.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        User createdUser = UserMapper.UserRequestToUser(userRequest);
        createdUser.setStatus(Status.ACTIVE);
        return userRepo.save(createdUser);
    }

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByMail(username).orElseThrow(() -> new UsernameNotFoundException("Invalid mail or password."));
        if (user.getStatus().equals(Status.BLOCK)) {
          throw new LockedException("User blocked.");
        }
        user.setLastLoginDate(LocalDateTime.now());
        userRepo.save(user);
        return new org.springframework.security.core.userdetails.User(user.getMail(), user.getPassword(), Collections.singletonList(new SimpleGrantedAuthority("USER")));
    }

    public boolean isUserExists(UserRequest userRequest) {
        return userRepo.existsByMail(userRequest.getMail());
    }

    public User getLoggedUser(Object principal) {
        String mail;
        if (principal instanceof UserDetails) {
            mail = ((UserDetails) principal).getUsername();
        } else {
            mail = principal.toString();
        }
        return userRepo.findByMail(mail).orElseThrow(() -> new UsernameNotFoundException("User not found."));
    }

    public User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String mail;
        if (principal instanceof UserDetails) {
            mail = ((UserDetails) principal).getUsername();
        } else {
            mail = principal.toString();
        }
        return userRepo.findByMail(mail).orElseThrow(() -> new UsernameNotFoundException("User not found."));
    }

}
