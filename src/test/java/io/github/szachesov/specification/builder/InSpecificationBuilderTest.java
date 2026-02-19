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
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

class InSpecificationBuilderTest extends SpecificationBuilderTest {

  @Test
  void in_getResult_byVarchar() {
    final List<String> usernames =
        List.of(TestConstants.USER_1_USERNAME, TestConstants.USER_2_USERNAME);
    final Specification<User> spec =
        SpecificationBuilder.<User>builder().in(User_.USERNAME, usernames).build();

    final List<User> entities = userRepository.findAll(spec);

    assertThat(entities).isNotEmpty().extracting(User::getUsername).containsAll(usernames);
  }

  @Test
  void in_getAll_byNullValues() {
    final Specification<Post> spec =
        SpecificationBuilder.<Post>builder().in(Post_.RATING, null).build();

    final List<Post> entities = postRepository.findAll(spec);

    assertThat(entities).hasSize(TestData.POSTS.size());
  }

  @Test
  void in_getAll_byEmptyValues() {
    final Specification<Post> spec =
        SpecificationBuilder.<Post>builder().in(Post_.RATING, List.of()).build();

    final List<Post> entities = postRepository.findAll(spec);

    assertThat(entities).hasSize(TestData.POSTS.size());
  }

  @Test
  void in_getResult_byJoinInteger() {
    final List<Integer> values = List.of(1, 2);
    final Specification<Post> spec =
        SpecificationBuilder.<Post>builder().in(List.of(Post_.AUTHOR, User_.ID), values).build();

    final EntityGraph eg =
        DynamicEntityGraph.loading(List.of(DbUtils.joinPath(Post_.AUTHOR, User_.PROFILE)));
    final List<Post> entities = postRepository.findAll(spec, eg);

    assertThat(entities)
        .isNotEmpty()
        .extracting(Post::getAuthor)
        .extracting(User::getId)
        .containsAnyElementsOf(values);
  }
}
