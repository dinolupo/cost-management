package com.github.dinolupo.cm.business.boundary;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.mvc.BasicLinkBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/")
public class IndexController {

    @GetMapping
    public RepresentationModel index() {
        RepresentationModel index = new RepresentationModel();

        String baseUri = BasicLinkBuilder.linkToCurrentMapping().toString();
        var link = Link.of(baseUri + "/projects/search{&sort,page,size}");
        index.add(link.withRel("projects").withName("search"));

        return index;
    }

}
