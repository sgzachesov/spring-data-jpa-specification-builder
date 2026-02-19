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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TestConstants {
  public static final String USER_NAME_GROUP = "USER";
  public static final String ADMIN_NAME_GROUP = "ADMIN";

  public static final String USER_1_USERNAME = "user1@user.com";
  public static final LocalDate USER_1_REGISTRATION_DATE = LocalDate.of(2023, 6, 4);
  public static final String USER_1_PHONE = "+79666666666";
  public static final String USER_2_USERNAME = "user2@user.com";
  public static final LocalDate USER_2_REGISTRATION_DATE = LocalDate.of(2024, 6, 5);
  public static final String ADMIN_USERNAME = "admin@admin.com";
  public static final String ADMIN_PHONE = "+79777777777";

  public static final String USER_1_BIO = "Интересный человек";

  public static final String TITLE_POST_1_USER_1 = "Супер пост!";
  public static final String CONTENT_POST_1_USER_1 =
      "Очень интересный контент, об интересных постах. Читайте внимательно!";
  public static final BigDecimal RATING_POST_1_USER_1 = BigDecimal.valueOf(13.3);
  public static final LocalDateTime CREATE_AT_POST_1_USER_1 =
      LocalDateTime.of(2025, 10, 25, 9, 11, 13);
  public static final Integer WORD_COUNT_POST_1_USER_1 = 23;

  public static final String TITLE_POST_2_USER_1 = "Супер пост 4!";
  public static final String CONTENT_POST_2_USER_1 =
      "Очень интересный контент, об интересных постах. Читайте внимательно 2!";
  public static final BigDecimal RATING_POST_2_USER_1 = BigDecimal.valueOf(3.74);
  public static final LocalDateTime CREATE_AT_POST_2_USER_1 =
      LocalDateTime.of(2025, 11, 25, 9, 11, 13);
  public static final Integer WORD_COUNT_POST_2_USER_1 = 24;

  public static final String TITLE_POST_1_USER_2 = "Супер пост 444!";
  public static final String CONTENT_POST_1_USER_2 =
      "Очень интересный контент, об интересных постах. Читайте внимательно 555!";
  public static final BigDecimal RATING_POST_1_USER_2 = BigDecimal.valueOf(3.75);
  public static final LocalDateTime CREATE_AT_POST_1_USER_2 =
      LocalDateTime.of(2025, 11, 25, 9, 11, 14);
  public static final Integer WORD_COUNT_POST_1_USER_2 = 77;

  public static final String TECH_NAME_TAG = "Tech";
  public static final String PROGRAMMING_NAME_TAG = "Programming";
  public static final String AI_NAME_TAG = "AI";
}
