package com.acamica.social.rest;

import java.util.UUID;
import javax.validation.constraints.NotNull;

import com.acamica.social.domain.User;
import com.acamica.social.domain.UserRepository;

public class FollowUserCommand {
    private final UserRepository userRepository;

    @NotNull
    private UUID followerId;

    @NotNull
    private UUID targetId;

    public FollowUserCommand(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void execute() throws UserNotFoundException {
        User follower = userRepository.findById(followerId).orElseThrow(UserNotFoundException::new);
        User target = userRepository.findById(targetId).orElseThrow(UserNotFoundException::new);

        follower.follow(target);
    }

    public void setFollowerId(UUID followerId) {
        this.followerId = followerId;
    }

    public void setTargetId(UUID followerId) {
        this.targetId = followerId;
    }
}
