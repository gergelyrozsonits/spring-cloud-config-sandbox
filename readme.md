UTF8 chars: ❔

# Refreshable Configuration

## Current Implementation

### AWS Parameter Store

https://docs.aws.amazon.com/systems-manager/latest/userguide/systems-manager-parameter-store.html

## Spring Cloud Configuration

- fits well with Spring `Environment` and `PropertySource` abstractions

- hierarchical, supports: application, profile, label based separations:

  ```
  /{application}/{profile}[/{label}]
  /{application}-{profile}.yml
  /{label}/{application}-{profile}.yml
  /{application}-{profile}.properties
  /{label}/{application}-{profile}.properties
  ```

  where label is the branch for the git repository where the data is being stored

  ❔ how the label is being applied in case of a different kind of data source (e.g. jdbc)

- for testing purpose the local file system can be also used, to do that:
  1. create a git repo somewhere (its not enough to have a `git init` in a local folder as somehow the git branches will not be properly initialized this way - maybe it would require at least a commit...)

### Server

- to test the provided configurations just use: http://localhost:8888/order/default/master where:

  1. `order` should match with the app name (`spring.application.name`) of the client
  2. `default` should be a placeholder for the Spring Profiles (if no profile presents, it must be `default`)
  3. `master` (optional) is the git branch, but this can be omitted (http://localhost:8888/order/default also works well)

  for more information please see: https://docs.spring.io/spring-cloud-config/docs/current/reference/html/#_environment_repository

### Client

- with the newer version of Spring Boot (as per the reference guide it comes from Spring Boot `2.4` but strangely it works well with `2.1.0.RELEASE` as well) there is no need for having a `bootstrap.properties`.

- ...but instead of using the `spring.cloud.config.uri` now there is a different way to import the configuration: `spring.config.import=optional:configserver:http://localhost:8888`

  for more information: https://docs.spring.io/spring-cloud-config/docs/current/reference/html/#config-data-import

  - `optional:` part is optional :) if its omitted then the app will fail on startup if there is no configuration server available
  - `http://localhost:8888` part can be also skipped if `spring.cloud.config.uri` configured
  - in this way `spring.config.import=optional:configserver:` is a perfectly calid configuration

- don't forget to set `spring.application.name`...

### Refreshing

- the config server (at least with git backend) immediately picks up the config changes but on the client side there is no effect on these changes (independently whether `@Value`, `@ConfigurationProperties` or `Environment` is being used)

- by annotating beans with `@RefreshScope` the configs can be refreshed by using `Actuator`'s refresh endpoint: http://localhost:8080/actuator/refresh

- the `/actuator/refresh` endpoint requires a `POST` request:

  ```bash
  curl localhost:8080/actuator/refresh -d {} -H "Content-Type: application/json"
  ```

- source: https://spring.io/guides/gs/centralized-configuration/

### JDBC

- additional dependencies:

  ```groovy
  implementation 'org.springframework.boot:spring-boot-starter-jdbc'
  implementation 'org.liquibase:liquibase-core'
  runtimeOnly 'com.h2database:h2'
  ```

- configuration:

  ```properties
  server.port: 8888
  
  # Requires for Spring Cloud Config JDBC (+JdbcTemplate bean)
  # corresponding configuration: org.springframework.cloud.config.server.config.JdbcRepositoryConfiguration
  spring.profiles.active=jdbc
  
  # Database
  spring.datasource.url=jdbc:h2:mem:config
  spring.datasource.driverClassName=org.h2.Driver
  spring.datasource.username=sa
  spring.datasource.password=password
  
  spring.h2.console.enabled=true
  
  spring.liquibase.change-log=classpath:db.changelog-master.xml
  ```

- `liquibase` change log:

  ```xml
  <?xml version="1.0" encoding="UTF-8"?>
  <databaseChangeLog
  	xmlns="http://www.liquibase.org/xml/ns/dbchangelog"
  	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  	xmlns:pro="http://www.liquibase.org/xml/ns/pro"
  	xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog
  http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-3.8.xsd
  http://www.liquibase.org/xml/ns/pro http://www.liquibase.org/xml/ns/pro/liquibase-pro-3.8.xsd">
  	<changeSet id="20210414-1" author="gergelyrozsonits">
  		<createTable tableName="PROPERTIES">
  			<column name="APPLICATION" type="varchar(255)">
  				<constraints nullable="false" primaryKey="true"
  					primaryKeyName="PK_PROPERTIES_TABLE"></constraints>
  			</column>
  			<column name="PROFILE" type="varchar(255)">
  				<constraints nullable="false" primaryKey="true"
  					primaryKeyName="PK_PROPERTIES_TABLE"></constraints>
  			</column>
  			<column name="LABEL" type="varchar(255)">
  				<constraints nullable="true" primaryKey="true"
  					primaryKeyName="PK_PROPERTIES_TABLE"></constraints>
  			</column>
  			<column name="KEY" type="varchar(255)" />
  			<column name="VALUE" type="varchar(255)" />
  		</createTable>
  	</changeSet>
  	<changeSet id="20210414-2" author="gergelyrozsonits">
  		<insert tableName="PROPERTIES">
  			<column name="APPLICATION" value="order" />
  			<column name="PROFILE" value="default" />
  			<column name="LABEL" value="master" />
  			<column name="KEY" value="foo.bar" />
  			<column name="VALUE" value="foo" />
  		</insert>
  	</changeSet>
  </databaseChangeLog>
  ```

## Cloud Bus

- to trigger the update just use:

  ```bash
  curl localhost:8888/actuator/bus-refresh -d "" -H "Content-Type: application/json"
  ```

  

### Experiments:

#### Experiment \#1: Override default properties

- properties defined in the clients configurations will be automatically overridden by the remote configs
- `actuator` could expose the remote configuration sources on http://localhost:8080/actuator/env but for this you need to expose the `env` endpoint by using the `management.endpoints.web.exposure.include=env` configuration
  - to find the config source just search for `configserver:`



