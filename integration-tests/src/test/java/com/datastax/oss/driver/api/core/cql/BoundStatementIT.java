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
package com.datastax.oss.driver.api.core.cql;

import static org.assertj.core.api.Assertions.assertThat;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.config.DefaultDriverOption;
import com.datastax.oss.driver.api.core.config.DriverConfigProfile;
import com.datastax.oss.driver.api.core.metadata.token.Token;
import com.datastax.oss.driver.api.testinfra.CassandraRequirement;
import com.datastax.oss.driver.api.testinfra.ccm.CcmRule;
import com.datastax.oss.driver.api.testinfra.session.SessionRule;
import com.datastax.oss.driver.api.testinfra.session.SessionUtils;
import com.datastax.oss.driver.categories.ParallelizableTests;
import com.datastax.oss.driver.internal.core.ProtocolFeature;
import com.datastax.oss.driver.internal.core.ProtocolVersionRegistry;
import com.datastax.oss.driver.internal.core.context.InternalDriverContext;
import com.datastax.oss.driver.internal.core.type.codec.CqlIntToStringCodec;
import com.datastax.oss.driver.shaded.guava.common.collect.ImmutableList;
import com.datastax.oss.protocol.internal.util.Bytes;
import com.datastax.oss.protocol.internal.util.collection.NullAllowingImmutableMap;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.Map;
import java.util.function.Function;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;

@Category(ParallelizableTests.class)
public class BoundStatementIT {

  @Rule public CcmRule ccm = CcmRule.getInstance();

  @Rule
  public SessionRule<CqlSession> sessionRule =
      new SessionRule<>(ccm, "basic.request.page-size = 20");

  @Rule public TestName name = new TestName();

  private static final int VALUE = 7;

  @Before
  public void setupSchema() {
    // table with simple primary key, single cell.
    sessionRule
        .session()
        .execute(
            SimpleStatement.builder("CREATE TABLE IF NOT EXISTS test2 (k text primary key, v0 int)")
                .withConfigProfile(sessionRule.slowProfile())
                .build());
  }

  @Test(expected = IllegalStateException.class)
  public void should_not_allow_unset_value_when_protocol_less_than_v4() {
    try (CqlSession v3Session =
        SessionUtils.newSession(ccm, sessionRule.keyspace(), "advanced.protocol.version = V3")) {
      PreparedStatement prepared = v3Session.prepare("INSERT INTO test2 (k, v0) values (?, ?)");

      BoundStatement boundStatement =
          prepared.boundStatementBuilder().setString(0, name.getMethodName()).unset(1).build();

      v3Session.execute(boundStatement);
    }
  }

  @Test
  @CassandraRequirement(min = "2.2")
  public void should_not_write_tombstone_if_value_is_implicitly_unset() {
    PreparedStatement prepared =
        sessionRule.session().prepare("INSERT INTO test2 (k, v0) values (?, ?)");

    sessionRule.session().execute(prepared.bind(name.getMethodName(), VALUE));

    BoundStatement boundStatement =
        prepared.boundStatementBuilder().setString(0, name.getMethodName()).build();

    verifyUnset(boundStatement);
  }

  @Test
  @CassandraRequirement(min = "2.2")
  public void should_write_tombstone_if_value_is_explicitly_unset() {
    PreparedStatement prepared =
        sessionRule.session().prepare("INSERT INTO test2 (k, v0) values (?, ?)");

    sessionRule.session().execute(prepared.bind(name.getMethodName(), VALUE));

    BoundStatement boundStatement =
        prepared
            .boundStatementBuilder()
            .setString(0, name.getMethodName())
            .setInt(1, VALUE + 1) // set initially, will be unset later
            .build();

    verifyUnset(boundStatement.unset(1));
  }

  @Test
  @CassandraRequirement(min = "2.2")
  public void should_write_tombstone_if_value_is_explicitly_unset_on_builder() {
    PreparedStatement prepared =
        sessionRule.session().prepare("INSERT INTO test2 (k, v0) values (?, ?)");

    sessionRule.session().execute(prepared.bind(name.getMethodName(), VALUE));

    BoundStatement boundStatement =
        prepared
            .boundStatementBuilder()
            .setString(0, name.getMethodName())
            .setInt(1, VALUE + 1) // set initially, will be unset later
            .unset(1)
            .build();

    verifyUnset(boundStatement);
  }

  @Test
  public void should_have_empty_result_definitions_for_update_query() {
    PreparedStatement prepared =
        sessionRule.session().prepare("INSERT INTO test2 (k, v0) values (?, ?)");

    assertThat(prepared.getResultSetDefinitions()).hasSize(0);

    ResultSet rs = sessionRule.session().execute(prepared.bind(name.getMethodName(), VALUE));
    assertThat(rs.getColumnDefinitions()).hasSize(0);
  }

  @Test
  public void should_allow_custom_codecs_when_setting_values_in_bulk() {
    // v0 is an int column, but we'll bind a String to it
    CqlIntToStringCodec codec = new CqlIntToStringCodec();
    try (CqlSession session = sessionWithCustomCodec(codec)) {
      PreparedStatement prepared = session.prepare("INSERT INTO test2 (k, v0) values (?, ?)");
      for (BoundStatement boundStatement :
          ImmutableList.of(
              prepared.bind(name.getMethodName(), "42"),
              prepared.boundStatementBuilder(name.getMethodName(), "42").build())) {

        session.execute(boundStatement);
        ResultSet rs =
            session.execute(
                SimpleStatement.newInstance(
                    "SELECT v0 FROM test2 WHERE k = ?", name.getMethodName()));
        assertThat(rs.one().getInt(0)).isEqualTo(42);
      }
    }
  }

  @Test
  public void should_propagate_attributes_when_preparing_a_simple_statement() {
    CqlSession session = sessionRule.session();

    DriverConfigProfile mockProfile =
        session
            .getContext()
            .config()
            .getDefaultProfile()
            // Value doesn't matter, we just want a distinct profile
            .withDuration(DefaultDriverOption.REQUEST_TIMEOUT, Duration.ofSeconds(10));
    String mockConfigProfileName = "mockConfigProfileName";
    ByteBuffer mockPagingState = Bytes.fromHexString("0xaaaa");
    CqlIdentifier mockKeyspace =
        supportsPerRequestKeyspace(session) ? CqlIdentifier.fromCql("system") : null;
    CqlIdentifier mockRoutingKeyspace = CqlIdentifier.fromCql("mockRoutingKeyspace");
    ByteBuffer mockRoutingKey = Bytes.fromHexString("0xbbbb");
    Token mockRoutingToken = session.getMetadata().getTokenMap().get().newToken(mockRoutingKey);
    Map<String, ByteBuffer> mockCustomPayload =
        NullAllowingImmutableMap.of("key1", Bytes.fromHexString("0xcccc"));

    SimpleStatement simpleStatement =
        SimpleStatement.builder("SELECT release_version FROM system.local")
            .withConfigProfile(mockProfile)
            .withConfigProfileName(mockConfigProfileName)
            .withPagingState(mockPagingState)
            .withKeyspace(mockKeyspace)
            .withRoutingKeyspace(mockRoutingKeyspace)
            .withRoutingKey(mockRoutingKey)
            .withRoutingToken(mockRoutingToken)
            .addCustomPayload("key1", mockCustomPayload.get("key1"))
            .withTimestamp(42)
            .withIdempotence(true)
            .withTracing()
            .build();
    PreparedStatement preparedStatement = session.prepare(simpleStatement);

    // Cover all the ways to create bound statements:
    ImmutableList<Function<PreparedStatement, BoundStatement>> createMethods =
        ImmutableList.of(PreparedStatement::bind, p -> p.boundStatementBuilder().build());

    for (Function<PreparedStatement, BoundStatement> createMethod : createMethods) {
      BoundStatement boundStatement = createMethod.apply(preparedStatement);

      assertThat(boundStatement.getConfigProfile()).isEqualTo(mockProfile);
      assertThat(boundStatement.getConfigProfileName()).isEqualTo(mockConfigProfileName);
      assertThat(boundStatement.getPagingState()).isEqualTo(mockPagingState);
      assertThat(boundStatement.getRoutingKeyspace())
          .isEqualTo(mockKeyspace != null ? mockKeyspace : mockRoutingKeyspace);
      assertThat(boundStatement.getRoutingKey()).isEqualTo(mockRoutingKey);
      assertThat(boundStatement.getRoutingToken()).isEqualTo(mockRoutingToken);
      assertThat(boundStatement.getCustomPayload()).isEqualTo(mockCustomPayload);
      assertThat(boundStatement.isIdempotent()).isTrue();
      assertThat(boundStatement.isTracing()).isTrue();

      // Bound statements do not support per-query keyspaces, so this is not set
      assertThat(boundStatement.getKeyspace()).isNull();
      // Should not be propagated
      assertThat(boundStatement.getTimestamp()).isEqualTo(Long.MIN_VALUE);
    }
  }

  private void verifyUnset(BoundStatement boundStatement) {
    sessionRule.session().execute(boundStatement.unset(1));

    // Verify that no tombstone was written by reading data back and ensuring initial value is
    // retained.
    ResultSet result =
        sessionRule
            .session()
            .execute(
                SimpleStatement.builder("SELECT v0 from test2 where k = ?")
                    .addPositionalValue(name.getMethodName())
                    .build());

    Row row = result.iterator().next();
    assertThat(row.getInt(0)).isEqualTo(VALUE);
  }

  @SuppressWarnings("unchecked")
  private CqlSession sessionWithCustomCodec(CqlIntToStringCodec codec) {
    return (CqlSession)
        SessionUtils.baseBuilder()
            .addContactPoints(ccm.getContactPoints())
            .withKeyspace(sessionRule.keyspace())
            .addTypeCodecs(codec)
            .build();
  }

  private boolean supportsPerRequestKeyspace(CqlSession session) {
    InternalDriverContext context = (InternalDriverContext) session.getContext();
    ProtocolVersionRegistry protocolVersionRegistry = context.protocolVersionRegistry();
    return protocolVersionRegistry.supports(
        context.protocolVersion(), ProtocolFeature.PER_REQUEST_KEYSPACE);
  }
}
