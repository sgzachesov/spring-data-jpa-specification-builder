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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
import jakarta.persistence.criteria.JoinType;
import java.util.List;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.Test;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

class CommonSpecificationBuilderTest extends SpecificationBuilderTest {

  @Test
  void distinct_throws_orderJoinColumnWithDistinct() {
    final Specification<User> spec =
        SpecificationBuilder.<User>builder().isNotNull(List.of(User_.POSTS, Post_.TITLE)).build();

    final Sort sort = Sort.by(DbUtils.joinPath(User_.POSTS, Post_.TITLE));
    assertThatThrownBy(() -> userRepository.findAll(spec, sort))
        .isInstanceOf(InvalidDataAccessResourceUsageException.class);
  }

  @Test
  void distinct_getResult_orderJoinColumnWithDistinctOff() {
    final Specification<User> spec =
        SpecificationBuilder.<User>builder()
            .distinct(false)
            .isNotNull(List.of(User_.POSTS, Post_.TITLE))
            .build();

    final Sort sort = Sort.by(DbUtils.joinPath(User_.POSTS, Post_.TITLE));
    final List<User> entities = userRepository.findAll(spec, sort);

    assertThat(entities).isNotEmpty();
  }

  @Test
  void inner_getAll_innerSpecificationIsNull() {
    final Specification<User> spec =
        SpecificationBuilder.<User>builder().inner(null, LogicalOperator.AND).build();

    final EntityGraph eg = DynamicEntityGraph.loading(List.of(User_.POSTS, User_.PROFILE));
    final List<User> entities = userRepository.findAll(spec, eg);

    assertThat(entities).hasSize(TestData.USERS.size());
  }

  @Test
  void inner_getResult_basePredicateAndInnerOrPredicates() {
    final String username1 = TestConstants.ADMIN_USERNAME;
    final String username2 = TestConstants.USER_1_USERNAME;
    final Specification<User> innerSpec =
        SpecificationBuilder.<User>builder()
            .equal(User_.USERNAME, username1)
            .equal(User_.USERNAME, username2, b -> b.connection(LogicalOperator.OR))
            .build();

    final Specification<User> spec =
        SpecificationBuilder.<User>builder()
            .inner(innerSpec, LogicalOperator.AND)
            .isNotNull(User_.POSTS)
            .build();

    final EntityGraph eg = DynamicEntityGraph.loading(List.of(User_.POSTS, User_.PROFILE));
    final List<User> entities = userRepository.findAll(spec, eg);

    assertThat(entities)
        .isNotEmpty()
        .allSatisfy(e -> assertThat(e.getUsername()).containsAnyOf(username1, username2))
        .allSatisfy(e -> assertThat(e.getPosts()).isNotEmpty());
  }

  @Test
  void andInner_getResult_basePredicateAndInnerOrPredicates() {
    final String username1 = TestConstants.ADMIN_USERNAME;
    final String username2 = TestConstants.USER_1_USERNAME;
    final Specification<User> innerSpec =
        SpecificationBuilder.<User>builder()
            .equal(User_.USERNAME, username1)
            .equal(User_.USERNAME, username2, b -> b.connection(LogicalOperator.OR))
            .build();

    final Specification<User> spec =
        SpecificationBuilder.<User>builder().andInner(innerSpec).isNotNull(User_.POSTS).build();

    final EntityGraph eg = DynamicEntityGraph.loading(List.of(User_.POSTS, User_.PROFILE));
    final List<User> entities = userRepository.findAll(spec, eg);

    assertThat(entities)
        .isNotEmpty()
        .allSatisfy(e -> assertThat(e.getUsername()).containsAnyOf(username1, username2))
        .allSatisfy(e -> assertThat(e.getPosts()).isNotEmpty());
  }

  @Test
  void orInner_getResult_basePredicateOrInnerOrPredicates() {
    final String username1 = TestConstants.ADMIN_USERNAME;
    final String phoneUser1 = TestConstants.ADMIN_PHONE;
    final Specification<User> innerSpec =
        SpecificationBuilder.<User>builder()
            .equal(User_.USERNAME, username1)
            .equal(User_.PHONE, phoneUser1)
            .build();

    final Specification<User> spec =
        SpecificationBuilder.<User>builder().orInner(innerSpec).isNotNull(User_.POSTS).build();

    final EntityGraph eg = DynamicEntityGraph.loading(List.of(User_.POSTS, User_.PROFILE));
    final List<User> entities = userRepository.findAll(spec, eg);

    assertThat(entities)
        .isNotEmpty()
        .anySatisfy(user -> assertThat(user.getPosts()).isNotEmpty())
        .anySatisfy(
            user -> {
              assertThat(user.getUsername()).isEqualTo(username1);
              assertThat(user.getPhone()).isEqualTo(phoneUser1);
            });
  }

  @Test
  void emptyAllSpecification_getResult() {
    final Specification<Post> spec = SpecificationBuilder.<Post>builder().build();

    final List<Post> entities = postRepository.findAll(spec);

    assertThat(entities).hasSize(TestData.POSTS.size());
  }

  @Test
  void emptySpecificationAndNotEmptyInner_getAll() {
    final String username1 = TestConstants.ADMIN_USERNAME;
    final String phoneUser1 = TestConstants.ADMIN_PHONE;
    final Specification<User> innerSpec =
        SpecificationBuilder.<User>builder()
            .equal(User_.USERNAME, username1)
            .equal(User_.PHONE, phoneUser1)
            .build();

    final Specification<User> spec =
        SpecificationBuilder.<User>builder().orInner(innerSpec).build();

    final EntityGraph eg = DynamicEntityGraph.loading(List.of(User_.POSTS, User_.PROFILE));
    final List<User> entities = userRepository.findAll(spec, eg);

    assertThat(entities)
        .isNotEmpty()
        .anySatisfy(
            user -> {
              assertThat(user.getUsername()).isEqualTo(username1);
              assertThat(user.getPhone()).isEqualTo(phoneUser1);
            });
  }

  @Test
  void equal_getResult_byCollectionField() {
    final String value = TestConstants.TECH_NAME_TAG;
    final Specification<Post> spec =
        SpecificationBuilder.<Post>builder().equal(Post_.TAGS, value).build();

    final EntityGraph eg = DynamicEntityGraph.loading(List.of(Post_.TAGS));
    final List<Post> entities = postRepository.findAll(spec, eg);

    assertThat(entities)
        .isNotEmpty()
        .extracting(Post::getTags)
        .allSatisfy(posts -> assertThat(posts).anySatisfy(p -> assertThat(p).contains(value)));
  }

  @Test
  void equal_getResult_byJoinCollectionField() {
    final String value = TestConstants.TECH_NAME_TAG;
    final Specification<User> spec =
        SpecificationBuilder.<User>builder().equal(List.of(User_.POSTS, Post_.TAGS), value).build();

    final EntityGraph eg =
        DynamicEntityGraph.loading(
            List.of(DbUtils.joinPath(User_.POSTS, Post_.TAGS), User_.PROFILE));
    final List<User> entities = userRepository.findAll(spec, eg);

    assertThat(entities)
        .isNotEmpty()
        .allSatisfy(
            e ->
                assertThat(e.getPosts())
                    .flatExtracting(Post::getTags)
                    .anySatisfy(t -> assertThat(t).isEqualTo(value)));
  }

  @Test
  void equal_getResult_byJoinAndLeftJoin() {
    final String group = TestConstants.USER_NAME_GROUP;
    final String phone = TestData.USER_1.getPhone();
    final Specification<Post> spec =
        SpecificationBuilder.<Post>builder()
            .equal(List.of(Post_.AUTHOR, User_.GROUPS, Group_.NAME), group)
            .equal(List.of(Post_.AUTHOR, User_.PHONE), phone, b -> b.join(JoinType.INNER))
            .build();

    final List<String> attributePaths =
        List.of(
            DbUtils.joinPath(Post_.AUTHOR, User_.GROUPS),
            DbUtils.joinPath(Post_.AUTHOR, User_.PROFILE));
    final EntityGraph eg = DynamicEntityGraph.loading(attributePaths);
    final List<Post> entities = postRepository.findAll(spec, eg);

    assertThat(entities)
        .isNotEmpty()
        .extracting(Post::getAuthor)
        .allSatisfy(
            user -> {
              assertThat(user.getGroups()).extracting(Group::getName).containsAnyOf(group);
              assertThat(user.getPhone()).isEqualTo(phone);
            });
  }

  @Test
  void equal_getResult_byFetch() {
    final String phone = TestData.USER_1.getPhone();
    final Specification<Post> spec =
        SpecificationBuilder.<Post>builder()
            .equal(List.of(Post_.AUTHOR, User_.PHONE), phone, CompositeSpecification.Builder::fetch)
            .build();

    final EntityGraph eg =
        DynamicEntityGraph.loading(List.of(DbUtils.joinPath(Post_.AUTHOR, User_.PROFILE)));
    final List<Post> entities = postRepository.findAll(spec, eg);

    assertThat(entities)
        .isNotEmpty()
        .extracting(Post::getAuthor)
        .allSatisfy(user -> assertThat(Hibernate.isInitialized(user)).isEqualTo(true));
  }

  @Test
  void equal_getResult_byFetchToMany() {
    final Specification<User> spec =
        SpecificationBuilder.<User>builder()
            .isNull(List.of(User_.POSTS, Post_.TITLE), true, b -> b.not().fetch())
            .build();

    final List<User> entities = userRepository.findAll(spec);

    assertThat(entities)
        .isNotEmpty()
        .extracting(User::getPosts)
        .allSatisfy(p -> assertThat(Hibernate.isInitialized(p)).isEqualTo(true));
  }

  @Test
  void isNotNull_getResult_byOneToOne() {
    final Specification<User> spec =
        SpecificationBuilder.<User>builder().isNotNull(List.of(User_.PROFILE)).build();

    final List<User> entities = userRepository.findAll(spec);

    assertThat(entities).isNotEmpty().extracting(User::getProfile).isNotEmpty();
  }
}
