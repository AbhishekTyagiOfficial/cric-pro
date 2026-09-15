package com.cricpro.app.domain.usecase;

import com.cricpro.app.domain.engine.ScoringEngine;
import com.cricpro.app.domain.repository.ScoringRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class UndoBallUseCase_Factory implements Factory<UndoBallUseCase> {
  private final Provider<ScoringRepository> scoringRepositoryProvider;

  private final Provider<ScoringEngine> scoringEngineProvider;

  public UndoBallUseCase_Factory(Provider<ScoringRepository> scoringRepositoryProvider,
      Provider<ScoringEngine> scoringEngineProvider) {
    this.scoringRepositoryProvider = scoringRepositoryProvider;
    this.scoringEngineProvider = scoringEngineProvider;
  }

  @Override
  public UndoBallUseCase get() {
    return newInstance(scoringRepositoryProvider.get(), scoringEngineProvider.get());
  }

  public static UndoBallUseCase_Factory create(
      Provider<ScoringRepository> scoringRepositoryProvider,
      Provider<ScoringEngine> scoringEngineProvider) {
    return new UndoBallUseCase_Factory(scoringRepositoryProvider, scoringEngineProvider);
  }

  public static UndoBallUseCase newInstance(ScoringRepository scoringRepository,
      ScoringEngine scoringEngine) {
    return new UndoBallUseCase(scoringRepository, scoringEngine);
  }
}
