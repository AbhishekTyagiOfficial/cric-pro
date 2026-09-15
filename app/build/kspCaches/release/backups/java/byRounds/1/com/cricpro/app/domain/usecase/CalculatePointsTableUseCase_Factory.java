package com.cricpro.app.domain.usecase;

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
public final class CalculatePointsTableUseCase_Factory implements Factory<CalculatePointsTableUseCase> {
  @Override
  public CalculatePointsTableUseCase get() {
    return newInstance();
  }

  public static CalculatePointsTableUseCase_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static CalculatePointsTableUseCase newInstance() {
    return new CalculatePointsTableUseCase();
  }

  private static final class InstanceHolder {
    private static final CalculatePointsTableUseCase_Factory INSTANCE = new CalculatePointsTableUseCase_Factory();
  }
}
