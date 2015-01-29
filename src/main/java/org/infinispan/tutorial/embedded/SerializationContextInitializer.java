package org.infinispan.tutorial.embedded;

import org.infinispan.protostream.annotations.AutoProtoSchemaBuilder;

@AutoProtoSchemaBuilder(
      includeClasses = LocationWeather.class,
      schemaFileName = "weather.proto",
      schemaFilePath = "proto",
      schemaPackageName = "org.infinispan.tutorial.embedded"
)
public interface SerializationContextInitializer extends org.infinispan.protostream.SerializationContextInitializer {
}
