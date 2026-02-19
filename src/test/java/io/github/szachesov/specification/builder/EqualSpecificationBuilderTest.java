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
import io.github.szachesov.specification.builder.sample.entity.Group;
import io.github.szachesov.specification.builder.sample.entity.Group_;
import io.github.szachesov.specification.builder.sample.entity.Post;
import io.github.szachesov.specification.builder.sample.entity.Post_;
import io.github.szachesov.specification.builder.sample.entity.User;
import io.github.szachesov.specification.builder.sample.entity.User_;
import io.github.szachesov.specification.builder.testutils.DbUtils;
import io.github.szachesov.specification.builder.testutils.TestConstants;
import io.github.szachesov.specification.builder.testutils.TestData;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.assertj.core.util.BigDecimalComparator;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

class EqualSpecificationBuilderTest extends SpecificationBuilderTest {

  @Test
  void equal_getResult_byVarchar() {
    final String value = TestConstants.USER_NAME_GROUP;
    final Specification<Group> spec =
        SpecificationBuilder.<Group>builder().equal(Group_.NAME, value).build();

    final List<Group> entities = groupRepository.findAll(spec);

    assertThat(entities).extracting(Group::getName).contains(value);
  }

  @Test
  void equal_notFound_byVarchar() {
    final Specification<Group> spec =
        SpecificationBuilder.<Group>builder().equal(Group_.NAME, "Unknown").build();

    final List<Group> entities = groupRepository.findAll(spec);

    assertThat(entities).hasSize(0);
  }

  @Test
  void equal_getResult_byNotVarchar() {
    final String value = TestConstants.USER_NAME_GROUP;
    final Specification<Group> spec =
        SpecificationBuilder.<Group>builder()
            .equal(Group_.NAME, value, CompositeSpecification.Builder::not)
            .build();

    final List<Group> entities = groupRepository.findAll(spec);

    assertThat(entities).extracting(Group::getName).doesNotContain(value);
  }

  @Test
  void equal_getResult_byBoolean() {
    final Boolean value = TestData.USER_1.getIsActive();
    final Specification<User> spec =
        SpecificationBuilder.<User>builder().equal(User_.IS_ACTIVE, value).build();

    final List<User> entities = userRepository.findAll(spec);

    assertThat(entities).extracting(User::getIsActive).contains(value);
  }

  @Test
  void notEqual_getResult_byBoolean() {
    final Boolean value = TestData.USER_1.getIsActive();
    final Specification<User> spec =
        SpecificationBuilder.<User>builder().notEqual(User_.IS_ACTIVE, value).build();

    final List<User> entities = userRepository.findAll(spec);

    assertThat(entities).extracting(User::getIsActive).doesNotContain(value);
  }

  @Test
  void equal_getResult_byInteger() {
    final var value = 1;
    final Specification<User> spec =
        SpecificationBuilder.<User>builder().equal(User_.ID, value).build();

    final List<User> entities = userRepository.findAll(spec);

    assertThat(entities).extracting(User::getId).contains(value);
  }

  @Test
  void equal_getResult_byBigDecimal() {
    final BigDecimal value = TestConstants.RATING_POST_1_USER_1;
    final Specification<Post> spec =
        SpecificationBuilder.<Post>builder().equal(Post_.RATING, value).build();

    final List<Post> entities = postRepository.findAll(spec);

    assertThat(entities)
        .extracting(Post::getRating)
        .usingComparatorForType(BigDecimalComparator.BIG_DECIMAL_COMPARATOR, BigDecimal.class)
        .contains(value);
  }

  @Test
  void equal_getResult_byDate() {
    final LocalDate value = TestConstants.USER_1_REGISTRATION_DATE;
    final Specification<User> spec =
        SpecificationBuilder.<User>builder().equal(User_.REGISTRATION_DATE, value).build();

    final List<User> entities = userRepository.findAll(spec);

    assertThat(entities).extracting(User::getRegistrationDate).contains(value);
  }

  @Test
  void equal_getResult_byDateTime() {
    final LocalDateTime value = TestConstants.CREATE_AT_POST_1_USER_1;
    final Specification<Post> spec =
        SpecificationBuilder.<Post>builder().equal(Post_.CREATED_AT, value).build();

    final List<Post> entities = postRepository.findAll(spec);

    assertThat(entities).extracting(Post::getCreatedAt).contains(value);
  }

  @Test
  void equal_getResult_byJoinObject() {
    final User value = TestData.USER_1;
    final Specification<Post> spec =
        SpecificationBuilder.<Post>builder().equal(Post_.AUTHOR, value).build();

    final EntityGraph eg =
        DynamicEntityGraph.loading(List.of(DbUtils.joinPath(Post_.AUTHOR, User_.PROFILE)));
    final List<Post> entities = postRepository.findAll(spec, eg);

    assertThat(entities)
        .isNotEmpty()
        .extracting(Post::getAuthor)
        .extracting(User::getUsername)
        .contains(value.getUsername());
  }

  @Test
  void equal_notFound_byJoinObject() {
    final User user = TestData.ADMIN_USER;
    final Specification<Post> spec =
        SpecificationBuilder.<Post>builder().equal(Post_.AUTHOR, user).build();

    final EntityGraph eg = DynamicEntityGraph.loading(List.of(Post_.AUTHOR));
    final List<Post> entities = postRepository.findAll(spec, eg);

    assertThat(entities).hasSize(0);
  }

  @Test
  void equal_getResult_byJoinVarchar() {
    final String value = TestData.USER_1.getUsername();
    final Specification<Post> spec =
        SpecificationBuilder.<Post>builder()
            .equal(List.of(Post_.AUTHOR, User_.USERNAME), value)
            .build();

    final EntityGraph eg =
        DynamicEntityGraph.loading(List.of(DbUtils.joinPath(Post_.AUTHOR, User_.PROFILE)));
    final List<Post> entities = postRepository.findAll(spec, eg);

    assertThat(entities)
        .isNotEmpty()
        .extracting(Post::getAuthor)
        .extracting(User::getUsername)
        .contains(value);
  }

  @Test
  void equal_getAll_byNullValue() {
    final Specification<Post> spec =
        SpecificationBuilder.<Post>builder().equal(Post_.AUTHOR, null).build();

    final List<Post> entities = postRepository.findAll(spec);

    assertThat(entities).hasSize(TestData.POSTS.size());
  }
}
