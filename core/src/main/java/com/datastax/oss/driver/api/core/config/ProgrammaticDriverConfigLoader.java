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

import com.datastax.oss.driver.internal.core.config.typesafe.DefaultDriverConfigLoader;
import com.datastax.oss.driver.shaded.guava.common.collect.ImmutableSortedSet;
import com.datastax.oss.protocol.internal.util.collection.NullAllowingImmutableMap;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValueFactory;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import java.util.AbstractMap;
import java.util.Comparator;
import java.util.Map;
import java.util.SortedSet;

/**
 * Provides a mechanism for constructing a {@link DriverConfigLoader} programmatically as an
 * alternative, or in addition to, the default configuration mechanism which is to load
 * configuration from configuration files using typesafe config.
 *
 * <p>The built {@link DriverConfigLoader} provided by {@link Builder#build()} can be passed to
 * {@link
 * com.datastax.oss.driver.api.core.session.SessionBuilder#withConfigLoader(DriverConfigLoader)}.
 *
 * <p>Configuration provided to {@link Builder}'s {@code withXXX} methods will override the values
 * provided in the base configuration.
 */
public final class ProgrammaticDriverConfigLoader {

  private ProgrammaticDriverConfigLoader() {}

  /** @return a new {@link Builder} to provide programmatic configuration. */
  public static Builder builder() {
    return new Builder();
  }

  /**
   * @return a new {@link ProfileBuilder} to provide programmatic configuration at a profile level.
   * @see Builder#withProfile(String, Profile)
   */
  public static ProfileBuilder profileBuilder() {
    return new ProfileBuilder();
  }

  public static final class Builder implements ProgrammaticConfigBuilder<Builder> {

    private NullAllowingImmutableMap.Builder<String, Object> values =
        NullAllowingImmutableMap.builder();

    private Builder() {}

    /**
     * @return Constructs a config loader based on the configuration values provided to this builder
     *     and falls back to the default configuration mechanism of the driver.
     */
    public DriverConfigLoader build() {
      // build config from map
      Config config = ConfigFactory.empty();
      for (Map.Entry<String, Object> entry : values.build().entrySet()) {
        config = config.withValue(entry.getKey(), ConfigValueFactory.fromAnyRef(entry.getValue()));
      }

      // fallback on the default config supplier (config file)
      final Config fConfig = config;
      return new DefaultDriverConfigLoader(
          () -> fConfig.withFallback(DefaultDriverConfigLoader.DEFAULT_CONFIG_SUPPLIER.get()));
    }

    /**
     * @return All configured entries as a map. This is useful for cases where you want to build an
     *     alternate {@link DriverConfigLoader} taking into the account the configuration provided
     *     to this builder.
     */
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
    public Builder with(@NonNull String path, @Nullable Object value) {
      values.put(path, value);
      return this;
    }

    /** Adds configuration for a profile constructed using {@link #profileBuilder()} by name. */
    public Builder withProfile(String profileName, Profile profile) {
      String prefix = "profiles." + profileName + ".";
      for (Map.Entry<String, Object> entry : profile.values.entrySet()) {
        this.with(prefix + entry.getKey(), entry.getValue());
      }
      return this;
    }
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
