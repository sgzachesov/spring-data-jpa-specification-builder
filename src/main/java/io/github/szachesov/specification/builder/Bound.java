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

import lombok.Getter;

/** Enumeration of boundary types for value comparison. */
@Getter
public enum Bound {
  INCLUSIVE("<=", ">=") {
    @Override
    <T, P extends Comparable<? super P>> InequalitySpecification<T, P> min(
        final ComparisonSpecification.Builder<T, P> builder) {
      return InequalitySpecification.gte(builder);
    }

    @Override
    <T, P extends Comparable<? super P>> InequalitySpecification<T, P> max(
        final ComparisonSpecification.Builder<T, P> builder) {
      return InequalitySpecification.lte(builder);
    }
  },
  EXCLUSIVE(">", "<") {
    @Override
    <T, P extends Comparable<? super P>> InequalitySpecification<T, P> min(
        final ComparisonSpecification.Builder<T, P> builder) {
      return InequalitySpecification.gt(builder);
    }

    @Override
    <T, P extends Comparable<? super P>> InequalitySpecification<T, P> max(
        final ComparisonSpecification.Builder<T, P> builder) {
      return InequalitySpecification.lt(builder);
    }
  };
  private final String[] descriptions;

  Bound(final String... description) {
    this.descriptions = description;
  }

  abstract <T, P extends Comparable<? super P>> InequalitySpecification<T, P> min(
      ComparisonSpecification.Builder<T, P> builder);

  abstract <T, P extends Comparable<? super P>> InequalitySpecification<T, P> max(
      ComparisonSpecification.Builder<T, P> builder);
}
