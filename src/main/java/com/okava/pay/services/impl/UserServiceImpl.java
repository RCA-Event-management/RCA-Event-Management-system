package com.okava.pay.services.impl;

import com.okava.pay.models.Tag;
import com.okava.pay.models.User;
import com.okava.pay.models.enums.ERole;
import com.okava.pay.repositories.IUserRepository;
import com.okava.pay.services.ITagService;
import com.okava.pay.services.IUserService;
import com.okava.pay.utils.Utility;
import com.okava.pay.utils.dtos.RegisterDTO;
import com.okava.pay.utils.exceptions.BadRequestException;
import com.okava.pay.utils.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements IUserService {

    private final IUserRepository userRepository;

    private final ITagService tagService;

    @Autowired
    public UserServiceImpl(IUserRepository userRepository, ITagService tagService) {
        this.userRepository = userRepository;
        this.tagService = tagService;
    }

    @Override
    public Page<User> all(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", "id", id.toString()));
    }

    @Override
    public Page<User> byRole(ERole role, Pageable pageable) {
        return userRepository.findByRole(role, pageable);
    }

    @Override
    public User create(RegisterDTO dto) {
        User user = new User();

        user.setRole(ERole.STUDENT);
        user.setEmail(dto.getEmail());
        user.setFullNames(dto.getFullNames());
        user.setPassword(Utility.encode(dto.getPassword()));

        if (!isUnique(user)) throw new BadRequestException("The provided email is already used in the app");

        return userRepository.save(user);
    }

    @Override
    public boolean isUnique(User user) {
        Optional<User> userOptional = this.userRepository.findByEmail(user.getEmail());
        return userOptional.isEmpty();
    }

    @Override
    public User getLoggedInUser() {
        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() == "anonymousUser")
            throw new BadRequestException("You are not logged in, try to log in");

        String email;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            email = ((UserDetails) principal).getUsername();
        } else {
            email = principal.toString();
        }

        return userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
    }

    @Override
    public User addTag(Long userId, Long tagId) {
        User user = findById(userId);
        Tag tag = tagService.findById(tagId);

        user.getTags().add(tag);

        return userRepository.save(user);
    }

    @Override
    public User removeTag(Long userId, Long tagId) {
        User user = findById(userId);
        Tag tag = tagService.findById(tagId);

        user.getTags().remove(tag);

        return userRepository.save(user);
    }

    @Override
    public List<User> findMany(List<Long> userIds) {
        return userRepository.findAllById(userIds);
    }
}
