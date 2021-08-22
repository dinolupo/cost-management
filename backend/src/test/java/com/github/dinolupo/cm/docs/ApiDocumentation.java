package com.github.dinolupo.cm.docs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dinolupo.cm.business.entity.Project;
import com.github.dinolupo.cm.business.entity.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.hateoas.MediaTypes;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.RequestDispatcher;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
public class ApiDocumentation {

    @Autowired
    ProjectRepository repository;

    private MockMvc mockMvc;
    // handler for documentation
    private RestDocumentationResultHandler docHandler;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private Environment env;

    private String api;

    @BeforeEach
    public void setUp(WebApplicationContext webApplicationContext,
                      RestDocumentationContextProvider restDocumentation) {

        api = env.getProperty("api.prefix");

        docHandler = document("{method-name}",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()));

        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .alwaysDo(docHandler)
                .build();
    }

    @Test
    public void headersExample() throws Exception {
        this.mockMvc
                .perform(get(api+"/projects"))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(docHandler.document(responseHeaders(
                        headerWithName("Content-Type")
                                .description("The Content-Type of the payload, e.g. `application/hal+json`"))));
    }

    @Test
    public void errorExample() throws Exception {
        this.mockMvc
                .perform(get("/error")
                        .requestAttr(RequestDispatcher.ERROR_STATUS_CODE, 400)
                        .requestAttr(RequestDispatcher.ERROR_REQUEST_URI, "/project/555")
                        .requestAttr(RequestDispatcher.ERROR_MESSAGE, "The tag 'http://localhost:8080/projects/123' does not exist"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("error").value("Bad Request"))
                .andExpect(jsonPath("timestamp").isNotEmpty())
                .andExpect(jsonPath("status").value(400))
                .andExpect(jsonPath("path").isNotEmpty())
                .andDo(docHandler.document(responseFields(
                        fieldWithPath("error").description("The HTTP error that occurred, e.g. `Bad Request`"),
                        //fieldWithPath("message").description("[Optional] - A description of the cause of the error"),
                        fieldWithPath("path").description("The path to which the request was made"),
                        fieldWithPath("status").description("The HTTP status code, e.g. `400`"),
                        fieldWithPath("timestamp").description("The time, in milliseconds, at which the error occurred"))));
    }

    @Test
    public void indexExample() throws Exception {
        this.mockMvc.perform(get(api+"/"))
                .andExpect(status().isOk())
                .andDo(docHandler.document(
                        links(
                                linkWithRel("projects").description("The <<resources_projects,Projects resource>>")
                                //linkWithRel("tasks").description("The <<resources_tasks,Tasks resource>>")
                        ),
                        responseFields(
                                subsectionWithPath("_links").description("<<resources_index_access_links,Links>> to other resources"))));
    }


    @Test
    public void projectsListExample() throws Exception {
        repository.deleteAll();
        var projectNames = new String[]{"Consultancy trim I", "ORC Mananagement", "Diablo 4 - the game"};
        for (int i = 0; i < projectNames.length; i++) {
            createProject(projectNames[i],
                    "",
                    20_000.0 * (i + 1),
                    LocalDate.now().plusMonths(i),
                    LocalDate.now().plusMonths(i + 3),
                    "luca",
                    "",
                    Project.Status.READY,
                    false);
        }
        this.mockMvc
                .perform(get(api+"/projects").accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("_links.self").isNotEmpty())
                .andDo(docHandler.document(
                        responseFields(
                                subsectionWithPath("_embedded.projects").description("An array of <<resources_projects, Project resources>>"),
                                subsectionWithPath("_links").description("Operations available"),
                                subsectionWithPath("page").description("Page details"))));
    }

    @Test
    public void projectsCreateExample() throws Exception {

        Map<String, Object> project = new HashMap<String, Object>();
        project.put("name", "Enterprise NCC-1701");
        project.put("description", "Where no one has gone before");
        project.put("budget", "15000000");
        project.put("startDate", "2018-01-01");
        project.put("endDate", "2022-12-01");
        project.put("owner", "Christopher Benjamin Pike");
        project.put("estimation", "198v1");
        project.put("status", "READY");

        //ConstrainedFields fields = new ConstrainedFields(Project.class);

        this.mockMvc
                .perform(post(api+"/projects")
                        .contentType(MediaTypes.HAL_JSON)
                        .content(this.objectMapper.writeValueAsString(project)))
                .andExpect(
                        status().isCreated())
                .andDo(docHandler.document(
                        requestFields(
                                fieldWithPath("name").description("Title"),
                                fieldWithPath("description").description("Description"),
                                fieldWithPath("budget").description("Budget").type(Double.TYPE),
                                fieldWithPath("startDate").type(LocalDate.class.getTypeName())
                                        .description("Start Date. This will be adapted based on the tasks start dates. YYYY-MM-DD"),
                                fieldWithPath("endDate").type(LocalDate.class.getTypeName())
                                        .description("End Date. This will be adapted based on the tasks end dates. YYYY-MM-DD"),
                                fieldWithPath("owner").description("Owner"),
                                fieldWithPath("estimation").description("Estimation tool reference"),
                                fieldWithPath("status").type(Project.Status.class.getTypeName())
                                        .description("One of the following values:\n"
                                                + String.join(", ",
                                                Arrays.stream(Project.Status.values())
                                                        .map(v -> v.name())
                                                        .collect(Collectors.toUnmodifiableList()))
                                                + "\ndefaults to READY"
                                        )
                        )
                ));

    }

    private void createProject(String name,
                               String description,
                               Double budget,
                               LocalDate startDate,
                               LocalDate endDate,
                               String owner,
                               String estimation,
                               Project.Status status,
                               Boolean archived) {
        var project = new Project(name, description, budget, startDate, endDate, owner, estimation, status, archived);
        repository.save(project);
    }

}
