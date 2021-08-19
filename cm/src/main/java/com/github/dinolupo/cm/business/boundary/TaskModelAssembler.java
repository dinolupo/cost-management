package com.github.dinolupo.cm.business.boundary;

import com.github.dinolupo.cm.business.entity.Task;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class TaskModelAssembler implements RepresentationModelAssembler<Task, EntityModel<Task>> {
    @Override
    public EntityModel<Task> toModel(Task entity) {
        return EntityModel.of(entity,
                linkTo(methodOn(TaskController.class).one(entity.getProjectId(), entity.getId())).withSelfRel());
    }
}
