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
package com.datastax.oss.driver.internal.core.cql;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.config.DriverConfigProfile;
import com.datastax.oss.driver.api.core.cql.PrepareRequest;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.core.metadata.token.Token;
import java.nio.ByteBuffer;
import java.util.Map;
import net.jcip.annotations.Immutable;

/**
 * Default implementation of a prepare request, which is built internally to handle calls such as
 * {@link CqlSession#prepare(String)} and {@link CqlSession#prepare(SimpleStatement)}.
 *
 * <p>When built from a {@link SimpleStatement}, it propagates the attributes to bound statements
 * according to the rules described in {@link CqlSession#prepare(SimpleStatement)}. The prepare
 * request itself:
 *
 * <ul>
 *   <li>will use the same configuration profile (or configuration profile name) as the {@code
 *       SimpleStatement};
 *   <li>will use the same custom payload as the {@code SimpleStatement};
 * </ul>
 */
@Immutable
public class DefaultPrepareRequest implements PrepareRequest {

  private final SimpleStatement statement;

  public DefaultPrepareRequest(SimpleStatement statement) {
    this.statement = statement;
  }

  public DefaultPrepareRequest(String query) {
    this.statement = SimpleStatement.newInstance(query);
  }

  @Override
  public String getQuery() {
    return statement.getQuery();
  }

  @Override
  public String getConfigProfileName() {
    return statement.getConfigProfileName();
  }

  @Override
  public DriverConfigProfile getConfigProfile() {
    return statement.getConfigProfile();
  }

  @Override
  public CqlIdentifier getKeyspace() {
    return statement.getKeyspace();
  }

  @Override
  public CqlIdentifier getRoutingKeyspace() {
    // Prepare requests do not operate on a particular partition, token-aware routing doesn't apply.
    return null;
  }

  @Override
  public ByteBuffer getRoutingKey() {
    return null;
  }

  @Override
  public Token getRoutingToken() {
    return null;
  }

  @Override
  public Map<String, ByteBuffer> getCustomPayload() {
    return statement.getCustomPayload();
  }

  @Override
  public String getConfigProfileNameForBoundStatements() {
    return statement.getConfigProfileName();
  }

  @Override
  public DriverConfigProfile getConfigProfileForBoundStatements() {
    return statement.getConfigProfile();
  }

  @Override
  public ByteBuffer getPagingStateForBoundStatements() {
    return statement.getPagingState();
  }

  @Override
  public CqlIdentifier getRoutingKeyspaceForBoundStatements() {
    return (statement.getKeyspace() != null)
        ? statement.getKeyspace()
        : statement.getRoutingKeyspace();
  }

  @Override
  public ByteBuffer getRoutingKeyForBoundStatements() {
    return statement.getRoutingKey();
  }

  @Override
  public Token getRoutingTokenForBoundStatements() {
    return statement.getRoutingToken();
  }

  @Override
  public Map<String, ByteBuffer> getCustomPayloadForBoundStatements() {
    return statement.getCustomPayload();
  }

  @Override
  public Boolean areBoundStatementsIdempotent() {
    return statement.isIdempotent();
  }

  @Override
  public boolean areBoundStatementsTracing() {
    return statement.isTracing();
  }
}
