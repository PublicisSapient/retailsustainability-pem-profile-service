package com.publicis.sapient.p2p.repository;

import com.publicis.sapient.p2p.model.User;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileRepository {
    void addUserProfile(User userProfile);
    void updateUserProfile(User userProfile);
    User getUserProfile(String emailId);

    User getUserById(String id);

    void deleteById(String id);
}
