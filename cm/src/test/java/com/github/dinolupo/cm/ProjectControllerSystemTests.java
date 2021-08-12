package com.github.dinolupo.cm;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dinolupo.cm.business.project.boundary.ProjectController;
import com.github.dinolupo.cm.business.project.entity.Project;
import com.github.dinolupo.cm.business.project.entity.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@WebMvcTest(ProjectController.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ProjectControllerSystemTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ProjectRepository repository;

    @Test
    public void shouldRaiseExceptionOnPutWithDifferentVersion() throws Exception {

        // first object saved (version 1)
        var project = new Project();
        project.setName("name");
        project.setDescription("description");
        project.setOwner("dino");
        project.setBudget(Double.valueOf(15000));
        var saved = repository.save(project);

        // some other client save again (version 2)
        saved.setDescription("revisioned budget");
        saved.setBudget(Double.valueOf(10000));
        assertThat(saved.getVersion()).isEqualTo(1);
        var revisioned = repository.save(saved); // save again will change version
        assertThat(revisioned.getVersion()).isEqualTo(2);

        // PUT endpoint should raise exception if a client save again the first object
        this.mockMvc.perform(put("/projects/"+saved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(saved)))
                .andExpect(status().isPreconditionFailed());
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
