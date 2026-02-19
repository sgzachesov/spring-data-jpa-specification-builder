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
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.io.Serial;
import java.util.List;
import java.util.Locale;
import lombok.Getter;

/**
 * Predicate of like.
 *
 * @param <T> the type of the {@link Root} the resulting {@literal Specification} operates on.
 */
public class LikeSpecification<T> extends CompositeSpecification<T, String> {

  @Serial private static final long serialVersionUID = -4074284076173199097L;

  private String value;
  private final boolean isIgnoreCase;
  private final Wildcard wildcard;
  @Getter private final int minChar;

  private LikeSpecification(final Builder<T> builder) {
    super(builder);
    this.value = builder.value;
    this.isIgnoreCase = builder.isIgnoreCase;
    this.wildcard = builder.wildcard;
    this.minChar = builder.minChar;
  }

  @Override
  Predicate toCriteriaPredicate(
      final Root<T> root, final CriteriaQuery<?> query, final CriteriaBuilder builder) {
    final Path<String> path = getPath(root);

    final Expression<String> expression;
    if (isIgnoreCase) {
      expression = builder.upper(path);
      value = value.toUpperCase(Locale.ROOT);
    } else {
      expression = path;
    }

    return builder.like(expression, wildcard.getWithWildcard().apply(value));
  }

  /**
   * Builder for {@link LikeSpecification}.
   *
   * @param <T> the type of the {@link Root} the resulting {@literal Specification} operates on.
   */
  public static class Builder<T> extends CompositeSpecification.Builder<Builder<T>>
      implements ObjectBuilder<LikeSpecification<T>> {

    private final String value;
    private boolean isIgnoreCase = true;
    private Wildcard wildcard = Wildcard.ABSENCE;
    private int minChar = 3;

    Builder(final List<String> columns, final String value) {
      super(columns);
      this.value = value;
    }

    /** Case-insensitive comparison. */
    public Builder<T> noIgnoreCase() {
      this.isIgnoreCase = false;
      return this;
    }

    /** The location of the SQL wildcard {@link Wildcard}. */
    public Builder<T> wildcard(final Wildcard wildcard) {
      this.wildcard = wildcard;
      return this;
    }

    /**
     * The minimum number of characters to compare. If the number of characters in a word is less,
     * the predicate is ignored.
     */
    public Builder<T> minChar(final int minChar) {
      this.minChar = minChar;
      return this;
    }

    @Override
    public LikeSpecification<T> build() {
      return new LikeSpecification<>(this);
    }

    @Override
    protected Builder<T> self() {
      return this;
    }
  }
}
