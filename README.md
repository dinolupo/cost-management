# Cost Management

Cost Management is a web application that I implemented to update my programming skills.

## Directories
> tree -L 1 -d
```
.
├── docker      # dockerfile
├── backend     # spring boot application
└── webapp      # web frontend
```

### docker

Start local MariaDB for development with:

```bash
docker compose -f compose.yml up
```

### backend

Backend developed with

- Java 11 LTS + Spring Boot
- RestFul services with HATEOAS (level 3 Richardson maturity model)
- Single Sign On with OpenID Connect providers (OAuth2)
- Microprofiling (local user and roles on DB)
- Stateless and JWT authentication/authorization
- Spring Rest Docs with Asciidoc
- ...

*Java/Maven versions with SDKMAN:*

```bash
$ sdk current

Using:

java: 11.0.10-zulu
maven: 3.8.1
```

Debug with:

```bash
./mvnw spring-boot:run -Dagentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8000
```


### webapp

- TBD

## References

I used the following resources:

> great tutorial on REST with Spring

https://spring.io/guides/tutorials/rest/

> motivation about exposing Version for Optimistic lock into the representation
> Not convinced by the Oliver's response, Johan is right because I need the version in
> my collection GET API

https://stackoverflow.com/questions/36853343/with-spring-data-rest-why-is-the-version-property-becoming-an-etag-and-not-inc

> interesting ETag article, discarded the solution because I needed Version for Collection endpoint

https://www.novatec-gmbh.de/en/blog/managing-concurrency-in-a-distributed-restful-environment-with-spring-boot-and-angular2/

> Include Auditing for Entities

https://springbootdev.com/2018/03/13/spring-data-jpa-auditing-with-createdby-createddate-lastmodifiedby-and-lastmodifieddate/

> Paging and Sorting links

https://docs.spring.io/spring-data/rest/docs/current/reference/html/#repository-resources.search-resource

> Sample WebMvcTest with json assertion 

https://howtodoinjava.com/spring-boot2/testing/spring-boot-mockmvc-example/

> testing with spring

https://spring.io/guides/gs/testing-web/

https://howtodoinjava.com/spring-boot2/testing/spring-boot-mockmvc-example/

> openapi vs asciidoc

https://www.baeldung.com/spring-rest-docs-vs-openapi

> Spring HAL Explorer include spring data rest (not wanted)
> So we will include only libraries as static content

https://stackoverflow.com/questions/40360425/using-hal-browser-with-spring-hateoas-without-spring-data-rest

> Prefix only in rest controller, check the not accepted question, it is better
> I included the variable in the properties and referencing it in the controllers

https://stackoverflow.com/questions/28006501/how-to-specify-prefix-for-all-controllers-in-spring-boot

> Rest Docs

https://docs.spring.io/spring-restdocs/docs/current/reference/html5/#configuration-snippet-template-format

> Security and Testing

https://developer.okta.com/blog/2021/05/19/spring-security-testing

> Managing Google + HTTP Basic Authentication together

https://www.codejava.net/frameworks/spring-boot/oauth2-login-with-google-example

> video on Spring Security and JWT

https://www.youtube.com/watch?v=VVn9OG9nfH0

> Authentication STATELESS with JWT

https://www.toptal.com/spring/spring-security-tutorial

> Spring Security, how it should be, with spring examples

https://blog.jdriven.com/2019/10/spring-security-5-2-oauth-2-exploration-part1/

https://blog.jdriven.com/2019/10/spring-security-5-2-oauth-2-exploration-part2/









