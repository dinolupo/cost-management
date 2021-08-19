package com.github.dinolupo.cm.business.boundary;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import com.github.dinolupo.cm.business.entity.Project;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class ProjectModelAssembler implements RepresentationModelAssembler<Project, EntityModel<Project>> {

    @Override
    public EntityModel<Project> toModel(Project entity) {

        // Unconditional links to single-item resource and aggregate root

        EntityModel<Project> model = EntityModel.of(entity,
                linkTo(methodOn(ProjectController.class).one(entity.getId())).withSelfRel(),
                linkTo(methodOn(ProjectController.class).all()).withRel("projects"));

        // Conditional links based on state of the project

        if ((entity.getStatus() == Project.Status.CANCELLED || entity.getStatus() == Project.Status.COMPLETED)
                && !entity.getArchived()) {
            model.add(linkTo(methodOn(ProjectController.class).archive(entity.getId())).withRel("archive"));
        } else if (entity.getArchived()) {
            model.add(linkTo(methodOn(ProjectController.class).unarchive(entity.getId())).withRel("unarchive"));
        }

        return model;
    }

}
