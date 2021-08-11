package com.github.dinolupo.cm.business.project.boundary;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import com.github.dinolupo.cm.business.project.entity.Project;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class ProjectModelAssembler implements RepresentationModelAssembler<Project, EntityModel<Project>> {

    @Override
    public EntityModel<Project> toModel(Project entity) {
        return EntityModel.of(entity,
                linkTo(methodOn(ProjectController.class).one(entity.getId())).withSelfRel(),
                linkTo(methodOn(ProjectController.class).all(null)).withRel("projects"));
    }
}
