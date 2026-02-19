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

package io.github.szachesov.specification.builder.testutils;

import io.github.szachesov.specification.builder.sample.entity.Group;
import io.github.szachesov.specification.builder.sample.entity.Post;
import io.github.szachesov.specification.builder.sample.entity.Profile;
import io.github.szachesov.specification.builder.sample.entity.Tag;
import io.github.szachesov.specification.builder.sample.entity.User;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TestData {

  public static final Group ADMIN_GROUP = new Group(TestConstants.ADMIN_NAME_GROUP);
  public static final Group USER_GROUP = new Group(TestConstants.USER_NAME_GROUP);
  public static final List<Group> GROUPS = List.of(ADMIN_GROUP, USER_GROUP);

  public static final Tag TECH_TAG = new Tag(TestConstants.TECH_NAME_TAG);
  public static final Tag PROGRAMMING_TAG = new Tag(TestConstants.PROGRAMMING_NAME_TAG);
  public static final Tag AI_TAG = new Tag(TestConstants.AI_NAME_TAG);
  public static final List<Tag> TAGS = List.of(TECH_TAG, PROGRAMMING_TAG, AI_TAG);

  public static final User ADMIN_USER =
      User.builder()
          .username(TestConstants.ADMIN_USERNAME)
          .isActive(true)
          .registrationDate(LocalDate.of(2025, 6, 25))
          .phone(TestConstants.ADMIN_PHONE)
          .groups(List.of(ADMIN_GROUP, USER_GROUP))
          .build();

  public static final User USER_1 =
      User.builder()
          .username(TestConstants.USER_1_USERNAME)
          .isActive(false)
          .registrationDate(TestConstants.USER_1_REGISTRATION_DATE)
          .phone(TestConstants.USER_1_PHONE)
          .groups(List.of(USER_GROUP))
          .build();

  public static final User USER_2 =
      User.builder()
          .username(TestConstants.USER_2_USERNAME)
          .isActive(true)
          .registrationDate(TestConstants.USER_2_REGISTRATION_DATE)
          .groups(List.of(USER_GROUP))
          .registrationDate(TestConstants.USER_2_REGISTRATION_DATE)
          .build();
  public static final List<User> USERS = List.of(ADMIN_USER, USER_1, USER_2);

  public static final Profile PROFILE_USER_1 = new Profile(TestConstants.USER_1_BIO, USER_1);

  public static final Post POST_1_USER_1 =
      Post.builder()
          .title(TestConstants.TITLE_POST_1_USER_1)
          .content(TestConstants.CONTENT_POST_1_USER_1)
          .createdAt(TestConstants.CREATE_AT_POST_1_USER_1)
          .rating(TestConstants.RATING_POST_1_USER_1)
          .wordCount(TestConstants.WORD_COUNT_POST_1_USER_1)
          .author(USER_1)
          .tags(Set.of(AI_TAG.getName(), TECH_TAG.getName()))
          .build();

  public static final Post POST_2_USER_1 =
      Post.builder()
          .title(TestConstants.TITLE_POST_2_USER_1)
          .content(TestConstants.CONTENT_POST_2_USER_1)
          .createdAt(TestConstants.CREATE_AT_POST_2_USER_1)
          .rating(TestConstants.RATING_POST_2_USER_1)
          .wordCount(TestConstants.WORD_COUNT_POST_2_USER_1)
          .author(USER_1)
          .tags(Set.of(PROGRAMMING_TAG.getName(), AI_TAG.getName()))
          .build();

  public static final Post POST_1_USER_2 =
      Post.builder()
          .title(TestConstants.TITLE_POST_1_USER_2)
          .content(TestConstants.CONTENT_POST_1_USER_2)
          .createdAt(TestConstants.CREATE_AT_POST_1_USER_2)
          .rating(TestConstants.RATING_POST_1_USER_2)
          .wordCount(TestConstants.WORD_COUNT_POST_1_USER_2)
          .author(USER_2)
          .tags(Set.of(TECH_TAG.getName()))
          .build();
  public static final List<Post> POSTS = List.of(POST_1_USER_1, POST_2_USER_1, POST_1_USER_2);
}
