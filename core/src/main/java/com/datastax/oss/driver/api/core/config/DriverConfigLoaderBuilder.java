/*
 * Copyright DataStax, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.datastax.oss.driver.api.core.config;

import com.datastax.oss.driver.shaded.guava.common.collect.ImmutableSortedSet;
import com.datastax.oss.protocol.internal.util.collection.NullAllowingImmutableMap;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import java.util.AbstractMap;
import java.util.Comparator;
import java.util.Map;
import java.util.SortedSet;
import net.jcip.annotations.NotThreadSafe;

/**
 * Provides a mechanism for constructing a {@link DriverConfigLoader} programmatically.
 *
 * <p>The built {@link DriverConfigLoader} provided by {@link #build()} can be passed to {@link
 * com.datastax.oss.driver.api.core.session.SessionBuilder#withConfigLoader(DriverConfigLoader)}.
 */
@NotThreadSafe
public abstract class DriverConfigLoaderBuilder<SelfT extends DriverConfigLoaderBuilder<SelfT>>
    implements ProgrammaticConfigBuilder<SelfT> {

  @SuppressWarnings("unchecked")
  protected final SelfT self = (SelfT) this;

  /**
   * @return a new {@link ProfileBuilder} to provide programmatic configuration at a profile level.
   * @see #withProfile(String, Profile)
   */
  @NonNull
  public static ProfileBuilder profileBuilder() {
    return new ProfileBuilder();
  }

  private NullAllowingImmutableMap.Builder<String, Object> values =
      NullAllowingImmutableMap.builder();

  /**
   * @return constructed {@link DriverConfigLoader} using the configuration passed into this
   *     builder.
   */
  @NonNull
  public abstract DriverConfigLoader build();

  /** @return All configured entries as a map. */
  @NonNull
  public SortedSet<Map.Entry<String, Object>> entrySet() {
    ImmutableSortedSet.Builder<Map.Entry<String, Object>> builder =
        ImmutableSortedSet.orderedBy(Comparator.comparing(Map.Entry::getKey));
    for (Map.Entry<String, Object> entry : values.build().entrySet()) {
      builder.add(new AbstractMap.SimpleEntry<>(entry.getKey(), entry.getValue()));
    }
    return builder.build();
  }

  @NonNull
  @Override
  public SelfT with(@NonNull String path, @Nullable Object value) {
    values.put(path, value);
    return self;
  }

  /** Adds configuration for a profile constructed using {@link #profileBuilder()} by name. */
  @NonNull
  public SelfT withProfile(String profileName, Profile profile) {
    String prefix = "profiles." + profileName + ".";
    for (Map.Entry<String, Object> entry : profile.values.entrySet()) {
      this.with(prefix + entry.getKey(), entry.getValue());
    }
    return self;
  }

  public static final class ProfileBuilder implements ProgrammaticConfigBuilder<ProfileBuilder> {

    final NullAllowingImmutableMap.Builder<String, Object> values =
        NullAllowingImmutableMap.builder();

    private ProfileBuilder() {}

    @NonNull
    @Override
    public ProfileBuilder with(@NonNull String path, @Nullable Object value) {
      values.put(path, value);
      return this;
    }

    @NonNull
    public Profile build() {
      return new Profile(values.build());
    }
  }

  public static final class Profile {

    final Map<String, Object> values;

    private Profile(Map<String, Object> values) {
      this.values = values;
    }
  }
}
