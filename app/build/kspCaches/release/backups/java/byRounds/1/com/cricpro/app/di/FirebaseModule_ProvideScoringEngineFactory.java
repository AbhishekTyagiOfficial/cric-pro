package com.cricpro.app.di;

import com.cricpro.app.domain.engine.ScoringEngine;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
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
public final class FirebaseModule_ProvideScoringEngineFactory implements Factory<ScoringEngine> {
  @Override
  public ScoringEngine get() {
    return provideScoringEngine();
  }

  public static FirebaseModule_ProvideScoringEngineFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static ScoringEngine provideScoringEngine() {
    return Preconditions.checkNotNullFromProvides(FirebaseModule.INSTANCE.provideScoringEngine());
  }

  private static final class InstanceHolder {
    private static final FirebaseModule_ProvideScoringEngineFactory INSTANCE = new FirebaseModule_ProvideScoringEngineFactory();
  }
}
