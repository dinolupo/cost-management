package com.github.dinolupo.cm.security.boundary;

import com.github.dinolupo.cm.security.entity.Role;
import com.github.dinolupo.cm.security.entity.RoleRepository;
import com.github.dinolupo.cm.exception.ElementNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    @Autowired
    RoleRepository repository;

    @Autowired
    PagedResourcesAssembler<Role> pagedAssembler;
    private RoleModelAssembler assembler;

    // page, size, and sort are available due to spring web support, they are converted into Pageable instance
    @GetMapping
    ResponseEntity<?> all() {
        return filter(Pageable.unpaged());
    }

    @GetMapping(path = "/search")
    ResponseEntity<?> filter(Pageable pageable) {
        return ResponseEntity.ok(pagedAssembler.toModel(repository.findAll(pageable), assembler));
    }

    @PostMapping
    ResponseEntity<?> newElement(@RequestBody Role role) {
        var entityModel = assembler.toModel(repository.save(role));
        return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(entityModel);
    }

    @GetMapping("/{id}")
    ResponseEntity<?> one(@PathVariable Long id) {
        return ResponseEntity.ok(assembler.toModel(repository.findById(id).orElseThrow(
                ()->new ElementNotFoundException(Role.class, id))));
    }

    @DeleteMapping("/{id}")
    ResponseEntity<?> delete(@PathVariable Long id) {
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
