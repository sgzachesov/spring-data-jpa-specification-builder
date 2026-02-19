/*
 * Copyright 2025-present Sergei Zachesov and others.
 * https://github.com/sergei-zachesov/spring-data-jpa-specification-builder
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.szachesov.specification.builder;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import com.cosium.spring.data.jpa.entity.graph.domain2.DynamicEntityGraph;
import com.cosium.spring.data.jpa.entity.graph.domain2.EntityGraph;
import io.github.szachesov.specification.builder.sample.entity.Post;
import io.github.szachesov.specification.builder.sample.entity.Post_;
import io.github.szachesov.specification.builder.sample.entity.User;
import io.github.szachesov.specification.builder.sample.entity.User_;
import io.github.szachesov.specification.builder.testutils.DbUtils;
import io.github.szachesov.specification.builder.testutils.TestConstants;
import io.github.szachesov.specification.builder.testutils.TestData;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

class ComparisonSpecificationBuilderTest extends SpecificationBuilderTest {

  @Test
  void min_getAll_byNullValue() {
    final Specification<Post> spec =
        SpecificationBuilder.<Post>builder().min(Post_.WORD_COUNT, null).build();

    final List<Post> entities = postRepository.findAll(spec);

    assertThat(entities).hasSize(TestData.POSTS.size());
  }

  @Test
  void min_getResult_byExclusiveInteger() {
    final int value = TestConstants.WORD_COUNT_POST_1_USER_1;
    final Specification<Post> spec =
        SpecificationBuilder.<Post>builder()
            .min(Post_.WORD_COUNT, value, b -> b.minBound(Bound.EXCLUSIVE))
            .build();

    final List<Post> entities = postRepository.findAll(spec);

    assertThat(entities)
        .isNotEmpty()
        .extracting(Post::getWordCount)
        .allSatisfy(v -> assertThat(v).isGreaterThan(value));
  }

  @Test
  void min_getResult_byInclusiveInteger() {
    final int value = TestConstants.WORD_COUNT_POST_1_USER_1;
    final Specification<Post> spec =
        SpecificationBuilder.<Post>builder()
            .min(Post_.WORD_COUNT, value, b -> b.minBound(Bound.INCLUSIVE))
            .build();

    final List<Post> entities = postRepository.findAll(spec);

    assertThat(entities)
        .isNotEmpty()
        .extracting(Post::getWordCount)
        .allSatisfy(v -> assertThat(v).isGreaterThanOrEqualTo(value));
  }

  @Test
  void min_getResult_byJoinLocalDate() {
    final LocalDate value = TestConstants.USER_2_REGISTRATION_DATE;
    final Specification<Post> spec =
        SpecificationBuilder.<Post>builder()
            .min(List.of(Post_.AUTHOR, User_.REGISTRATION_DATE), value)
            .build();

    final List<String> attributePaths = List.of(DbUtils.joinPath(Post_.AUTHOR, User_.PROFILE));
    final EntityGraph eg = DynamicEntityGraph.loading(attributePaths);
    final List<Post> entities = postRepository.findAll(spec, eg);

    assertThat(entities)
        .isNotEmpty()
        .extracting(Post::getAuthor)
        .extracting(User::getRegistrationDate)
        .allSatisfy(v -> assertThat(v).isAfterOrEqualTo(value));
  }

  @Test
  void min_getResult_byJoinExclusiveLocalDate() {
    final LocalDate value = TestConstants.USER_1_REGISTRATION_DATE;
    final Specification<Post> spec =
        SpecificationBuilder.<Post>builder()
            .min(
                List.of(Post_.AUTHOR, User_.REGISTRATION_DATE),
                value,
                b -> b.minBound(Bound.EXCLUSIVE))
            .build();

    final List<String> attributePaths = List.of(DbUtils.joinPath(Post_.AUTHOR, User_.PROFILE));
    final EntityGraph eg = DynamicEntityGraph.loading(attributePaths);
    final List<Post> entities = postRepository.findAll(spec, eg);

    assertThat(entities)
        .isNotEmpty()
        .extracting(Post::getAuthor)
        .extracting(User::getRegistrationDate)
        .allSatisfy(v -> assertThat(v).isAfter(value));
  }

  @Test
  void max_getAll_byNullValue() {
    final Specification<Post> spec =
        SpecificationBuilder.<Post>builder().max(Post_.WORD_COUNT, null).build();

    final List<Post> entities = postRepository.findAll(spec);

    assertThat(entities).hasSize(TestData.POSTS.size());
  }

  @Test
  void max_getResult_byExclusiveInteger() {
    final int value = TestConstants.WORD_COUNT_POST_1_USER_2;
    final Specification<Post> spec =
        SpecificationBuilder.<Post>builder()
            .max(Post_.WORD_COUNT, value, b -> b.maxBound(Bound.EXCLUSIVE))
            .build();

    final List<Post> entities = postRepository.findAll(spec);

    assertThat(entities)
        .isNotEmpty()
        .extracting(Post::getWordCount)
        .allSatisfy(v -> assertThat(v).isLessThan(value));
  }

  @Test
  void max_getResult_byInclusiveInteger() {
    final int value = TestConstants.WORD_COUNT_POST_1_USER_2;
    final Specification<Post> spec =
        SpecificationBuilder.<Post>builder()
            .max(Post_.WORD_COUNT, value, b -> b.maxBound(Bound.INCLUSIVE))
            .build();

    final List<Post> entities = postRepository.findAll(spec);

    assertThat(entities)
        .isNotEmpty()
        .extracting(Post::getWordCount)
        .allSatisfy(v -> assertThat(v).isLessThanOrEqualTo(value));
  }

  @Test
  void max_getResult_byJoinLocalDate() {
    final LocalDate value = TestConstants.USER_1_REGISTRATION_DATE;
    final Specification<Post> spec =
        SpecificationBuilder.<Post>builder()
            .max(List.of(Post_.AUTHOR, User_.REGISTRATION_DATE), value)
            .build();

    final List<String> attributePaths = List.of(DbUtils.joinPath(Post_.AUTHOR, User_.PROFILE));
    final EntityGraph eg = DynamicEntityGraph.loading(attributePaths);
    final List<Post> entities = postRepository.findAll(spec, eg);

    assertThat(entities)
        .isNotEmpty()
        .extracting(Post::getAuthor)
        .extracting(User::getRegistrationDate)
        .allSatisfy(v -> assertThat(v).isBeforeOrEqualTo(value));
  }

  @Test
  void max_getResult_byJoinExclusiveLocalDate() {
    final LocalDate value = TestConstants.USER_2_REGISTRATION_DATE;
    final Specification<Post> spec =
        SpecificationBuilder.<Post>builder()
            .max(
                List.of(Post_.AUTHOR, User_.REGISTRATION_DATE),
                value,
                b -> b.maxBound(Bound.EXCLUSIVE))
            .build();

    final List<String> attributePaths = List.of(DbUtils.joinPath(Post_.AUTHOR, User_.PROFILE));
    final EntityGraph eg = DynamicEntityGraph.loading(attributePaths);
    final List<Post> entities = postRepository.findAll(spec, eg);

    assertThat(entities)
        .isNotEmpty()
        .extracting(Post::getAuthor)
        .extracting(User::getRegistrationDate)
        .allSatisfy(v -> assertThat(v).isBefore(value));
  }

  @Test
  void between_getAll_byNullValue() {
    final Specification<Post> spec =
        SpecificationBuilder.<Post>builder().between(Post_.WORD_COUNT, null, null).build();

    final List<Post> entities = postRepository.findAll(spec);

    assertThat(entities).hasSize(TestData.POSTS.size());
  }

  @Test
  void between_getResult_byLocalDateTime() {
    final LocalDateTime min = TestConstants.CREATE_AT_POST_1_USER_2;
    final LocalDateTime max = TestConstants.CREATE_AT_POST_1_USER_2.plusDays(1);
    final Specification<Post> spec =
        SpecificationBuilder.<Post>builder().between(Post_.CREATED_AT, min, max).build();

    final List<Post> entities = postRepository.findAll(spec);

    assertThat(entities)
        .isNotEmpty()
        .extracting(Post::getCreatedAt)
        .allSatisfy(v -> assertThat(v).isBetween(min, max));
  }

  @Test
  void between_getResult_byMinExclusiveLocalDateTime() {
    final LocalDateTime min = TestConstants.CREATE_AT_POST_1_USER_1;
    final LocalDateTime max = TestConstants.CREATE_AT_POST_2_USER_1;
    final Specification<Post> spec =
        SpecificationBuilder.<Post>builder()
            .between(Post_.CREATED_AT, min, max, b -> b.minBound(Bound.EXCLUSIVE))
            .build();

    final List<Post> entities = postRepository.findAll(spec);

    assertThat(entities)
        .isNotEmpty()
        .extracting(Post::getCreatedAt)
        .allSatisfy(v -> assertThat(v).isAfter(min).isBeforeOrEqualTo(max));
  }

  @Test
  void between_getResult_byMinMaxExclusive() {
    final LocalDateTime min = TestConstants.CREATE_AT_POST_1_USER_1;
    final LocalDateTime max = TestConstants.CREATE_AT_POST_2_USER_1;
    final Specification<Post> spec =
        SpecificationBuilder.<Post>builder()
            .between(
                Post_.CREATED_AT,
                min,
                max,
                b -> b.minBound(Bound.EXCLUSIVE).maxBound(Bound.EXCLUSIVE))
            .build();

    final List<Post> entities = postRepository.findAll(spec);

    assertThat(entities)
        .isNotEmpty()
        .extracting(Post::getCreatedAt)
        .allSatisfy(v -> assertThat(v).isAfterOrEqualTo(min).isBeforeOrEqualTo(max));
  }

  @Test
  void between_getResult_byJoinLocalDate() {
    final LocalDate min = TestConstants.USER_1_REGISTRATION_DATE;
    final LocalDate max = TestConstants.USER_2_REGISTRATION_DATE;
    final List<String> columns = List.of(Post_.AUTHOR, User_.REGISTRATION_DATE);
    final Specification<Post> spec =
        SpecificationBuilder.<Post>builder().between(columns, min, max).build();

    final List<String> attributePaths = List.of(DbUtils.joinPath(Post_.AUTHOR, User_.PROFILE));
    final EntityGraph eg = DynamicEntityGraph.loading(attributePaths);
    final List<Post> entities = postRepository.findAll(spec, eg);

    assertThat(entities)
        .isNotEmpty()
        .extracting(Post::getAuthor)
        .extracting(User::getRegistrationDate)
        .allSatisfy(v -> assertThat(v).isAfterOrEqualTo(min).isBeforeOrEqualTo(max));
  }

  @Test
  void between_getResult_byJoinMaxExclusiveLocalDate() {
    final LocalDate min = TestConstants.USER_1_REGISTRATION_DATE;
    final LocalDate max = TestConstants.USER_2_REGISTRATION_DATE;
    final List<String> columns = List.of(Post_.AUTHOR, User_.REGISTRATION_DATE);
    final Specification<Post> spec =
        SpecificationBuilder.<Post>builder()
            .between(columns, min, max, b -> b.maxBound(Bound.EXCLUSIVE))
            .build();

    final List<String> attributePaths = List.of(DbUtils.joinPath(Post_.AUTHOR, User_.PROFILE));
    final EntityGraph eg = DynamicEntityGraph.loading(attributePaths);
    final List<Post> entities = postRepository.findAll(spec, eg);

    assertThat(entities)
        .isNotEmpty()
        .extracting(Post::getAuthor)
        .extracting(User::getRegistrationDate)
        .allSatisfy(v -> assertThat(v).isAfterOrEqualTo(min).isBefore(max));
  }
}
