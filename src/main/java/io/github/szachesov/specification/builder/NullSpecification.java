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
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.io.Serial;
import java.util.List;

/**
 * Predicate of compare to null (IS NULL).
 *
 * @param <T> the type of the {@link Root} the resulting {@literal Specification} operates on.
 * @param <P> target predicate type, maybe {@link Join}
 */
public class NullSpecification<T, P> extends CompositeSpecification<T, P> {

  @Serial private static final long serialVersionUID = 3240395376802943186L;

  private NullSpecification(final Builder<T, P> builder) {
    super(builder);
  }

  @Override
  Predicate toCriteriaPredicate(
      final Root<T> root, final CriteriaQuery<?> query, final CriteriaBuilder criteriaBuilder) {

    final Path<?> path = getPath(root);
    return isNot ? path.isNotNull() : path.isNull();
  }

  /**
   * Builder for {@link NullSpecification}.
   *
   * @param <T> the type of the {@link Root} the resulting {@literal Specification} operates on.
   * @param <P> target predicate type, maybe {@link Join}
   */
  public static class Builder<T, P> extends CompositeSpecification.Builder<Builder<T, P>>
      implements ObjectBuilder<NullSpecification<T, P>> {

    Builder(final List<String> columns) {
      super(columns);
    }

    @Override
    public NullSpecification<T, P> build() {
      this.join(JoinType.LEFT);
      return new NullSpecification<>(this);
    }

    @Override
    protected Builder<T, P> self() {
      return this;
    }
  }
}
