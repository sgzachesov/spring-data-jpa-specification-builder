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

import jakarta.persistence.ElementCollection;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Fetch;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.io.Serial;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Setter;
import org.jspecify.annotations.Nullable;
import org.springframework.data.jpa.domain.Specification;

/**
 * An abstract aggregating class that describes the basic properties and behaviors of predicates.
 *
 * <p><a href="https://martinfowler.com/apsupp/spec.pdf">Specifications pattern</a>
 *
 * @param <T> the type of the {@link Root} the resulting {@literal Specification} operates on.
 * @param <P> target predicate type, maybe {@link Join}
 */
public abstract class CompositeSpecification<T, P> implements Specification<T> {

  @Serial private static final long serialVersionUID = -5778517129027250693L;

  protected final List<String> columns;
  protected final boolean isNot;
  protected final JoinType joinType;
  LogicalOperator connection;
  private final boolean isFetch;

  @Setter(AccessLevel.PACKAGE)
  private boolean distinct = true;

  protected <BuilderT extends Builder<BuilderT>> CompositeSpecification(
      final Builder<BuilderT> builder) {
    this.columns = builder.columns;
    this.connection = builder.connection;
    this.isNot = builder.isNot;
    this.joinType = builder.joinType;
    this.isFetch = builder.isFetch;
  }

  @Override
  public final Predicate toPredicate(
      @Nullable final Root<T> root,
      @Nullable final CriteriaQuery<?> query,
      @Nullable final CriteriaBuilder criteriaBuilder) {
    if (query != null) {
      query.distinct(distinct);
    }
    return toCriteriaPredicate(root, query, criteriaBuilder);
  }

  abstract Predicate toCriteriaPredicate(
      Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder);

  @SuppressWarnings("unchecked")
  protected Path<P> getPath(final Root<T> root) {
    Path<P> path = null;
    From<?, ?> from = root;
    Class<?> javaType = root.getJavaType();

    for (final String column : columns) {
      if (isObjectAssociation(column, javaType)) {
        final Optional<Join<?, ?>> joinOpt = getJoin(root.getJoins(), column);
        from = joinOpt.isPresent() ? joinOpt.get() : joinFetch(from, column);
        javaType = from.getJavaType();

      } else if (isElementCollection(column, javaType)) {
        path = from.join(column);
        break;
      } else {
        path = from.get(column);
        break;
      }
    }
    if (path == null) {
      path = (Path<P>) from;
    }

    return path;
  }

  private boolean isObjectAssociation(final String column, final Class<?> javaType) {
    final Field[] fields = javaType.getDeclaredFields();
    final Field field =
        Arrays.stream(fields).filter(f -> f.getName().equals(column)).findFirst().orElse(null);
    if (field == null) return false;
    return field.isAnnotationPresent(OneToOne.class)
        || field.isAnnotationPresent(ManyToOne.class)
        || field.isAnnotationPresent(OneToMany.class)
        || field.isAnnotationPresent(ManyToMany.class);
  }

  private boolean isElementCollection(final String column, final Class<?> javaType) {
    final Field[] fields = javaType.getDeclaredFields();
    final Field field =
        Arrays.stream(fields).filter(f -> f.getName().equals(column)).findFirst().orElse(null);
    return field != null && field.isAnnotationPresent(ElementCollection.class);
  }

  private Optional<Join<?, ?>> getJoin(final Set<? extends Join<?, ?>> joins, final String column) {
    final Optional<Join<?, ?>> result = Optional.empty();
    if (joins.isEmpty()) return result;

    for (final Join<?, ?> join : joins) {
      if (join.getAttribute().getName().equals(column)) {
        return Optional.of(join);
      }
    }

    return result;
  }

  private Join<?, ?> joinFetch(final From<?, ?> from, final String column) {
    if (isFetch) {
      return (Join<?, ?>) from.fetch(column, joinType);
    } else {
      return from.join(column, joinType);
    }
  }

  /**
   * Common abstract builder for {@link CompositeSpecification}.
   *
   * @param <BuilderT> the concrete builder class type.
   */
  public abstract static class Builder<BuilderT extends Builder<BuilderT>> {

    protected final List<String> columns;
    private LogicalOperator connection = LogicalOperator.AND;
    private boolean isNot;
    private JoinType joinType = JoinType.INNER;
    private boolean isFetch;

    Builder(final List<String> columns) {
      this.columns = columns;
    }

    /**
     * Sets the logical operator for combining multiple filter conditions.
     *
     * @param connection boolean operator to use for connecting conditions (AND/OR)
     */
    public BuilderT connection(final LogicalOperator connection) {
      this.connection = connection;
      return self();
    }

    /**
     * Sets the type of join to be used when fetching associated entities.
     *
     * @param joinType the type of join to use {@link JoinType}
     */
    public BuilderT join(final JoinType joinType) {
      this.joinType = joinType;
      return self();
    }

    /** Enables fetch join({@link Fetch}). */
    public BuilderT fetch() {
      this.isFetch = true;
      return self();
    }

    /** Adds NOT to the condition. */
    public BuilderT not() {
      this.isNot = true;
      return self();
    }

    protected abstract BuilderT self();
  }
}
