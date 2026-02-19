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

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.io.Serial;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Predicate of inequality (&gt;, &lt;, &ge;, &le;).
 *
 * @param <T> the type of the {@link Root} the resulting {@literal Specification} operates on.
 * @param <P> target predicate type, maybe {@link Join}
 */
public class InequalitySpecification<T, P extends Comparable<? super P>>
    extends ComparisonSpecification<T, P> {

  @Serial private static final long serialVersionUID = 1908582843545920332L;

  private final Sign sign;

  protected InequalitySpecification(final Builder<T, P> builder, final Sign sign) {
    super(builder);
    this.sign = sign;
  }

  static <T, P extends Comparable<? super P>> InequalitySpecification<T, P> gt(
      final Builder<T, P> builder) {
    return new InequalitySpecification<>(builder, Sign.GT);
  }

  static <T, P extends Comparable<? super P>> InequalitySpecification<T, P> gte(
      final Builder<T, P> builder) {
    return new InequalitySpecification<>(builder, Sign.GTE);
  }

  static <T, P extends Comparable<? super P>> InequalitySpecification<T, P> lt(
      final Builder<T, P> builder) {
    return new InequalitySpecification<>(builder, Sign.LT);
  }

  static <T, P extends Comparable<? super P>> InequalitySpecification<T, P> lte(
      final Builder<T, P> builder) {
    return new InequalitySpecification<>(builder, Sign.LTE);
  }

  @Override
  Predicate toPredicate(final CriteriaBuilder builder, final Path<P> path) {
    return sign.toPredicate(builder, path, range);
  }

  /**
   * The sign of inequality.
   *
   * <p><a href="https://en.wikipedia.org/wiki/Inequality_(mathematics)">Inequality</a>
   */
  @Getter
  @AllArgsConstructor
  protected enum Sign {
    GT("greater than", ">") {
      @Override
      <P extends Comparable<? super P>> Predicate toPredicate(
          final CriteriaBuilder builder, final Path<P> path, final Range<P> range) {
        return builder.greaterThan(path, range.min());
      }
    },
    GTE("greater than or equal to", ">=") {
      @Override
      <P extends Comparable<? super P>> Predicate toPredicate(
          final CriteriaBuilder builder, final Path<P> path, final Range<P> range) {
        return builder.greaterThanOrEqualTo(path, range.min());
      }
    },
    LT("less than", "<") {
      @Override
      <P extends Comparable<? super P>> Predicate toPredicate(
          final CriteriaBuilder builder, final Path<P> path, final Range<P> range) {
        return builder.lessThan(path, range.max());
      }
    },
    LTE("less than or equal to", "<=") {
      @Override
      <P extends Comparable<? super P>> Predicate toPredicate(
          final CriteriaBuilder builder, final Path<P> path, final Range<P> range) {
        return builder.lessThanOrEqualTo(path, range.max());
      }
    };

    private final String name;
    private final String description;

    abstract <P extends Comparable<? super P>> Predicate toPredicate(
        CriteriaBuilder builder, Path<P> path, Range<P> range);
  }
}
