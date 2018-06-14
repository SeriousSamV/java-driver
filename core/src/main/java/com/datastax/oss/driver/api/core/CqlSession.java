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

import com.datastax.oss.driver.api.core.cql.AsyncResultSet;
import com.datastax.oss.driver.api.core.cql.PrepareRequest;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.core.cql.Statement;
import com.datastax.oss.driver.api.core.session.Request;
import com.datastax.oss.driver.api.core.session.Session;
import com.datastax.oss.driver.internal.core.cql.DefaultPrepareRequest;
import java.util.concurrent.CompletionStage;

/** A specialized session with convenience methods to execute CQL statements. */
public interface CqlSession extends Session {

  /** Returns a builder to create a new instance. */
  static CqlSessionBuilder builder() {
    return new CqlSessionBuilder();
  }

  /**
   * Executes a CQL statement synchronously (the calling thread blocks until the result becomes
   * available).
   */
  default ResultSet execute(Statement<?> statement) {
    return execute(statement, Statement.SYNC);
  }

  /**
   * Executes a CQL statement synchronously (the calling thread blocks until the result becomes
   * available).
   */
  default ResultSet execute(String query) {
    return execute(SimpleStatement.newInstance(query));
  }

  /**
   * Executes a CQL statement asynchronously (the call returns as soon as the statement was sent,
   * generally before the result is available).
   */
  default CompletionStage<AsyncResultSet> executeAsync(Statement<?> statement) {
    return execute(statement, Statement.ASYNC);
  }

  /**
   * Executes a CQL statement asynchronously (the call returns as soon as the statement was sent,
   * generally before the result is available).
   */
  default CompletionStage<AsyncResultSet> executeAsync(String query) {
    return executeAsync(SimpleStatement.newInstance(query));
  }

  /**
   * Prepares a CQL statement synchronously (the calling thread blocks until the statement is
   * prepared).
   *
   * <p>Note that the bound statements created from the resulting prepared statement will inherit
   * some of the attributes of the given {@link SimpleStatement}. For example, given:
   *
   * <pre>{@code
   * SimpleStatement simpleStatement = SimpleStatement.newInstance("...");
   * PreparedStatement preparedStatement = session.prepare(simpleStatement);
   * BoundStatement boundStatement = preparedStatement.bind();
   * }</pre>
   *
   * <ul>
   *   <li>{@link Request#getConfigProfileName() boundStatement.getConfigProfileName()} , {@link
   *       Request#getConfigProfile() boundStatement.getConfigProfile()} , {@link
   *       Statement#getPagingState() boundStatement.getPagingState()} , {@link
   *       Statement#getPagingState() boundStatement.getPagingState()} , {@link
   *       Request#getRoutingKey() boundStatement.getRoutingKey()} , {@link
   *       Request#getRoutingToken() boundStatement.getRoutingToken()} , {@link
   *       Request#getCustomPayload() boundStatement.getCustomPayload()} , {@link
   *       Request#isIdempotent() boundStatement.isIdempotent()} and {@link Request#isTracing()
   *       boundStatement.isTracing()} return the same values as their counterparts on {@code
   *       simpleStatement};
   *   <li>{@link Request#getRoutingKeyspace() boundStatement.getRoutingKeyspace()} is set from
   *       either {@link Request#getKeyspace() simpleStatement.getKeyspace()} (if it is set), or
   *       {@code simpleStatement.getRoutingKeyspace()};
   *   <li>{@link Statement#getTimestamp() boundStatement.getTimestamp()}, on the other hand, is not
   *       copied from the simple statement.
   * </ul>
   *
   * If you want to customize this behavior, you can write your own implementation of {@link
   * PrepareRequest} and pass it to {@link #prepare(PrepareRequest)}.
   */
  default PreparedStatement prepare(SimpleStatement query) {
    return execute(new DefaultPrepareRequest(query), PrepareRequest.SYNC);
  }

  /**
   * Prepares a CQL statement synchronously (the calling thread blocks until the statement is
   * prepared).
   */
  default PreparedStatement prepare(String query) {
    return execute(new DefaultPrepareRequest(query), PrepareRequest.SYNC);
  }

  /**
   * Prepares a CQL statement synchronously (the calling thread blocks until the statement is
   * prepared).
   *
   * <p>This variant is exposed in case you use an ad hoc {@link PrepareRequest} implementation to
   * customize how attributes are propagated when you prepare a {@link SimpleStatement} (see {@link
   * #prepare(SimpleStatement)} for more explanations). Otherwise, you should rarely have to deal
   * with {@link PrepareRequest} directly.
   */
  default PreparedStatement prepare(PrepareRequest request) {
    return execute(request, PrepareRequest.SYNC);
  }

  /**
   * Prepares a CQL statement asynchronously (the call returns as soon as the prepare query was
   * sent, generally before the statement is prepared).
   *
   * <p>Note that the bound statements created from the resulting prepared statement will inherit
   * some of the attributes of {@code query}; see {@link #prepare(SimpleStatement)} for more
   * details.
   */
  default CompletionStage<PreparedStatement> prepareAsync(SimpleStatement query) {
    return execute(new DefaultPrepareRequest(query), PrepareRequest.ASYNC);
  }

  /**
   * Prepares a CQL statement asynchronously (the call returns as soon as the prepare query was
   * sent, generally before the statement is prepared).
   */
  default CompletionStage<PreparedStatement> prepareAsync(String query) {
    return execute(new DefaultPrepareRequest(query), PrepareRequest.ASYNC);
  }

  /**
   * Prepares a CQL statement asynchronously (the call returns as soon as the prepare query was
   * sent, generally before the statement is prepared).
   *
   * <p>This variant is exposed in case you use an ad hoc {@link PrepareRequest} implementation to
   * customize how attributes are propagated when you prepare a {@link SimpleStatement} (see {@link
   * #prepare(SimpleStatement)} for more explanations). Otherwise, you should rarely have to deal
   * with {@link PrepareRequest} directly.
   */
  default CompletionStage<PreparedStatement> prepareAsync(PrepareRequest request) {
    return execute(request, PrepareRequest.ASYNC);
  }
}
