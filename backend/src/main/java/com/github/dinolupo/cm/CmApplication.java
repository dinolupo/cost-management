package com.github.dinolupo.cm;

import com.github.dinolupo.cm.security.control.UserService;
import com.github.dinolupo.cm.business.entity.*;
import com.github.dinolupo.cm.security.entity.Role;
import com.github.dinolupo.cm.security.entity.RoleRepository;
import com.github.dinolupo.cm.security.entity.User;
import com.github.dinolupo.cm.security.entity.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.filter.CommonsRequestLoggingFilter;
import org.springframework.web.filter.ForwardedHeaderFilter;

import java.time.LocalDate;
import java.util.ArrayList;

@SpringBootApplication
public class CmApplication {

	// forwarding header handling to manage hateoas correctly behind load balancers
	// https://docs.spring.io/spring-hateoas/docs/current/reference/html/
	@Bean
	ForwardedHeaderFilter forwardedHeaderFilter() {
		return new ForwardedHeaderFilter();
	}

	@Bean
	public CommonsRequestLoggingFilter requestLoggingFilter() {
		CommonsRequestLoggingFilter loggingFilter = new CommonsRequestLoggingFilter();
		loggingFilter.setIncludeClientInfo(true);
		loggingFilter.setIncludeQueryString(true);
		loggingFilter.setIncludePayload(true);
		loggingFilter.setIncludeHeaders(true);
		return loggingFilter;
	}

	@Bean
	CommandLineRunner run (RoleRepository roleRepo,
						   UserRepository userRepo,
						   UserService service,
						   ProjectRepository projRepo,
						   TaskRepository taskRepo) {
		return args -> {
			userRepo.deleteAll();
			roleRepo.deleteAll();

			roleRepo.save(new Role(null, "ROLE_USER"));
			roleRepo.save(new Role(null, "ROLE_ADMIN"));
			roleRepo.save(new Role(null, "ROLE_SUPER_ADMIN"));
			roleRepo.save(new Role(null, "ROLE_PROJECT_MANAGER"));
			service.save(new User(null,"dino","123456", "Dino", new ArrayList<>(),false));
			service.save(new User(null,"luca", "123456","Luca", new ArrayList<>(),false));
			service.save(new User(null,"fabio","123456","Fabio", new ArrayList<>(),false));
			service.addRoleToUser("dino", "ROLE_SUPER_ADMIN");
			service.addRoleToUser("dino", "ROLE_ADMIN");
			service.addRoleToUser("dino", "ROLE_USER");
			service.addRoleToUser("luca", "ROLE_ADMIN");
			service.addRoleToUser("fabio", "ROLE_PROJECT_MANAGER");

			projRepo.deleteAll();
			taskRepo.deleteAll();

			var p1 = projRepo.save(new Project(null, "Tesla Model S", "", 12_500.00, LocalDate.now(), LocalDate.now().plusMonths(5), "dino", "31v2", null, null, false));
			var p2 = projRepo.save(new Project(null, "Discovery", "", 412_666.00, LocalDate.now().plusMonths(1), LocalDate.now().plusMonths(8), "dino", "31v2", null, null, false));
			var p3 = projRepo.save(new Project(null, "Enterprise NCC 1701-D", "", 2_312_500.00, LocalDate.now().plusMonths(6), LocalDate.now().plusMonths(24), "dino", "31v2", null, null, false));
			var p4 = projRepo.save(new Project(null, "Ponte Stretto Messina", "", 51_500_988.00, LocalDate.now(), LocalDate.now().plusMonths(9), "dino", "31v2", null, null, false));
			var p5 = projRepo.save(new Project(null, "Mars Rover Opportunity", "", 433_500.00, LocalDate.now(), LocalDate.now().plusMonths(1), "dino", "31v2", null, null, false));

			taskRepo.save(new Task(null, "Analysys", null, 38_500.0, LocalDate.now(), LocalDate.now().plusMonths(8), p1.getId(), null));
			taskRepo.save(new Task(null, "Analysys", null, 99_500.0, LocalDate.now(), LocalDate.now().plusMonths(8), p2.getId(), null));
			taskRepo.save(new Task(null, "Analysys", null, 9_938_500.0, LocalDate.now(), LocalDate.now().plusMonths(8), p4.getId(), null));
			taskRepo.save(new Task(null, "Analysys", null, 358_500.0, LocalDate.now(), LocalDate.now().plusMonths(8), p5.getId(), null));
			taskRepo.save(new Task(null, "Analysys", null, 1_387_500.0, LocalDate.now(), LocalDate.now().plusMonths(8), p3.getId(), null));
			taskRepo.save(new Task(null, "Design", null, 3_784_500.0, LocalDate.now(), LocalDate.now().plusMonths(8), p3.getId(), null));
			taskRepo.save(new Task(null, "Design", null, 838_500.0, LocalDate.now(), LocalDate.now().plusMonths(8), p3.getId(), null));
			taskRepo.save(new Task(null, "Implementation", null, 138_500.0, LocalDate.now(), LocalDate.now().plusMonths(8), p3.getId(), null));
			taskRepo.save(new Task(null, "Test", null, 328_500.0, LocalDate.now(), LocalDate.now().plusMonths(8), p3.getId(), null));
			taskRepo.save(new Task(null, "Operation", null, 577_500.0, LocalDate.now(), LocalDate.now().plusMonths(8), p3.getId(), null));

		};
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}


	public static void main(String[] args) {
		SpringApplication.run(CmApplication.class, args);
	}

}
