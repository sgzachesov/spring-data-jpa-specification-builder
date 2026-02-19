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
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

class IsNullSpecificationBuilderTest extends SpecificationBuilderTest {

  @Test
  void isNotNull_getResult_byVarchar() {
    final Specification<User> spec =
        SpecificationBuilder.<User>builder().isNotNull(User_.PHONE).build();

    final List<User> entities = userRepository.findAll(spec);

    assertThat(entities).isNotEmpty().extracting(User::getPhone).doesNotContainNull();
  }

  @Test
  void isNull_getResult_byVarchar() {
    final Specification<User> spec =
        SpecificationBuilder.<User>builder().isNull(User_.PHONE).build();

    final List<User> entities = userRepository.findAll(spec);

    assertThat(entities).isNotEmpty().extracting(User::getPhone).containsNull();
  }

  @Test
  void isNull_getResult_byIsActive() {
    final Specification<User> spec =
        SpecificationBuilder.<User>builder().isNull(User_.PHONE, true).build();

    final List<User> entities = userRepository.findAll(spec);

    assertThat(entities).isNotEmpty().extracting(User::getPhone).containsNull();
  }

  @Test
  void isNull_getAll_byIsNotActive() {
    final Specification<User> spec =
        SpecificationBuilder.<User>builder().isNull(User_.PHONE, false).build();

    final List<User> entities = userRepository.findAll(spec);

    assertThat(entities).isNotEmpty().extracting(User::getPhone).containsNull();
  }

  @Test
  void isNull_getResult_byIsActiveNotNull() {
    final Specification<User> spec =
        SpecificationBuilder.<User>builder()
            .isNull(User_.PHONE, true, CompositeSpecification.Builder::not)
            .build();

    final List<User> entities = userRepository.findAll(spec);

    assertThat(entities).isNotEmpty().extracting(User::getPhone).doesNotContainNull();
  }

  @Test
  void isNull_getResult_byJoinVarchar() {
    final Specification<Post> spec =
        SpecificationBuilder.<Post>builder().isNull(List.of(Post_.AUTHOR, User_.PHONE)).build();

    final EntityGraph eg =
        DynamicEntityGraph.loading(List.of(DbUtils.joinPath(Post_.AUTHOR, User_.PROFILE)));
    final List<Post> entities = postRepository.findAll(spec, eg);

    assertThat(entities)
        .isNotEmpty()
        .extracting(Post::getAuthor)
        .extracting(User::getPhone)
        .containsNull();
  }
}
