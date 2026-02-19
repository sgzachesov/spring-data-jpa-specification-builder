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
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.io.Serial;
import java.util.List;

/**
 * Predicate of equal to(=).
 *
 * @param <T> the type of the {@link Root} the resulting {@literal Specification} operates on.
 */
public class EqualsSpecification<T> extends CompositeSpecification<T, Object> {

  @Serial private static final long serialVersionUID = 637979759818300347L;

  private final Object value;

  private EqualsSpecification(final Builder<T> builder) {
    super(builder);
    this.value = builder.value;
  }

  @Override
  Predicate toCriteriaPredicate(
      final Root<T> root, final CriteriaQuery<?> query, final CriteriaBuilder criteriaBuilder) {
    final Path<Object> path = getPath(root);

    return isNot ? criteriaBuilder.notEqual(path, value) : criteriaBuilder.equal(path, value);
  }

  /**
   * Builder for {@link EqualsSpecification}.
   *
   * @param <T> the type of the {@link Root} the resulting {@literal Specification} operates on.
   */
  public static class Builder<T> extends CompositeSpecification.Builder<Builder<T>>
      implements ObjectBuilder<EqualsSpecification<T>> {
    private final Object value;

    Builder(final List<String> columns, final Object value) {
      super(columns);
      this.value = value;
    }

    @Override
    public EqualsSpecification<T> build() {
      return new EqualsSpecification<>(this);
    }

    @Override
    protected Builder<T> self() {
      return this;
    }
  }
}
