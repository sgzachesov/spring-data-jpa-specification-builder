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
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Predicate of comparison operators.
 *
 * @param <T> the type of the {@link Root} the resulting {@literal Specification} operates on.
 * @param <P> target predicate type, maybe {@link Join}
 */
public abstract class ComparisonSpecification<T, P extends Comparable<? super P>>
    extends CompositeSpecification<T, P> {

  @Serial private static final long serialVersionUID = -7540328509379465629L;

  protected final Range<P> range;

  protected ComparisonSpecification(final Builder<T, P> builder) {
    super(builder);
    this.range = new Range<>(builder.min, builder.max);
  }

  @Override
  protected Predicate toCriteriaPredicate(
      final Root<T> root, final CriteriaQuery<?> query, final CriteriaBuilder builder) {
    final Path<P> path = getPath(root);
    return toPredicate(builder, path);
  }

  abstract Predicate toPredicate(CriteriaBuilder builder, Path<P> path);

  /**
   * Builder for {@link ComparisonSpecification}.
   *
   * @param <T> the type of the {@link Root} the resulting {@literal Specification} operates on.
   * @param <P> target predicate type, maybe {@link Join}
   */
  public static class Builder<T, P extends Comparable<? super P>>
      extends CompositeSpecification.Builder<Builder<T, P>>
      implements ObjectBuilder<List<ComparisonSpecification<T, P>>> {

    private final P min;
    private final P max;
    private Bound minBound = Bound.INCLUSIVE;
    private Bound maxBound = Bound.INCLUSIVE;

    Builder(final List<String> columns, final P min, final P max) {
      super(columns);
      this.min = min;
      this.max = max;
    }

    /** The type of the minimum value boundary. */
    public Builder<T, P> minBound(final Bound minBound) {
      this.minBound = minBound;
      return self();
    }

    /** The type of the maximum value boundary. */
    public Builder<T, P> maxBound(final Bound maxBound) {
      this.maxBound = maxBound;
      return self();
    }

    @Override
    protected Builder<T, P> self() {
      return this;
    }

    @Override
    public List<ComparisonSpecification<T, P>> build() {
      if (isBetween()) {
        return List.of(new BetweenSpecification<>(this));
      }

      return buildInequalitySpecification();
    }

    boolean isEmptyValues() {
      return min == null && max == null;
    }

    private boolean isBetween() {
      if (this.min == null || this.max == null) return false;
      return !Bound.INCLUSIVE.equals(this.minBound) && !Bound.INCLUSIVE.equals(this.maxBound);
    }

    private List<ComparisonSpecification<T, P>> buildInequalitySpecification() {
      final List<ComparisonSpecification<T, P>> specifications = new ArrayList<>(2);
      if (min != null) {
        specifications.add(minBound.min(this));
      }
      if (max != null) {
        specifications.add(maxBound.max(this));
      }
      return specifications;
    }
  }

  /**
   * Range values for the value operation.
   *
   * @param min - minimum value for comparison
   * @param max - maximum value for comparison
   * @param <P> target predicate type, maybe {@link Join}
   */
  protected record Range<P extends Comparable<? super P>>(P min, P max) implements Serializable {
    @Serial private static final long serialVersionUID = -6357550033396721101L;
  }
}
