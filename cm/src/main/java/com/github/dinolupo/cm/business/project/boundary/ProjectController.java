package com.github.dinolupo.cm.business.project.boundary;

import com.github.dinolupo.cm.business.project.entity.Project;
import com.github.dinolupo.cm.business.project.entity.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.BeanUtils;

import java.util.Optional;

@RestController
public class ProjectController {

    @Autowired
    private ProjectRepository repository;

    @Autowired
    private ProjectModelAssembler assembler;

    @Autowired
    private PagedResourcesAssembler<Project> pagedAssembler;

    // page, size, and sort are available due to spring web support, they are converted into Pageable instance
    @GetMapping(path = "/projects")
    ResponseEntity<PagedModel<EntityModel<Project>>> all(Pageable pageable) {
        return ResponseEntity.ok(pagedAssembler.toModel(repository.findAll(pageable), assembler));
    }

    @PostMapping(path = "/projects")
    ResponseEntity<EntityModel<Project>> newElement(@RequestBody Project newProject) {
        var entityModel = assembler.toModel(repository.save(newProject));
        return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    @GetMapping("/projects/{id}")
    ResponseEntity<EntityModel<Project>> one(@PathVariable Long id) {
        return ResponseEntity.ok(assembler.toModel(repository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException(id))));
    }

    @PutMapping("/projects/{id}")
    ResponseEntity<EntityModel<Project>> replace(@RequestBody Project newElement, @PathVariable Long id) {

        Optional<Project> optProject = repository.findById(id);
        if (optProject.isPresent()) { // found, updating entity
            var current = optProject.get();
            BeanUtils.copyProperties(newElement, current, "id");
            var ent = repository.save(current);
            var entityModel = assembler.toModel(ent);
            return ResponseEntity.ok(entityModel);
        } else { // not found, creating new entity with id
            newElement.setId(id);
            var ent = repository.save(newElement);
            var entityModel = assembler.toModel(ent);
            return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                    .body(entityModel);
        }
    }

    @DeleteMapping("/projects/{id}")
    ResponseEntity<?> delete(@PathVariable Long id) {
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
