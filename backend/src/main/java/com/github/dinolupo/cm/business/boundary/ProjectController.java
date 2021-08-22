package com.github.dinolupo.cm.business.boundary;

import com.github.dinolupo.cm.business.boundary.exception.ProjectNotFoundException;
import com.github.dinolupo.cm.business.entity.Project;
import com.github.dinolupo.cm.business.entity.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.*;
import org.springframework.hateoas.mediatype.problem.Problem;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.BeanUtils;

import javax.persistence.OptimisticLockException;
import java.util.Optional;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    @Autowired
    private ProjectRepository repository;

    @Autowired
    private ProjectModelAssembler assembler;

    @Autowired
    private PagedResourcesAssembler<Project> pagedAssembler;

    // page, size, and sort are available due to spring web support, they are converted into Pageable instance
    @GetMapping
    ResponseEntity<PagedModel<EntityModel<Project>>> all() {
        return filter(Pageable.unpaged());
    }

    @GetMapping(path = "/search")
    ResponseEntity<PagedModel<EntityModel<Project>>> filter(Pageable pageable) {
        var project = new Project();
        project.setArchived(false);
        var example = Example.of(project);
        return ResponseEntity.ok(pagedAssembler.toModel(repository.findAll(example, pageable), assembler));
    }

    @PostMapping
    ResponseEntity<EntityModel<Project>> newElement(@RequestBody Project newProject) {
        if (newProject.getStatus() == null) {
            newProject.setStatus(Project.Status.READY);
        }
        newProject.setArchived(false);
        var entityModel = assembler.toModel(repository.save(newProject));
        return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    @PutMapping("/{id}/archive")
    ResponseEntity<?> archive(@PathVariable Long id) {
        Optional<Project> optProject = repository.findById(id);

        if (optProject.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        var project = optProject.get();

        // if ok save
        if (project.getStatus() == Project.Status.CANCELLED || project.getStatus() == Project.Status.COMPLETED) {
            project.setArchived(true);
            return ResponseEntity.ok(assembler.toModel(repository.save(project)));
        }

        // else return an error
        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .header(HttpHeaders.CONTENT_TYPE, MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE)
                .body(Problem.create()
                        .withTitle("Method not allowed")
                        .withDetail("You can't archive a Project that is in the " + project.getStatus() + " status"));
    }

    @PutMapping("/{id}/unarchive")
    ResponseEntity<?> unarchive(@PathVariable Long id) {
        Optional<Project> optProject = repository.findById(id);

        if (optProject.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        var project = optProject.get();

        // if ok save
        if (project.getArchived()) {
            project.setArchived(false);
            return ResponseEntity.ok(assembler.toModel(repository.save(project)));
        }

        // else return an error
        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .header(HttpHeaders.CONTENT_TYPE, MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE)
                .body(Problem.create()
                        .withTitle("Method not allowed")
                        .withDetail("You can't unarchive a Project that is already online"));
    }

    @GetMapping("/{id}")
    ResponseEntity<EntityModel<Project>> one(@PathVariable Long id) {
        return ResponseEntity.ok(assembler.toModel(repository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException(id))));
    }

    @PutMapping("/{id}")
    ResponseEntity<EntityModel<Project>> replace(@RequestBody Project newElement, @PathVariable Long id) {

        Optional<Project> optProject = repository.findById(id);

        if (optProject.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        var current = optProject.get();
        // check versioning
        if (newElement.getVersion() == null || current.getVersion() != newElement.getVersion()) {
            String message = String.format("Version is null or different for project id=%d, current db version=%d, new version=%d"
                    , id
                    , current.getVersion()
                    , newElement.getVersion());
            throw new OptimisticLockException(message);
        }

        // id and archived attributes cannot be changed
        BeanUtils.copyProperties(newElement, current, "id", "archived");
        var ent = repository.save(current);
        var entityModel = assembler.toModel(ent);
        return ResponseEntity.ok(entityModel);

    }

    @DeleteMapping("/{id}")
    ResponseEntity<?> delete(@PathVariable Long id) {
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
