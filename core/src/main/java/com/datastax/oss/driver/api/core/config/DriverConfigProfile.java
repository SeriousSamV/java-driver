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

import edu.umd.cs.findbugs.annotations.NonNull;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

/**
 * A profile in the driver's configuration.
 *
 * <p>It is a collection of typed options.
 *
 * <p>Getters (such as {@link #getBoolean(DriverOption)}) are self-explanatory.
 *
 * <p>{@code withXxx} methods (such as {@link #withBoolean(DriverOption, boolean)}) create a
 * "derived" profile, which is an on-the-fly <b>copy</b> of the profile with the new value (which
 * might be a new option, or overwrite an existing one). If the original configuration is reloaded,
 * all derived profiles get updated as well. For best performance, such derived profiles should be
 * used sparingly; it is better to have built-in profiles for common scenarios.
 *
 * @see DriverConfig
 */
public interface DriverConfigProfile extends ProgrammaticBuilder<DriverConfigProfile> {

  /**
   * The name of the default profile (the string {@value}).
   *
   * <p>Named profiles can't use this name. If you try to declare such a profile, a runtime error
   * will be thrown.
   */
  String DEFAULT_NAME = "default";

  /**
   * The name of the profile in the configuration.
   *
   * <p>Derived profiles inherit the name of their parent.
   */
  @NonNull
  String getName();

  boolean isDefined(@NonNull DriverOption option);

  boolean getBoolean(@NonNull DriverOption option);

  @NonNull
  List<Boolean> getBooleanList(@NonNull DriverOption option);

  int getInt(@NonNull DriverOption option);

  @NonNull
  List<Integer> getIntList(@NonNull DriverOption option);

  long getLong(@NonNull DriverOption option);

  @NonNull
  List<Long> getLongList(@NonNull DriverOption option);

  double getDouble(@NonNull DriverOption option);

  @NonNull
  List<Double> getDoubleList(@NonNull DriverOption option);

  @NonNull
  String getString(@NonNull DriverOption option);

  @NonNull
  List<String> getStringList(@NonNull DriverOption option);

  @NonNull
  Map<String, String> getStringMap(@NonNull DriverOption option);

  /**
   * @return a size in bytes. This is separate from {@link #getLong(DriverOption)}, in case
   *     implementations want to allow users to provide sizes in a more human-readable way, for
   *     example "256 MB".
   */
  long getBytes(@NonNull DriverOption option);

  /** @see #getBytes(DriverOption) */
  @NonNull
  List<Long> getBytesList(@NonNull DriverOption option);

  @NonNull
  Duration getDuration(@NonNull DriverOption option);

  @NonNull
  List<Duration> getDurationList(@NonNull DriverOption option);

  /**
   * Returns a representation of all the child options under a given option.
   *
   * <p>This is only used to compare configuration sections across profiles, so the actual
   * implementation does not matter, as long as identical sections (same options with same values,
   * regardless of order) compare as equal and have the same {@code hashCode()}.
   */
  @NonNull
  Object getComparisonKey(@NonNull DriverOption option);

  /**
   * Enumerates all the entries in this profile, including those that were inherited from another
   * profile.
   *
   * <p>The keys are raw strings that match {@link DriverOption#getPath()}.
   *
   * <p>The values are implementation-dependent. With the driver's default implementation, the
   * possible types are {@code String}, {@code Number}, {@code Boolean}, {@code Map<String,Object>},
   * {@code List<Object>}, or {@code null}.
   */
  @NonNull
  SortedSet<Map.Entry<String, Object>> entrySet();
}
