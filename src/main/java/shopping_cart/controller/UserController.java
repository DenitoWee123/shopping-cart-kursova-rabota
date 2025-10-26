package shopping_cart.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shopping_cart.model.User;
import shopping_cart.service.UserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/")
public class UserController {
  private final UserService userService;

  @GetMapping("/get/all")
  public ResponseEntity<List<User>> getUsers() {
    return ResponseEntity.ok(userService.getAllUsers());
  }

  @PutMapping("/add/user")
  public ResponseEntity<?> addUserToDatabase(@RequestBody User user) {
    var result = userService.addUser(user);
    if (result != null) {
      return ResponseEntity.ok(
          String.format("User Added name : %s, age: %d", result.getName(), result.getAge()));
    }
    return ResponseEntity.badRequest().build();
  }
}
