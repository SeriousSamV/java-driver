## Integration

This page contains various information on how to integrate the driver in your application.

### Minimal project structure

We publish the driver to [Maven central][central_oss]. Most modern build tools can download the
dependency automatically.

#### Maven

Create the following 4 files:

```
$ find . -type f
./pom.xml
./src/main/resources/application.conf
./src/main/resources/logback.xml
./src/main/java/Main.java
```

##### pom.xml 

This is the [Project Object Model][maven_pom] that describes your application. We declare the
dependencies, and tell Maven that we're going to use Java 8:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>

  <groupId>com.example.yourcompany</groupId>
  <artifactId>yourapp</artifactId>
  <version>1.0.0-SNAPSHOT</version>

  <dependencies>
    <dependency>
      <groupId>com.datastax.oss</groupId>
      <artifactId>java-driver-core</artifactId>
      <version>4.0.0-beta1</version>
    </dependency>
    <dependency>
      <groupId>ch.qos.logback</groupId>
      <artifactId>logback-classic</artifactId>
      <version>1.2.3</version>
    </dependency>
  </dependencies>

  <build>
    <plugins>
      <plugin>
        <artifactId>maven-compiler-plugin</artifactId>
        <configuration>
          <source>1.8</source>
          <target>1.8</target>
        </configuration>
      </plugin>
    </plugins>
  </build>
</project>
```

##### application.conf

```
datastax-java-driver {
  basic.session-name = poc
}
```

This file is not stricly necessary, but it illustrates an important point about the driver's
[configuration](../configuration/): you override any of the driver's default options here.

In this case, we just specify a custom name for our session, it will appear in the logs.

##### logback.xml

For this example, we choose Logback as our [logging framework](../logging/) (we added the dependency
in `pom.xml`). This file configures it to send the driver's `INFO` logs to the console.

```xml
<configuration>
  <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
    <encoder>
      <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
    </encoder>
  </appender>
  <root level="ERROR">
    <appender-ref ref="STDOUT"/>
  </root>
  <logger name="com.datastax.oss.driver" level= "INFO"/>
</configuration>
```

Again, this is not strictly necessary: a truly minimal example could run without the Logback
dependency, or this file; but the default behavior is a bit verbose. 

##### Main.java

This is the canonical example introduced in our [quick start](../#quick-start); it connects to
Cassandra, queries the server version and prints it: 

```java
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;

public class Main {
  public static void main(String[] args) {
    try (CqlSession session = CqlSession.builder().build()) {
      ResultSet rs = session.execute("SELECT release_version FROM system.local");
      System.out.println(rs.one().getString(0));
    }
  }
}
```

Make sure you have a Cassandra instance running on 127.0.0.1:9042 (otherwise, you use
[CqlSession.builder().addContactPoint()][SessionBuilder.addContactPoint] to use a different
address).

##### Running

To launch the program from the command line, use:

```
$ mvn compile exec:java -Dexec.mainClass=Main
```

You should see output similar to:

```
...
[INFO] ------------------------------------------------------------------------
[INFO] Building yourapp 1.0.0-SNAPSHOT
[INFO] ------------------------------------------------------------------------
... (at this point, Maven will download the dependencies the first time) 
[INFO] --- maven-resources-plugin:2.6:resources (default-resources) @ yourapp ---
[WARNING] Using platform encoding (UTF-8 actually) to copy filtered resources, i.e. build is platform dependent!
[INFO] Copying 1 resource
[INFO]
[INFO] --- maven-compiler-plugin:2.5.1:compile (default-compile) @ yourapp ---
[INFO] Nothing to compile - all classes are up to date
[INFO]
[INFO] --- exec-maven-plugin:1.3.1:java (default-cli) @ yourapp ---
11:39:45.355 [Main.main()] INFO  c.d.o.d.i.c.DefaultMavenCoordinates - DataStax Java driver for Apache Cassandra(R) (com.datastax.oss:java-driver-core) version 4.0.0-beta1
11:39:45.648 [poc-admin-0] INFO  c.d.o.d.internal.core.time.Clock - Using native clock for microsecond precision
11:39:45.649 [poc-admin-0] INFO  c.d.o.d.i.c.metadata.MetadataManager - [poc] No contact points provided, defaulting to /127.0.0.1:9042
3.11.2
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 11.777 s
[INFO] Finished at: 2018-06-18T11:32:49-08:00
[INFO] Final Memory: 16M/277M
[INFO] ------------------------------------------------------------------------
```

#### Gradle

[Initialize a new project][gradle_init] with Gradle.

Modify `build.gradle` to add the dependencies:

```groovy
group 'com.example.yourcompany'
version '1.0.0-SNAPSHOT'

apply plugin: 'java'

sourceCompatibility = 1.8

repositories {
    mavenCentral()
}

dependencies {
    compile group: 'com.datastax.oss', name: 'java-driver-core', version: '4.0.0-alpha4-SNAPSHOT'
    compile group: 'ch.qos.logback', name: 'logback-classic', version: '1.2.3'
}
```

Then place [application.conf](#application-conf), [logback.xml](#logback-xml) and
[Main.java](#main-java) in the same locations, and with the same contents, as in the Maven example:

```
./src/main/resources/application.conf
./src/main/resources/logback.xml
./src/main/java/Main.java
```

Optionally, if you want to run from the command line, add the following at the end of
`build.gradle`:

```groovy
task execute(type:JavaExec) {
    main = 'Main'
    classpath = sourceSets.main.runtimeClasspath
}
```

Then launch with:

```
$ ./gradlew execute
```

You should see output similar to:

```
$ ./gradlew execute
:compileJava
:processResources
:classes
:execute
13:32:25.339 [main] INFO  c.d.o.d.i.c.DefaultMavenCoordinates - DataStax Java driver for Apache Cassandra(R) (com.datastax.oss:java-driver-core) version 4.0.0-alpha4-SNAPSHOT
13:32:25.682 [poc-admin-0] INFO  c.d.o.d.internal.core.time.Clock - Using native clock for microsecond precision
13:32:25.683 [poc-admin-0] INFO  c.d.o.d.i.c.metadata.MetadataManager - [poc] No contact points provided, defaulting to /127.0.0.1:9042
3.11.2

BUILD SUCCESSFUL
```

#### Manually (from the binary tarball)

If your build tool can't fetch dependencies from Maven central, we publish a binary tarball on the 
[DataStax download server][downloads].

The driver and its dependencies must be in the compile-time classpath. Application resources, such
as `application.conf` and `logback.xml` in our previous examples, must be in the runtime classpath.

### Component lifecycles

`CqlSession` is a long-lived object: it is expensive to create, and designed to be shared safely
across threads. In most cases, the best approach is to create a single instance, and share it
throughout your application. With the standard servlet API, this is typically done with a
[ServletContextListener]; higher-level frameworks generally have an equivalent "singleton" concept.

Don't forget to close the session when your application shuts down, otherwise the driver's internal
threads might prevent the VM from exiting.

[Simple statements](../statements/simple/) are immutable and completely detached from the session.
You can store them as constants:

```java
public static final SimpleStatement selectVersion =
    SimpleStatement.newInstance("SELECT release_version FROM system.local");
```

[Prepared statements](../statements/prepared/) are thread-safe and should be created only once. But
they depend on the session, so you can't create them statically. One common pattern is to store them
as fields in DAO/repository components (assuming those components are themselves singletons):

```java
class UserRepository {

  private final CqlSession session;
  private final PreparedStatement getStatement;

  public UserRepository(CqlSession session) {
    this.session = session;
    this.getStatement = session.prepare("SELECT * FROM user WHERE id = ?");
  }

  public User get(UUID id) {
    BoundStatement boundStatement = getStatement.bind(id);
    Row row = session.execute(boundStatement).one();
    return toUser(row);
  }
}
```

Instances of [CqlIdentifier] \(used for [case-sensitive](../../case_sensitivity/) schemas) and
[GenericType] \(used with [custom codecs](../custom_codecs/)) are immutable, and should be stored
as constants. 


[central_oss]: https://search.maven.org/#search%7Cga%7C1%7Ccom.datastax.oss
[maven_pom]: https://maven.apache.org/guides/introduction/introduction-to-the-pom.html
[gradle_init]: https://guides.gradle.org/creating-new-gradle-builds/
[downloads]: http://downloads.datastax.com/java-driver/
[ServletContextListener]: https://docs.oracle.com/javaee/6/api/javax/servlet/ServletContextListener.html

[CqlIdentifier]:                  https://docs.datastax.com/en/drivers/java/4.0/com/datastax/oss/driver/api/core/CqlIdentifier.html
[GenericType]:                    https://docs.datastax.com/en/drivers/java/4.0/com/datastax/oss/driver/api/core/type/reflect/GenericType.html
[SessionBuilder.addContactPoint]: https://docs.datastax.com/en/drivers/java/4.0/com/datastax/oss/driver/api/core/session/SessionBuilder.html#addContactPoint-java.net.InetSocketAddress-
