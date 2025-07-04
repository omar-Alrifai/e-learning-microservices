package e_learning.user_service.service;
import e_learning.user_service.model.User;
import e_learning.user_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

@Service
public class UserService implements UserDetailsService { //  تطبيق UserDetailsService

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; //  حقن PasswordEncoder

    public User createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword())); //  تشفير كلمة المرور
        // تعيين الدور الافتراضي إذا لم يتم تحديده (مثلاً عند التسجيل الذاتي)
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("STUDENT");
        }
        return userRepository.save(user);
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public User findByUsername(String username) { // ✅ طريقة جديدة للبحث عن المستخدم بالاسم
        return userRepository.findByUsername(username);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User updateUser(Long id, User userDetails) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setUsername(userDetails.getUsername());
        // إذا تم تحديث كلمة المرور، قم بتشفيرها
        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        }
        user.setRole(userDetails.getRole());
        user.setEmail(userDetails.getEmail());
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //  هذه الطريقة تستخدمها Spring Security لتحميل تفاصيل المستخدم للمصادقة
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        // بناء UserDetails من كيان User
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                new ArrayList<>(List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + user.getRole())))
        );
    }
}