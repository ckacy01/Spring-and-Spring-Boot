package org.technoready.meliecommerce.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.technoready.meliecommerce.entity.User;
import org.technoready.meliecommerce.repository.UserRepository;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public List<User> findAllIsActive() {
        return userRepository.findAllByActiveTrue();
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public User update(User user, Long id) {
        User user1 = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not Found!") );
        user1.setEmail(user.getEmail());
        user1.setName(user.getName());
        user1.setLastName(user.getLastName());
        user1.setActive(user.isActive());
        return userRepository.save(user1);
    }

    public void delete(Long id) {
        User  user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not Found!") );
        user.setActive(false);
        userRepository.save(user);
    }

}
