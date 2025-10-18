package org.technoready.meliecommerce.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.technoready.meliecommerce.entity.User;
import org.technoready.meliecommerce.service.UserService;

import java.util.List;


@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping()
    public List<User> findAll( @RequestParam(required = false) boolean activeOnly)
    {
        if(activeOnly)
            return userService.findAllIsActive();
         return userService.findAll();
    }

    @GetMapping("/{id}")
    public User findById(@PathVariable Long id)
    {
        return userService.findById(id);
    }

    @PostMapping
    public ResponseEntity<User> save(@RequestBody User user)
    {
        return ResponseEntity.ok(userService.save(user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable long id)
    {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> update(@RequestBody User user, @PathVariable long id)
    {
        return ResponseEntity.ok(userService.update(user, id));
    }


}
