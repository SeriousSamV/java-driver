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
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValueFactory;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class ProgrammaticDriverConfigLoader {

  private ProgrammaticDriverConfigLoader() {}

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {

    private Config config = ConfigFactory.empty();

    private Supplier<Config> fallbackSupplier = DefaultDriverConfigLoader.DEFAULT_CONFIG_SUPPLIER;

    private Builder() {}

    public Builder withBoolean(DriverOption option, boolean value) {
      return with(option, value);
    }

    public Builder withBoolean(String profileName, DriverOption option, boolean value) {
      return with(profileName, option, value);
    }

    public Builder withBooleanList(DriverOption option, List<Boolean> value) {
      return with(option, value);
    }

    public Builder withBooleanList(String profileName, DriverOption option, List<Boolean> value) {
      return with(profileName, option, value);
    }

    public Builder withInt(DriverOption option, int value) {
      return with(option, value);
    }

    public Builder withInt(String profileName, DriverOption option, int value) {
      return with(profileName, option, value);
    }

    public Builder withIntList(DriverOption option, List<Integer> value) {
      return with(option, value);
    }

    public Builder withIntList(String profileName, DriverOption option, List<Integer> value) {
      return with(profileName, option, value);
    }

    public Builder withLong(DriverOption option, long value) {
      return with(option, value);
    }

    public Builder withLong(String profileName, DriverOption option, long value) {
      return with(profileName, option, value);
    }

    public Builder withLongList(DriverOption option, List<Long> value) {
      return with(option, value);
    }

    public Builder withLongList(String profileName, DriverOption option, List<Long> value) {
      return with(profileName, option, value);
    }

    public Builder withDouble(DriverOption option, double value) {
      return with(option, value);
    }

    public Builder withDouble(String profileName, DriverOption option, double value) {
      return with(profileName, option, value);
    }

    public Builder withDoubleList(DriverOption option, List<Double> value) {
      return with(option, value);
    }

    public Builder withDoubleList(String profileName, DriverOption option, List<Double> value) {
      return with(profileName, option, value);
    }

    public Builder withString(DriverOption option, String value) {
      return with(option, value);
    }

    public Builder withString(String profileName, DriverOption option, String value) {
      return with(profileName, option, value);
    }

    public Builder withStringList(DriverOption option, List<String> value) {
      return with(option, value);
    }

    public Builder withStringList(String profileName, DriverOption option, List<String> value) {
      return with(option, value);
    }

    public Builder withStringMap(DriverOption option, Map<String, String> value) {
      for (String key : value.keySet()) {
        config =
            config.withValue(
                option.getPath() + '.' + key, ConfigValueFactory.fromAnyRef(value.get(key)));
      }
      return this;
    }

    public Builder withStringMap(
        String profileName, DriverOption option, Map<String, String> value) {
      for (String key : value.keySet()) {
        String path = "profiles." + profileName + '.' + option.getPath() + '.' + key;
        config = config.withValue(path, ConfigValueFactory.fromAnyRef(value.get(key)));
      }
      return this;
    }

    public Builder withBytes(DriverOption option, long value) {
      return with(option, value);
    }

    public Builder withBytes(String profileName, DriverOption option, long value) {
      return with(profileName, option, value);
    }

    public Builder withBytesList(DriverOption option, List<Long> value) {
      return with(option, value);
    }

    public Builder withBytesList(String profileName, DriverOption option, List<Long> value) {
      return with(profileName, option, value);
    }

    public Builder withDuration(DriverOption option, Duration value) {
      return with(option, value);
    }

    public Builder withDuration(String profileName, DriverOption option, Duration value) {
      return with(profileName, option, value);
    }

    public Builder withDurationList(DriverOption option, List<Duration> value) {
      return with(option, value);
    }

    public Builder withDurationList(String profileName, DriverOption option, List<Duration> value) {
      return with(profileName, option, value);
    }

    public Builder withClass(DriverOption option, Class<?> value) {
      return with(option, value.getName());
    }

    public Builder withClass(String profileName, DriverOption option, Class<?> value) {
      return with(profileName, option, value.getName());
    }

    /** Unsets an option. */
    public Builder without(DriverOption option) {
      return with(option, null);
    }

    public Builder without(String profileName, DriverOption option) {
      return with(profileName, option, null);
    }

    public Builder withFallback(Supplier<Config> configSupplier) {
      this.fallbackSupplier = configSupplier;
      return this;
    }

    public DriverConfigLoader build() {
      return new DefaultDriverConfigLoader(() -> config.withFallback(fallbackSupplier.get()));
    }

    public static Builder builder() {
      return new Builder();
    }

    private Builder with(DriverOption option, Object value) {
      this.config = config.withValue(option.getPath(), ConfigValueFactory.fromAnyRef(value));
      return this;
    }

    private Builder with(String profileName, DriverOption option, Object value) {
      this.config =
          config.withValue(
              "profiles." + profileName + '.' + option.getPath(),
              ConfigValueFactory.fromAnyRef(value));
      return this;
    }
  }
}
