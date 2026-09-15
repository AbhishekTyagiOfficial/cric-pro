package com.cricpro.app.data.repository;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast"
})
public final class GroundRepositoryImpl_Factory implements Factory<GroundRepositoryImpl> {
  @Override
  public GroundRepositoryImpl get() {
    return newInstance();
  }

  public static GroundRepositoryImpl_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static GroundRepositoryImpl newInstance() {
    return new GroundRepositoryImpl();
  }

  private static final class InstanceHolder {
    private static final GroundRepositoryImpl_Factory INSTANCE = new GroundRepositoryImpl_Factory();
  }
}
