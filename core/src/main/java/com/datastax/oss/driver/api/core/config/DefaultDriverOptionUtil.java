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

import java.time.Duration;
import java.util.List;
import java.util.Map;

public class DefaultDriverOptionUtil {
  /**
   * Fetch a boolean from the {@link DriverConfigProfile} if it exists, otherwise return provided
   * default.
   *
   * @param configProfile to fetch from.
   * @param option to fetch.
   * @param defaultValue fallback value if the option doesn't exist.
   * @return the specified boolean option, or the default if it's not defined.
   */
  public static boolean getConfigBooleanIfDefined(
      DriverConfigProfile configProfile, DefaultDriverOption option, boolean defaultValue) {
    return configProfile.isDefined(option) ? configProfile.getBoolean(option) : defaultValue;
  }
  /**
   * Fetch a Boolean List from the {@link DriverConfigProfile} if it exists, otherwise return
   * provided default.
   *
   * @param configProfile to fetch from.
   * @param option to fetch.
   * @param defaultValue fallback value if the option doesn't exist.
   * @return the specified Boolean List option, or the default if it's not defined.
   */
  public static List<Boolean> getConfigBooleanListIfDefined(
      DriverConfigProfile configProfile, DefaultDriverOption option, List<Boolean> defaultValue) {
    return configProfile.isDefined(option) ? configProfile.getBooleanList(option) : defaultValue;
  }

  /**
   * Fetch a byte from the {@link DriverConfigProfile} if it exists, otherwise return provided
   * default.
   *
   * @param configProfile to fetch from.
   * @param option to fetch.
   * @param defaultValue fallback value if the option doesn't exist.
   * @return the specified byte option, or the default if it's not defined.
   */
  public static long getConfigBytesIfDefined(
      DriverConfigProfile configProfile, DefaultDriverOption option, long defaultValue) {
    return configProfile.isDefined(option) ? configProfile.getBytes(option) : defaultValue;
  }

  /**
   * Fetch a Byte List from the {@link DriverConfigProfile} if it exists, otherwise return provided
   * default.
   *
   * @param configProfile to fetch from.
   * @param option to fetch.
   * @param defaultValue fallback value if the option doesn't exist.
   * @return the specified Byte List option, or the default if it's not defined.
   */
  public static List<Long> getConfigBytesListIfDefined(
      DriverConfigProfile configProfile, DefaultDriverOption option, List<Long> defaultValue) {
    return configProfile.isDefined(option) ? configProfile.getBytesList(option) : defaultValue;
  }

  /**
   * Fetch a double from the {@link DriverConfigProfile} if it exists, otherwise return provided
   * default.
   *
   * @param configProfile to fetch from.
   * @param option to fetch.
   * @param defaultValue fallback value if the option doesn't exist.
   * @return the specified double option, or the default if it's not defined.
   */
  public static double getConfigDoubleIfDefined(
      DriverConfigProfile configProfile, DefaultDriverOption option, double defaultValue) {
    return configProfile.isDefined(option) ? configProfile.getDouble(option) : defaultValue;
  }
  /**
   * Fetch a Double List from the {@link DriverConfigProfile} if it exists, otherwise return
   * provided default.
   *
   * @param configProfile to fetch from.
   * @param option to fetch.
   * @param defaultValue fallback value if the option doesn't exist.
   * @return the specified Double List option, or the default if it's not defined.
   */
  public static List<Double> getConfigDoubleListIfDefined(
      DriverConfigProfile configProfile, DefaultDriverOption option, List<Double> defaultValue) {
    return configProfile.isDefined(option) ? configProfile.getDoubleList(option) : defaultValue;
  }
  /**
   * Fetch a Duration in the form of a long from the {@link DriverConfigProfile} if it exists,
   * otherwise return provided default.
   *
   * @param configProfile to fetch from.
   * @param option to fetch.
   * @param defaultValue fallback value if the option doesn't exist.
   * @return the specified duration option, or the default if it's not defined.
   */
  public static long getConfigDurationIfDefined(
      DriverConfigProfile configProfile, DefaultDriverOption option, long defaultValue) {
    return configProfile.isDefined(option)
        ? configProfile.getDuration(option).toNanos()
        : defaultValue;
  }
  /**
   * Fetch a Duration List from the {@link DriverConfigProfile} if it exists, otherwise return
   * provided default.
   *
   * @param configProfile to fetch from.
   * @param option to fetch.
   * @param defaultValue fallback value if the option doesn't exist.
   * @return the specified Duration List option, or the default if it's not defined.
   */
  public static List<Duration> getConfigDurationListIfDefined(
      DriverConfigProfile configProfile, DefaultDriverOption option, List<Duration> defaultValue) {
    return configProfile.isDefined(option) ? configProfile.getDurationList(option) : defaultValue;
  }
  /**
   * Fetch an int from the {@link DriverConfigProfile} if it exists, otherwise return provided
   * default.
   *
   * @param configProfile to fetch from.
   * @param option to fetch.
   * @param defaultValue fallback value if the option doesn't exist.
   * @return the specified int option, or the default if it's not defined.
   */
  public static int getConfigIntIfDefined(
      DriverConfigProfile configProfile, DefaultDriverOption option, int defaultValue) {
    return configProfile.isDefined(option) ? configProfile.getInt(option) : defaultValue;
  }

  /**
   * Fetch an Integer List from the {@link DriverConfigProfile} if it exists, otherwise return
   * provided default.
   *
   * @param configProfile to fetch from.
   * @param option to fetch.
   * @param defaultValue fallback value if the option doesn't exist.
   * @return the specified Integer List option, or the default if it's not defined.
   */
  public static List<Integer> getConfigIntListIfDefined(
      DriverConfigProfile configProfile, DefaultDriverOption option, List<Integer> defaultValue) {
    return configProfile.isDefined(option) ? configProfile.getIntList(option) : defaultValue;
  }
  /**
   * Fetch a long from the {@link DriverConfigProfile} if it exists, otherwise return provided
   * default.
   *
   * @param configProfile to fetch from.
   * @param option to fetch.
   * @param defaultValue fallback value if the option doesn't exist.
   * @return the specified long option, or the default if it's not defined.
   */
  public static long getConfigLongIfDefined(
      DriverConfigProfile configProfile, DefaultDriverOption option, long defaultValue) {
    return configProfile.isDefined(option) ? configProfile.getLong(option) : defaultValue;
  }
  /**
   * Fetch a Long List from the {@link DriverConfigProfile} if it exists, otherwise return provided
   * default.
   *
   * @param configProfile to fetch from.
   * @param option to fetch.
   * @param defaultValue fallback value if the option doesn't exist.
   * @return the specified Long List option, or the default if it's not defined.
   */
  public static List<Long> getConfigLongListIfDefined(
      DriverConfigProfile configProfile, DefaultDriverOption option, List<Long> defaultValue) {
    return configProfile.isDefined(option) ? configProfile.getLongList(option) : defaultValue;
  }
  /**
   * Fetch a String from the {@link DriverConfigProfile} if it exists, otherwise return provided
   * default.
   *
   * @param configProfile to fetch from.
   * @param option to fetch.
   * @param defaultValue fallback value if the option doesn't exist.
   * @return the specified String option, or the default if it's not defined.
   */
  public static String getConfigStringIfDefined(
      DriverConfigProfile configProfile, DefaultDriverOption option, String defaultValue) {
    return configProfile.isDefined(option) ? configProfile.getString(option) : defaultValue;
  }
  /**
   * Fetch a String List from the {@link DriverConfigProfile} if it exists, otherwise return
   * provided default.
   *
   * @param configProfile to fetch from.
   * @param option to fetch.
   * @param defaultValue fallback value if the option doesn't exist.
   * @return the specified String List option, or the default if it's not defined.
   */
  public static List<String> getConfigStringListIfDefined(
      DriverConfigProfile configProfile, DefaultDriverOption option, List<String> defaultValue) {
    return configProfile.isDefined(option) ? configProfile.getStringList(option) : defaultValue;
  }
  /**
   * Fetch a String Map from the {@link DriverConfigProfile} if it exists, otherwise return provided
   * default.
   *
   * @param configProfile to fetch from.
   * @param option to fetch.
   * @param defaultValue fallback value if the option doesn't exist.
   * @return the specified String Map option, or the default if it's not defined.
   */
  public static Map<String, String> getConfigStringMapIfDefined(
      DriverConfigProfile configProfile,
      DefaultDriverOption option,
      Map<String, String> defaultValue) {
    return configProfile.isDefined(option) ? configProfile.getStringMap(option) : defaultValue;
  }
}
