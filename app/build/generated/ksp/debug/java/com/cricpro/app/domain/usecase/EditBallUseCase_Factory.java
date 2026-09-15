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
public final class EditBallUseCase_Factory implements Factory<EditBallUseCase> {
  private final Provider<ScoringRepository> scoringRepositoryProvider;

  private final Provider<ScoringEngine> scoringEngineProvider;

  public EditBallUseCase_Factory(Provider<ScoringRepository> scoringRepositoryProvider,
      Provider<ScoringEngine> scoringEngineProvider) {
    this.scoringRepositoryProvider = scoringRepositoryProvider;
    this.scoringEngineProvider = scoringEngineProvider;
  }

  @Override
  public EditBallUseCase get() {
    return newInstance(scoringRepositoryProvider.get(), scoringEngineProvider.get());
  }

  public static EditBallUseCase_Factory create(
      Provider<ScoringRepository> scoringRepositoryProvider,
      Provider<ScoringEngine> scoringEngineProvider) {
    return new EditBallUseCase_Factory(scoringRepositoryProvider, scoringEngineProvider);
  }

  public static EditBallUseCase newInstance(ScoringRepository scoringRepository,
      ScoringEngine scoringEngine) {
    return new EditBallUseCase(scoringRepository, scoringEngine);
  }
}
