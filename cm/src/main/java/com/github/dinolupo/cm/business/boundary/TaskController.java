package com.github.dinolupo.cm.business.boundary;

import com.github.dinolupo.cm.business.boundary.exception.ProjectNotFoundException;
import com.github.dinolupo.cm.business.boundary.exception.TaskNotFoundException;
import com.github.dinolupo.cm.business.entity.ProjectRepository;
import com.github.dinolupo.cm.business.entity.Task;
import com.github.dinolupo.cm.business.entity.TaskRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.OptimisticLockException;

@RestController
@RequestMapping("/api/projects/{projectId}/tasks")
public class TaskController {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TaskRepository repository;

    @Autowired
    private TaskModelAssembler assembler;

    @Autowired
    private PagedResourcesAssembler<Task> pagedAssembler;

    @GetMapping
    ResponseEntity<?> all(@PathVariable Long projectId) {
        return filter(projectId, Pageable.unpaged());
    }

    @GetMapping(path = "/search")
    ResponseEntity<?> filter(@PathVariable Long projectId, Pageable pageable) {
        return ResponseEntity.ok(pagedAssembler.toModel(repository.findAll(pageable), assembler));
    }

    @GetMapping("/{id}")
    ResponseEntity<EntityModel<Task>> one(@PathVariable Long projectId, @PathVariable Long id) {
        var task = new Task();
        task.setProjectId(projectId);
        task.setId(id);
        var example = Example.of(task);
        return ResponseEntity.ok(assembler.toModel(
                repository.findOne(example).orElseThrow(() -> new TaskNotFoundException(projectId, id))));
    }

    @PostMapping
    ResponseEntity<?> newElement(@PathVariable Long projectId, @RequestBody Task newTask) {
        projectRepository.findById(projectId).orElseThrow(
                () -> new ProjectNotFoundException(projectId));

        newTask.setProjectId(projectId);
        var entityModel = assembler.toModel(repository.save(newTask));
        return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    @PutMapping("/{id}")
    ResponseEntity<?> replace(@PathVariable Long projectId, @PathVariable Long id, @RequestBody Task newElement) {
        var task = new Task();
        task.setProjectId(projectId);
        task.setId(id);
        var example = Example.of(task);
        var current = repository.findOne(example).orElseThrow(() -> new TaskNotFoundException(projectId, id));

        if (newElement.getVersion() == null || current.getVersion() != newElement.getVersion()) {
            String message = String.format("Version is null or different for task id=%d, current db version=%d, new version=%d"
                    , id
                    , current.getVersion()
                    , newElement.getVersion());
            throw new OptimisticLockException(message);
        }
        BeanUtils.copyProperties(newElement, current, "id", "projectId");
        var ent = repository.save(current);
        var entityModel = assembler.toModel(ent);
        return ResponseEntity.ok(entityModel);
    }

    @DeleteMapping
    ResponseEntity<?> delete(@PathVariable Long projectId, @PathVariable Long id) {
        projectRepository.findById(projectId).orElseThrow(
                () -> new ProjectNotFoundException(projectId));
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
