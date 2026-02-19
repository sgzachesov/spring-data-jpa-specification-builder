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

import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.data.jpa.domain.Specification;

/**
 * A builder aggregating specifications describing all SQL predicates.
 *
 * <p><a href="https://en.wikipedia.org/wiki/SQL_syntax#Operators">SQL operators</a>
 *
 * @param <T> the type of the {@link Root} the resulting {@literal Specification} operates on.
 */
@NoArgsConstructor(staticName = "builder")
public class SpecificationBuilder<T> {

  private final List<CompositeSpecification<T, ?>> specifications = new ArrayList<>();
  private final List<InnerSpecification<T>> innerSpecifications = new ArrayList<>();
  private boolean distinct = true;

  /**
   * Specify whether duplicate query results will be eliminated. A true value will cause duplicates
   * to be eliminated.
   *
   * <p>Example: {@code SELECT DISTINCT ...}
   *
   * @param distinct boolean value specifying whether duplicate results must be eliminated from the
   *     query result or whether they must be retained
   */
  public SpecificationBuilder<T> distinct(final boolean distinct) {
    this.distinct = distinct;
    return this;
  }

  /**
   * Logical operations AND grouped with parentheses.
   *
   * <p>Example: {@code ... AND (column_1 = 1 AND column_2 = 2) AND...}
   *
   * @param spec specification representing grouped logical predicates.
   */
  public SpecificationBuilder<T> andInner(final Specification<T> spec) {
    return inner(spec, LogicalOperator.AND);
  }

  /**
   * Logical operations OR grouped with parentheses.
   *
   * <p>Example: {@code ... OR (column_1 = 1 AND column_2 = 2) AND...}
   *
   * @param spec specification representing grouped logical predicates.
   */
  public SpecificationBuilder<T> orInner(final Specification<T> spec) {
    return inner(spec, LogicalOperator.OR);
  }

  /**
   * Logical operations grouped with parentheses.
   *
   * <p>Example: {@code ... OR/AND (column_1 = 1 AND column_2 = 2) AND...}
   *
   * @param spec specification representing grouped logical predicates.
   * @param operator logical of the condition connection.
   */
  public SpecificationBuilder<T> inner(
      final Specification<T> spec, final LogicalOperator operator) {
    if (spec == null) return this;
    final InnerSpecification<T> inner = new InnerSpecification<>(spec, operator);
    innerSpecifications.add(inner);
    return this;
  }

  /**
   * Not Equal to.
   *
   * <p>Example: {@code ...WHERE column != 1...}
   *
   * @param column column name.
   * @param value value of predicate.
   */
  public SpecificationBuilder<T> notEqual(final String column, final Object value) {
    return equal(column, value, EqualsSpecification.Builder::not);
  }

  /**
   * Equal to.
   *
   * <p>Example: {@code ...WHERE column = 1...}
   *
   * @param column column name.
   * @param value value of predicate.
   */
  public SpecificationBuilder<T> equal(final String column, final Object value) {
    return equal(column, value, EqualsSpecification.Builder::self);
  }

  /**
   * Equal to.
   *
   * <p>Example: {@code ...WHERE column = 1...}
   *
   * @param column column name.
   * @param value value of predicate.
   * @param fn function of the builder of additional predicate parameters.
   */
  public SpecificationBuilder<T> equal(
      final String column,
      final Object value,
      final Function<EqualsSpecification.Builder<T>, ObjectBuilder<EqualsSpecification<T>>> fn) {
    return equal(splitColumn(column), value, fn);
  }

  /**
   * Equal to.
   *
   * <p>Example: {@code ... LEFT JOIN table_join ... WHERE table_join.column_join = 1...}
   *
   * @param columns join column names are listed before the target one.
   * @param value value of predicate.
   */
  public SpecificationBuilder<T> equal(final List<String> columns, final Object value) {
    return equal(columns, value, EqualsSpecification.Builder::self);
  }

  /**
   * Equal to.
   *
   * <p>Example: {@code ... LEFT JOIN table_join ... WHERE table_join.column = 1...}
   *
   * @param columns join column names are listed before the target one.
   * @param value value of predicate.
   * @param fn function of the builder of additional predicate parameters.
   */
  public SpecificationBuilder<T> equal(
      final List<String> columns,
      final Object value,
      final Function<EqualsSpecification.Builder<T>, ObjectBuilder<EqualsSpecification<T>>> fn) {
    if (value == null) return this;

    final EqualsSpecification<T> spec =
        fn.apply(new EqualsSpecification.Builder<>(columns, value)).build();
    specifications.add(spec);
    return this;
  }

  /**
   * Equal to one of multiple possible values.
   *
   * <p>Example: {@code WHERE column IN (101, 103, 209)...}
   *
   * @param column column name.
   * @param values value of predicate.
   */
  public <V> SpecificationBuilder<T> in(final String column, final Collection<V> values) {
    return in(column, values, InSpecification.Builder::self);
  }

  /**
   * Equal to one of multiple possible values.
   *
   * <p>Example: {@code WHERE column IN (101, 103, 209)...}
   *
   * @param column column name.
   * @param values values of predicate.
   * @param fn function of the builder of additional predicate parameters.
   */
  public <V> SpecificationBuilder<T> in(
      final String column,
      final Collection<V> values,
      final Function<InSpecification.Builder<T, V>, ObjectBuilder<InSpecification<T, V>>> fn) {
    return in(splitColumn(column), values, fn);
  }

  /**
   * Equal to one of multiple possible values.
   *
   * <p>Example: {@code ... LEFT JOIN table_join ... WHERE table_join.column IN (101, 103, 209)...}
   *
   * @param columns join column names are listed before the target one.
   * @param values values of predicate.
   */
  public <V> SpecificationBuilder<T> in(final List<String> columns, final Collection<V> values) {
    return in(columns, values, InSpecification.Builder::self);
  }

  /**
   * Equal to one of multiple possible values.
   *
   * <p>Example: {@code ...LEFT JOIN table_join ... WHERE table_join.column IN (101, 103, 209)...}
   *
   * @param columns join column names are listed before the target one.
   * @param values values of predicate.
   * @param fn function of the builder of additional predicate parameters.
   */
  public <V> SpecificationBuilder<T> in(
      final List<String> columns,
      final Collection<V> values,
      final Function<InSpecification.Builder<T, V>, ObjectBuilder<InSpecification<T, V>>> fn) {
    if (values == null || values.isEmpty()) {
      return this;
    }

    final InSpecification<T, V> spec =
        fn.apply(new InSpecification.Builder<>(columns, values)).build();
    specifications.add(spec);
    return this;
  }

  /**
   * Contains a character pattern.
   *
   * <p>Example: {@code ... WHERE column LIKE '%Will%'...}
   *
   * @param column column name.
   * @param value value of predicate.
   */
  public SpecificationBuilder<T> like(final String column, final String value) {
    return like(column, value, LikeSpecification.Builder::self);
  }

  /**
   * Contains a character pattern.
   *
   * <p>Example: {@code ... WHERE column LIKE '%Will%'...}
   *
   * @param column column name.
   * @param value value of predicate.
   * @param fn function of the builder of additional predicate parameters.
   */
  public SpecificationBuilder<T> like(
      final String column,
      final String value,
      final Function<LikeSpecification.Builder<T>, ObjectBuilder<LikeSpecification<T>>> fn) {
    return like(splitColumn(column), value, fn);
  }

  /**
   * Contains a character pattern.
   *
   * <p>Example: {@code ... LEFT JOIN table_join ... WHERE table_join.column LIKE '%Will%'...}
   *
   * @param columns join column names are listed before the target one.
   * @param value value of predicate.
   */
  public SpecificationBuilder<T> like(final List<String> columns, final String value) {
    return like(columns, value, LikeSpecification.Builder::self);
  }

  /**
   * Contains a character pattern.
   *
   * <p>Example: {@code ... LEFT JOIN table_join ... WHERE table_join.column LIKE '%Will%'...}
   *
   * @param columns join column names are listed before the target one.
   * @param value value of predicate.
   * @param fn function of the builder of additional predicate parameters.
   */
  public SpecificationBuilder<T> like(
      final List<String> columns,
      final String value,
      final Function<LikeSpecification.Builder<T>, ObjectBuilder<LikeSpecification<T>>> fn) {
    if (value == null || value.isBlank()) return this;

    final LikeSpecification<T> spec =
        fn.apply(new LikeSpecification.Builder<>(columns, value)).build();

    final String trimValue = value.trim();
    if (trimValue.length() < spec.getMinChar()) {
      return this;
    }

    specifications.add(spec);
    return this;
  }

  // Comparison: BETWEEN, >, <, >=, <=

  /**
   * Minimum allowed value.
   *
   * <p>Examples:
   *
   * <ul>
   *   <li>{@code ... WHERE column > '2012-01-31' ...}
   *   <li>{@code ... WHERE column >= 2 ...}
   * </ul>
   *
   * @param column column name.
   */
  public <P extends Comparable<? super P>> SpecificationBuilder<T> min(
      final String column, final P min) {
    return between(column, min, null, BetweenSpecification.Builder::self);
  }

  /**
   * Minimum allowed value.
   *
   * <p>Examples:
   *
   * <ul>
   *   <li>{@code ... WHERE column > '2012-01-31' ...}
   *   <li>{@code ... WHERE column >= 2 ...}
   * </ul>
   *
   * @param column column name.
   * @param fn function of the builder of min and max values, additional predicate parameters.
   */
  public <P extends Comparable<? super P>> SpecificationBuilder<T> min(
      final String column,
      final P min,
      final Function<ComparisonSpecification.Builder<T, P>, ComparisonSpecification.Builder<T, P>>
          fn) {
    return between(column, min, null, fn);
  }

  /**
   * Minimum allowed value.
   *
   * <p>Examples:
   *
   * <ul>
   *   <li>{@code ... LEFT JOIN table_join ... WHERE table_join.column > '2012-01-31' ...}
   *   <li>{@code ... LEFT JOIN table_join ... WHERE table_join.column >= 2 ...}
   * </ul>
   *
   * @param columns join column names are listed before the target one.
   */
  public <P extends Comparable<? super P>> SpecificationBuilder<T> min(
      final List<String> columns, final P min) {
    return between(columns, min, null, BetweenSpecification.Builder::self);
  }

  /**
   * Minimum allowed value.
   *
   * <p>Examples:
   *
   * <ul>
   *   <li>{@code ... LEFT JOIN table_join ... WHERE table_join.column > '2012-01-31' ...}
   *   <li>{@code ... LEFT JOIN table_join ... WHERE table_join.column >= 2 ...}
   * </ul>
   *
   * @param columns join column names are listed before the target one.
   * @param fn function of the builder of min and max values, additional predicate parameters.
   */
  public <P extends Comparable<? super P>> SpecificationBuilder<T> min(
      final List<String> columns,
      final P min,
      final Function<ComparisonSpecification.Builder<T, P>, ComparisonSpecification.Builder<T, P>>
          fn) {
    return between(columns, min, null, fn);
  }

  /**
   * Maximum allowed value.
   *
   * <p>Examples:
   *
   * <ul>
   *   <li>{@code ... WHERE column < 50000.00 ...}
   *   <li>{@code ... WHERE column <= 0.05 ...}
   * </ul>
   *
   * @param column column name.
   */
  public <P extends Comparable<? super P>> SpecificationBuilder<T> max(
      final String column, final P max) {
    return between(column, null, max, ComparisonSpecification.Builder::self);
  }

  /**
   * Maximum allowed value.
   *
   * <p>Examples:
   *
   * <ul>
   *   <li>{@code ... WHERE column < 50000.00 ...}
   *   <li>{@code ... WHERE column <= 0.05 ...}
   * </ul>
   *
   * @param column column name.
   * @param fn function of the builder of min and max values, additional predicate parameters.
   */
  public <P extends Comparable<? super P>> SpecificationBuilder<T> max(
      final String column,
      final P max,
      final Function<ComparisonSpecification.Builder<T, P>, ComparisonSpecification.Builder<T, P>>
          fn) {
    return between(column, null, max, fn);
  }

  /**
   * Maximum allowed value.
   *
   * <p>Examples:
   *
   * <ul>
   *   <li>{@code ... LEFT JOIN table_join ... WHERE table_join.column < 50000.00 ...}
   *   <li>{@code ... LEFT JOIN table_join ... WHERE table_join.column <= 0.05 ...}
   * </ul>
   *
   * @param column column name.
   */
  public <P extends Comparable<? super P>> SpecificationBuilder<T> max(
      final List<String> column, final P max) {
    return between(column, null, max, ComparisonSpecification.Builder::self);
  }

  /**
   * Maximum allowed value.
   *
   * <p>Examples:
   *
   * <ul>
   *   <li>{@code ... LEFT JOIN table_join ... WHERE table_join.column < 50000.00 ...}
   *   <li>{@code ... LEFT JOIN table_join ... WHERE table_join.column <= 0.05 ...}
   * </ul>
   *
   * @param columns join column names are listed before the target one.
   * @param fn function of the builder of min and max values, additional predicate parameters.
   */
  public <P extends Comparable<? super P>> SpecificationBuilder<T> max(
      final List<String> columns,
      final P max,
      final Function<ComparisonSpecification.Builder<T, P>, ComparisonSpecification.Builder<T, P>>
          fn) {
    return between(columns, null, max, fn);
  }

  /**
   * Between the range, the extreme values can be infinite({@code null}).
   *
   * <p>Examples:
   *
   * <ul>
   *   <li>{@code ... WHERE column > '2012-01-31' ...}
   *   <li>{@code ... WHERE column < 50000.00 ...}
   *   <li>{@code ... WHERE column >= 2 ...}
   *   <li>{@code ... WHERE column <= 0.05 ...}
   *   <li>{@code ... WHERE column BETWEEN 100.00 AND 500.00 ...}
   * </ul>
   *
   * @param column column name.
   */
  public <P extends Comparable<? super P>> SpecificationBuilder<T> between(
      final String column, @Nullable final P min, @Nullable final P max) {
    return between(splitColumn(column), min, max, ComparisonSpecification.Builder::self);
  }

  /**
   * Between the range, the extreme values can be infinite({@code null}).
   *
   * <p>Examples:
   *
   * <ul>
   *   <li>{@code ... WHERE column > '2012-01-31' ...}
   *   <li>{@code ... WHERE column < 50000.00 ...}
   *   <li>{@code ... WHERE column >= 2 ...}
   *   <li>{@code ... WHERE column <= 0.05 ...}
   *   <li>{@code ... WHERE column BETWEEN 100.00 AND 500.00 ...}
   * </ul>
   *
   * @param column column name.
   * @param fn function of the builder of min and max values, additional predicate parameters.
   */
  public <P extends Comparable<? super P>> SpecificationBuilder<T> between(
      final String column,
      @Nullable final P min,
      @Nullable final P max,
      final Function<ComparisonSpecification.Builder<T, P>, ComparisonSpecification.Builder<T, P>>
          fn) {
    return between(splitColumn(column), min, max, fn);
  }

  /**
   * Between the range, the extreme values can be infinite({@code null}).
   *
   * <p>Examples:
   *
   * <ul>
   *   <li>{@code ... LEFT JOIN table_join ... WHERE table_join.column > '2012-01-31' ...}
   *   <li>{@code ... LEFT JOIN table_join ... WHERE table_join.column < 50000.00 ...}
   *   <li>{@code ... LEFT JOIN table_join ... WHERE table_join.column >= 2 ...}
   *   <li>{@code ... LEFT JOIN table_join ... WHERE table_join.column <= 0.05 ...}
   *   <li>{@code ... LEFT JOIN table_join ... WHERE table_join.column BETWEEN 100.00 AND 500.00
   *       ...}
   * </ul>
   *
   * @param columns join column names are listed before the target one.
   */
  public <P extends Comparable<? super P>> SpecificationBuilder<T> between(
      final List<String> columns, @Nullable final P min, @Nullable final P max) {
    return between(columns, min, max, ComparisonSpecification.Builder::self);
  }

  /**
   * Between the range, the extreme values can be infinite({@code null}).
   *
   * <p>Examples:
   *
   * <ul>
   *   <li>{@code ... LEFT JOIN table_join ... WHERE table_join.column > '2012-01-31' ...}
   *   <li>{@code ... LEFT JOIN table_join ... WHERE table_join.column < 50000.00 ...}
   *   <li>{@code ... LEFT JOIN table_join ... WHERE table_join.column >= 2 ...}
   *   <li>{@code ... LEFT JOIN table_join ... WHERE table_join.column <= 0.05 ...}
   *   <li>{@code ... LEFT JOIN table_join ... WHERE table_join.column BETWEEN 100.00 AND 500.00
   *       ...}
   * </ul>
   *
   * @param columns join column names are listed before the target one.
   * @param fn function of the builder of min and max values, additional predicate parameters.
   */
  public <P extends Comparable<? super P>> SpecificationBuilder<T> between(
      final List<String> columns,
      @Nullable final P min,
      @Nullable final P max,
      final Function<ComparisonSpecification.Builder<T, P>, ComparisonSpecification.Builder<T, P>>
          fn) {
    final ComparisonSpecification.Builder<T, P> builder =
        fn.apply(new ComparisonSpecification.Builder<>(columns, min, max));

    if (builder.isEmptyValues()) return this;
    specifications.addAll(builder.build());

    return this;
  }

  /**
   * Compare to not null.
   *
   * <p>Examples: {@code ... WHERE column IS NOT NULL ...}
   *
   * @param column column name.
   */
  public SpecificationBuilder<T> isNotNull(final String column) {
    return isNull(column, true, CompositeSpecification.Builder::not);
  }

  /**
   * Compare to not null.
   *
   * <p>Examples: {@code ... LEFT JOIN table_join ... WHERE table_join.column IS NOT NULL ...}
   *
   * @param columns join column names are listed before the target one.
   */
  public SpecificationBuilder<T> isNotNull(final List<String> columns) {
    return isNull(columns, true, CompositeSpecification.Builder::not);
  }

  /**
   * Compare to null (missing data).
   *
   * <p>Examples: {@code ... WHERE column IS NULL ...}
   *
   * @param column column name.
   */
  public SpecificationBuilder<T> isNull(final String column) {
    return isNull(column, true);
  }

  /**
   * Compare to null (missing data).
   *
   * <p>Examples: {@code ... WHERE column IS NULL ...}
   *
   * @param column column name.
   * @param active - activate the predicate?
   */
  public SpecificationBuilder<T> isNull(final String column, final Boolean active) {
    return isNull(column, active, NullSpecification.Builder::self);
  }

  /**
   * Compare to null (missing data).
   *
   * <p>Examples: {@code ... WHERE column IS NULL ...}
   *
   * @param column column name.
   * @param active - activate the predicate?
   * @param fn function of the builder of min and max values, additional predicate parameters.
   */
  public <P> SpecificationBuilder<T> isNull(
      final String column,
      final boolean active,
      final Function<NullSpecification.Builder<T, P>, ObjectBuilder<NullSpecification<T, P>>> fn) {
    return isNull(splitColumn(column), active, fn);
  }

  /**
   * Compare to null (missing data).
   *
   * <p>Examples: {@code ... LEFT JOIN table_join ... WHERE table_join.column IS NULL ...}
   *
   * @param columns join column names are listed before the target one.
   */
  public SpecificationBuilder<T> isNull(final List<String> columns) {
    return isNull(columns, true);
  }

  /**
   * Compare to null (missing data).
   *
   * <p>Examples: {@code ... LEFT JOIN table_join ... WHERE table_join.column IS NULL ...}
   *
   * @param columns join column names are listed before the target one.
   * @param active - activate the predicate?
   */
  public SpecificationBuilder<T> isNull(final List<String> columns, final Boolean active) {
    return isNull(columns, active, NullSpecification.Builder::self);
  }

  /**
   * Compare to null (missing data).
   *
   * <p>Examples: {@code ... LEFT JOIN table_join ... WHERE table_join.column IS NULL ...}
   *
   * @param columns join column names are listed before the target one.
   * @param active - activate the predicate?
   * @param fn function of the builder of min and max values, additional predicate parameters.
   */
  public <P> SpecificationBuilder<T> isNull(
      final List<String> columns,
      final Boolean active,
      final Function<NullSpecification.Builder<T, P>, ObjectBuilder<NullSpecification<T, P>>> fn) {
    if (!Boolean.TRUE.equals(active)) {
      return this;
    }

    final NullSpecification<T, P> spec = fn.apply(new NullSpecification.Builder<>(columns)).build();

    specifications.add(spec);
    return this;
  }

  private List<String> splitColumn(final String column) {
    return Arrays.asList(column.split("\\."));
  }

  /** Builds a {@link Specification}. */
  public Specification<T> build() {
    Specification<T> compositeSpec = Specification.unrestricted();
    if (specifications.isEmpty() && innerSpecifications.isEmpty()) {
      return compositeSpec;
    }

    for (final CompositeSpecification<T, ?> spec : specifications) {
      spec.setDistinct(distinct);
      compositeSpec = spec.connection.connect(compositeSpec, spec);
    }
    for (final InnerSpecification<T> inner : innerSpecifications) {
      compositeSpec = inner.operator.connect(compositeSpec, inner.spec);
    }

    return compositeSpec;
  }

  private record InnerSpecification<T>(Specification<T> spec, LogicalOperator operator) {}
}
