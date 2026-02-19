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

import io.github.szachesov.specification.builder.sample.repository.GroupRepository;
import io.github.szachesov.specification.builder.sample.repository.PostRepository;
import io.github.szachesov.specification.builder.sample.repository.TagRepository;
import io.github.szachesov.specification.builder.sample.repository.UserRepository;
import io.github.szachesov.specification.builder.testutils.TestData;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@DataJpaTest
@Transactional(propagation = Propagation.NEVER)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
abstract class SpecificationBuilderTest {

  @Autowired protected GroupRepository groupRepository;
  @Autowired protected UserRepository userRepository;
  @Autowired protected PostRepository postRepository;

  @BeforeAll
  static void init(
      @Autowired final GroupRepository groupRepository,
      @Autowired final UserRepository userRepository,
      @Autowired final PostRepository postRepository,
      @Autowired final TagRepository tagRepository) {

    groupRepository.saveAll(TestData.GROUPS);
    tagRepository.saveAll(TestData.TAGS);
    userRepository.saveAll(TestData.USERS);
    postRepository.saveAll(TestData.POSTS);
  }
}
