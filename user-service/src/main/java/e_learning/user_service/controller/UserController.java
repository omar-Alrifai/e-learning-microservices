package e_learning.user_service.controller;
import e_learning.user_service.service.jwt.JwtUtils;
import e_learning.user_service.model.User;
import e_learning.user_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private AuthenticationManager authenticationManager; //  حقن AuthenticationManager
    @Autowired
    private JwtUtils jwtUtils; //  حقن JwtUtils

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody User user) {
        // الدور الافتراضي عند التسجيل
        user.setRole("STUDENT");
        User createdUser = userService.createUser(user);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    //لتسجيل الدخول والحصول على JWT
    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticateUser(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String jwt = jwtUtils.generateToken(userDetails);
        // إرجاع الـ token
        return ResponseEntity.ok(Map.of("jwtToken", jwt));
    }


    @PostMapping("/admin/add-instructor")
    @PreAuthorize("hasRole('ADMIN')") // يتطلب دور ADMIN
    public ResponseEntity<User> addInstructor(@RequestBody User user) {
        user.setRole("INSTRUCTOR");
        User createdInstructor = userService.createUser(user);
        return new ResponseEntity<>(createdInstructor, HttpStatus.CREATED);
    }

    @GetMapping("/byUsername")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR', 'STUDENT', 'INTERNAL_SERVICE')")
    public ResponseEntity<User> getUserByUsername(@RequestParam("username") String username) {
        User user = userService.findByUsername(username);
        if (user != null) {
            return new ResponseEntity<>(user, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR', 'STUDENT', 'INTERNAL_SERVICE')")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(user -> new ResponseEntity<>(user, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR', 'STUDENT')")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN') or (#id == authentication.principal.id)") // يمكن للمسؤول تحديث أي شيء، والمستخدم تحديث بياناته
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        User updatedUser = userService.updateUser(id, userDetails);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // يتطلب دور ADMIN للحذف
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}