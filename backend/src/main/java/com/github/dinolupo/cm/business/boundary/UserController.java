package com.github.dinolupo.cm.business.boundary;

import com.github.dinolupo.cm.business.boundary.exception.ElementNotFoundException;
import com.github.dinolupo.cm.business.control.UserService;
import com.github.dinolupo.cm.business.entity.Role;
import com.github.dinolupo.cm.business.entity.UserRepository;
import com.github.dinolupo.cm.business.entity.User;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepo;

    @Autowired
    UserModelAssembler assembler;

    @Autowired
    PagedResourcesAssembler<User> pagedAssembler;

    // page, size, and sort are available due to spring web support, they are converted into Pageable instance
    @GetMapping
    ResponseEntity<?> all() {
        return filter(Pageable.unpaged());
    }

    @GetMapping(path = "/search")
    ResponseEntity<?> filter(Pageable pageable) {
        return ResponseEntity.ok(pagedAssembler.toModel(userRepo.findByDisabled(false, pageable), assembler));
    }

    @PostMapping
    ResponseEntity<?> newElement(@RequestBody User newUser) {
        var entityModel = assembler.toModel(userRepo.save(newUser));
        return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    @GetMapping("/{id}")
    ResponseEntity<?> one(@PathVariable Long id) {
        return ResponseEntity.ok(assembler.toModel(userRepo.findById(id).orElseThrow(
                ()->new ElementNotFoundException(Role.class, id))));
    }

    @DeleteMapping("/{id}")
    ResponseEntity<?> delete(@PathVariable Long id) {
        userRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/addrole")
    ResponseEntity<?> addRoleToUser(@RequestBody RoleToUserForm form) {
        User userSaved = userService.addRoleToUser(form.getUsername(), form.getRolename());
        return ResponseEntity.ok(assembler.toModel(userSaved));
    }

}

@Data
class RoleToUserForm {
    private String username;
    private String rolename;
}