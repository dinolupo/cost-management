package com.github.dinolupo.cm;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dinolupo.cm.business.project.boundary.ProjectController;
import javassist.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import com.github.dinolupo.cm.business.project.entity.Project;
import com.github.dinolupo.cm.business.project.entity.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//@WebMvcTest(ProjectController.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ProjectControllerSystemTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ProjectRepository repository;

    @BeforeEach
    public void beforeEach() {
        repository.deleteAll();
    }

    @Test
    public void shouldRaiseExceptionOnPutWithDifferentVersion() throws Exception {

        // first object saved (version 1)
        var project = new Project();
        project.setName("name");
        project.setDescription("description");
        project.setOwner("dino");
        project.setBudget(15_000.0);
        var saved = repository.save(project);

        // some other client save again (version 2)
        saved.setDescription("revisioned budget");
        saved.setBudget(10000.0);
        assertThat(saved.getVersion()).isEqualTo(0);
        var revisioned = repository.save(saved); // save again will change version
        assertThat(revisioned.getVersion()).isEqualTo(1);

        // PUT endpoint should raise exception if a client save again the first object
        this.mockMvc.perform(put("/projects/"+saved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(saved)))
                .andExpect(status().isPreconditionFailed());
    }

    @Test
    public void archiveAndUnarchive() throws Exception {
        // can Archive id CANCELLED
        var project = new Project();
        project.setName("name");
        project.setBudget(200.0);
        project.setStatus(Project.Status.CANCELLED);
        project = repository.save(project);
        assertThat(project.getStatus()).isEqualTo(Project.Status.CANCELLED);
        this.mockMvc.perform(put("/projects/" + project.getId() + "/archive"))
                .andExpect(status().isOk());

        // cannot ARCHIVE IF READY
        project = repository.findById(project.getId()).orElseThrow(NoSuchElementException::new);
        assertThat(project.getArchived()).isEqualTo(true);
        project.setArchived(false);
        project.setStatus(Project.Status.READY);
        project = repository.save(project);
        this.mockMvc.perform(put("/projects/" + project.getId() + "/archive"))
                .andExpect(status().isMethodNotAllowed());

        // can ARCHIVE if COMPLETED
        project = repository.findById(project.getId()).orElseThrow(NoSuchElementException::new);
        project.setStatus(Project.Status.COMPLETED);
        project = repository.save(project);
        this.mockMvc.perform(put("/projects/" + project.getId() + "/archive"))
                .andExpect(status().isOk());

        // test unarchive
        this.mockMvc.perform(put("/projects/" + project.getId() + "/unarchive"))
                .andExpect(status().isOk());

        // cannot unarchive if already archived
        this.mockMvc.perform(put("/projects/" + project.getId() + "/unarchive"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(content().contentType(MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE))
                .andExpect(content().json("{\"title\":\"Method not allowed\",\"detail\":\"You can't unarchive a Project that is already online\"}"));

    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
