[![Java CI](https://github.com/sgzachesov/spring-data-jpa-specification-builder/actions/workflows/ci.yml/badge.svg)](https://github.com/sgzachesov/spring-data-jpa-specification-builder/actions/workflows/ci.yml "CI")
[![Maven Central](https://img.shields.io/maven-central/v/io.github.sgzachesov/spring-data-jpa-specification-builder.svg)](https://search.maven.org/artifact/io.github.sgzachesov/spring-data-jpa-specification-builder/ "Maven Central")
[![License: Apache 2.0](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://github.com/sgzachesov/spring-data-jpa-specification-builder/blob/main/LICENSE "Apache License 2.0")
[![javadoc](https://javadoc.io/badge2/io.github.sgzachesov/spring-data-jpa-specification-builder/javadoc.svg)](https://javadoc.io/doc/io.github.sgzachesov/spring-data-jpa-specification-builder "javadoc")

# Spring Data JPA Specification Builder

Spring Data JPA [Specification](https://docs.spring.io/spring-data/jpa/reference/jpa/specifications.html) provides an
API for writing dynamic queries based on the JPA Criteria API. Creating queries using the Criteria API is quite
labor-intensive.

The architecture
of [Spring Data JPA Specification Builder](https://github.com/sgzachesov/spring-data-jpa-specification-builder) is based
on the [Facade pattern](https://en.wikipedia.org/wiki/Facade_pattern), which hides the JPA Criteria API. It provides an
API based on the [Builder pattern](https://en.wikipedia.org/wiki/Builder_pattern) - `SpecificationBuilder`.

With `SpecificationBuilder`, you can create common business queries, including those using `join`.

# Quick Start

1. Select the correct version from the [compatibility matrix](#compatibility-matrix)
2. Add the dependency:

   **Maven:**
   ```xml
   <dependency>
       <groupId>io.github.sgzachesov</groupId>
       <artifactId>spring-data-jpa-specification-builder</artifactId>
       <version>{version}</version>
   </dependency>
   ```

   **Gradle:**
   ```gradle
   implementation 'io.github.sgzachesov:spring-data-jpa-specification-builder:{version}'
   ```

# Usage

## Building Basic Queries

1. Fulfill the Spring Data JPA [specification](https://docs.spring.io/spring-data/jpa/reference/jpa/specifications.html)
   requirements.
2. Create the specification itself using `SpecificationBuilder`. The query itself is similar to the SQL syntax.

```java
    Specification<User> spec =
        SpecificationBuilder.<User>builder()
                .equal(User_.IS_ACTIVE, true)
                .like(User_.USERNAME, "mail", p -> p.wildcard(Wildcard.MULTIPLE))
                .between(User_.REGISTRATION_DATE, LocalDate.of(2026, 1, 1), LocalDate.of(2026, 2, 1))
                .in(User_.PHONE, List.of("+777777", "+888888"))
                .build();
```

3. Pass the `Specification` as a parameter to the repository method `userRepository.findAll(spec);`

## Building Join Queries

`SpecificationBuilder` supports queries with joins.

```java
      Specification<User> spec =
        SpecificationBuilder.<User>builder()
                .equal(List.of(User_.POSTS, Post_.TAGS), "Tag")
                .equal(List.of(User_.PROFILE, Profile_.GROUP, Group_.NAME), "Admin")
                .in("posts.tags", "Tag")
                .build();
```

## Additional Query Features

Using the third parameter of the methods, you can configure the query with both general and specific options.

```java
      Specification<User> spec =
        SpecificationBuilder.<User>builder()
                .equal(User_.IS_ACTIVE, true, p -> p.connection(LogicalOperator.OR))
                .like(User_.USERNAME, "mail", p -> p.wildcard(Wildcard.MULTIPLE))
                .build();
```

# Compatibility matrix

| [Spring Data JPA](https://github.com/spring-projects/spring-data-jpa) version | [Spring Data JPA Specification Builder](https://github.com/sgzachesov/spring-data-jpa-specification-builder) version                                                                                                                           |
|-------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| 4.0.1+                                                                        | [![Maven Central](https://img.shields.io/maven-central/v/io.github.sgzachesov/spring-data-jpa-specification-builder/4.0.2.svg)](https://search.maven.org/artifact/io.github.sgzachesov/spring-data-jpa-specification-builder/ "Maven Central") |
| 3.5.8                                                                         | [![Maven Central](https://img.shields.io/maven-central/v/io.github.sgzachesov/spring-data-jpa-specification-builder/3.5.8.svg)](https://search.maven.org/artifact/io.github.sgzachesov/spring-data-jpa-specification-builder/ "Maven Central") |
