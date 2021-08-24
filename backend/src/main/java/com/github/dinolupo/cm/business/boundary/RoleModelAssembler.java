package com.github.dinolupo.cm.business.boundary;

import com.github.dinolupo.cm.business.entity.Role;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class RoleModelAssembler implements RepresentationModelAssembler<Role, EntityModel<Role>> {

    @Override
    public EntityModel<Role> toModel(Role entity) {
        var model = EntityModel.of(entity,
                linkTo(methodOn(RoleController.class).one(entity.getId())).withSelfRel(),
                linkTo(methodOn(RoleController.class).all()).withRel("roles"));
        return model;
    }
}
