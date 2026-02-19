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

import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

/** Enumeration of boolean operators for combining specifications. */
@AllArgsConstructor
public enum LogicalOperator {
  AND {
    @Override
    <T> Specification<T> connect(final Specification<T> spec, final Specification<T> connect) {
      return spec.and(connect);
    }
  },
  OR {
    @Override
    <T> Specification<T> connect(final Specification<T> spec, final Specification<T> connect) {
      return spec.or(connect);
    }
  };

  abstract <T> Specification<T> connect(Specification<T> spec, Specification<T> connect);
}
