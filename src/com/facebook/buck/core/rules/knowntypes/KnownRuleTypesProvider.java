/*
 * Copyright 2018-present Facebook, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.facebook.buck.core.rules.knowntypes;

import com.facebook.buck.core.cell.Cell;
import com.google.common.base.Throwables;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.UncheckedExecutionException;
import javax.annotation.Nonnull;

/** Lazily constructs {@link KnownNativeRuleTypes} for {@link Cell}s. */
public class KnownRuleTypesProvider {

  private final LoadingCache<Cell, KnownNativeRuleTypes> typesCache =
      CacheBuilder.newBuilder()
          .build(
              new CacheLoader<Cell, KnownNativeRuleTypes>() {
                @Override
                public KnownNativeRuleTypes load(@Nonnull Cell cell) {
                  return knownNativeRuleTypesFactory.create(cell);
                }
              });

  private final KnownNativeRuleTypesFactory knownNativeRuleTypesFactory;

  public KnownRuleTypesProvider(KnownNativeRuleTypesFactory knownNativeRuleTypesFactory) {
    this.knownNativeRuleTypesFactory = knownNativeRuleTypesFactory;
  }

  /** Returns {@link KnownNativeRuleTypes} for a given {@link Cell}. */
  public KnownNativeRuleTypes get(Cell cell) {
    try {
      return typesCache.getUnchecked(cell);
    } catch (UncheckedExecutionException e) {
      Throwables.throwIfUnchecked(e.getCause());
      throw e;
    }
  }
}
