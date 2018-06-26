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
package com.datastax.oss.driver.api.core;

import com.datastax.oss.driver.api.core.config.DriverConfigLoader;
import com.datastax.oss.driver.api.core.config.DriverConfigLoaderBuilder;
import com.datastax.oss.driver.internal.core.config.typesafe.DefaultDriverConfigLoader;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValueFactory;
import edu.umd.cs.findbugs.annotations.NonNull;
import java.util.Map;
import net.jcip.annotations.NotThreadSafe;

/**
 * Provides a mechanism for constructing a {@link DriverConfigLoader} programmatically that uses
 * {@link com.datastax.oss.driver.api.core.CqlSession}'s default config loader with the values of
 * {@code withXXX(...)} methods overriding the configuration defined in configuration files.
 *
 * <p>The built {@link DriverConfigLoader} provided by {@link #build()} can be passed to {@link
 * com.datastax.oss.driver.api.core.session.SessionBuilder#withConfigLoader(DriverConfigLoader)}.
 */
@NotThreadSafe
public class CqlDriverConfigLoaderBuilder
    extends DriverConfigLoaderBuilder<CqlDriverConfigLoaderBuilder> {

  @Override
  @NonNull
  public DriverConfigLoader build() {
    // build config from map
    Config config = ConfigFactory.empty();
    for (Map.Entry<String, Object> entry : entrySet()) {
      config = config.withValue(entry.getKey(), ConfigValueFactory.fromAnyRef(entry.getValue()));
    }

    // fallback on the default config supplier (config file)
    final Config fConfig = config;
    return new DefaultDriverConfigLoader(
        () -> fConfig.withFallback(DefaultDriverConfigLoader.DEFAULT_CONFIG_SUPPLIER.get()));
  }
}
