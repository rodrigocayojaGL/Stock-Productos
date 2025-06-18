package ar.edu.udecy.web.inventory.controller.impl;

    import ar.edu.udecy.web.inventory.controller.UserController;
    import ar.edu.udecy.web.inventory.dto.UserRequestDTO;
    import ar.edu.udecy.web.inventory.dto.UserResponseDTO;
    import ar.edu.udecy.web.inventory.service.UserService;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.security.access.prepost.PreAuthorize;
    import org.springframework.web.bind.annotation.*;

    import java.util.List;

    @RestController
    @RequestMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")

    public class UserControllerImpl implements UserController {

        @Autowired
        private UserService userService;

        @Override
        @PostMapping
        public UserResponseDTO createUser(@RequestBody UserRequestDTO userRequestDTO) {
            return userService.createUser(userRequestDTO);
        }

        @Override
        @GetMapping("/{username}")
        public UserResponseDTO getUserById(@PathVariable String username) {
            return userService.getUserById(username);
        }

        @Override
        @GetMapping
        public List<UserResponseDTO> getAllUsers() {
            return userService.getAllUsers();
        }

        @Override
        @PutMapping("/{id}")
        public UserResponseDTO updateUser(@PathVariable Long id, @RequestBody UserRequestDTO userRequestDTO) {
            return userService.updateUser(id, userRequestDTO);
        }

        @Override
        @DeleteMapping("/{username}")
        public void deleteUser(@PathVariable String username) {
            userService.deleteUser(username);
        }
    }