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
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

class LikeSpecificationBuilderTest extends SpecificationBuilderTest {

  @Test
  void like_getResult_byAbsence() {
    final String value = TestConstants.USER_NAME_GROUP;
    final Specification<Group> spec =
        SpecificationBuilder.<Group>builder().like(Group_.NAME, value).build();

    final List<Group> entities = groupRepository.findAll(spec);

    assertThat(entities).isNotEmpty().extracting(Group::getName).contains(value);
  }

  @Test
  void like_getAll_byNullValue() {
    final Specification<Group> spec =
        SpecificationBuilder.<Group>builder().like(Group_.NAME, null).build();

    final List<Group> entities = groupRepository.findAll(spec);

    assertThat(entities).hasSize(TestData.GROUPS.size());
  }

  @Test
  void like_getAll_byBlankValue() {
    final Specification<Group> spec =
        SpecificationBuilder.<Group>builder().like(Group_.NAME, "   ").build();

    final List<Group> entities = groupRepository.findAll(spec);

    assertThat(entities).hasSize(TestData.GROUPS.size());
  }

  @Test
  void like_getAll_byMinChar() {
    final String value = TestConstants.USER_NAME_GROUP.substring(0, 2);
    final Specification<Group> spec =
        SpecificationBuilder.<Group>builder().like(Group_.NAME, value).build();

    final List<Group> entities = groupRepository.findAll(spec);

    assertThat(entities).hasSize(TestData.GROUPS.size());
  }

  @Test
  void like_getResult_byCustomMinCharAndWildcardEnding() {
    final String value = TestConstants.USER_NAME_GROUP.substring(0, 2);
    final Specification<Group> spec =
        SpecificationBuilder.<Group>builder()
            .like(Group_.NAME, value, b -> b.minChar(2).wildcard(Wildcard.ENDING))
            .build();

    final List<Group> entities = groupRepository.findAll(spec);

    assertThat(entities)
        .extracting(Group::getName)
        .allSatisfy(n -> assertThat(n).startsWith(value));
  }

  @Test
  void like_getResult_byCustomMinCharAndWildcardLeading() {
    final String world = TestConstants.USER_NAME_GROUP;
    final String value = world.substring(world.length() - 3);
    final Specification<Group> spec =
        SpecificationBuilder.<Group>builder()
            .like(Group_.NAME, value, b -> b.minChar(2).wildcard(Wildcard.LEADING))
            .build();

    final List<Group> entities = groupRepository.findAll(spec);

    assertThat(entities).extracting(Group::getName).allSatisfy(n -> assertThat(n).endsWith(value));
  }

  @Test
  void like_getResult_byCustomMinCharAndWildcardMultiple() {
    final String world = TestConstants.USER_NAME_GROUP;
    final String value = world.substring(world.length() - 3, world.length() - 1);
    final Specification<Group> spec =
        SpecificationBuilder.<Group>builder()
            .like(Group_.NAME, value, b -> b.minChar(2).wildcard(Wildcard.MULTIPLE))
            .build();

    final List<Group> entities = groupRepository.findAll(spec);

    assertThat(entities).extracting(Group::getName).allSatisfy(n -> assertThat(n).contains(value));
  }

  @Test
  void like_notFound_byNoIgnoreCase() {
    final String value = TestConstants.USER_NAME_GROUP.toLowerCase(Locale.ROOT);
    final Specification<Group> spec =
        SpecificationBuilder.<Group>builder()
            .like(Group_.NAME, value, LikeSpecification.Builder::noIgnoreCase)
            .build();

    final List<Group> entities = groupRepository.findAll(spec);

    assertThat(entities).isEmpty();
  }

  @Test
  void like_getResult_byJoin() {
    final String value = TestConstants.USER_1_USERNAME;
    final Specification<Post> spec =
        SpecificationBuilder.<Post>builder()
            .like(List.of(Post_.AUTHOR, User_.USERNAME), value)
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
}
