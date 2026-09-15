package com.cricpro.app.domain.usecase;

import com.cricpro.app.domain.engine.ScoringEngine;
import com.cricpro.app.domain.repository.MatchRepository;
import com.cricpro.app.domain.repository.ScoringRepository;
import com.cricpro.app.domain.repository.SyncRepository;
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
public final class ScoreBallUseCase_Factory implements Factory<ScoreBallUseCase> {
  private final Provider<ScoringRepository> scoringRepositoryProvider;

  private final Provider<MatchRepository> matchRepositoryProvider;

  private final Provider<SyncRepository> syncRepositoryProvider;

  private final Provider<ScoringEngine> scoringEngineProvider;

  public ScoreBallUseCase_Factory(Provider<ScoringRepository> scoringRepositoryProvider,
      Provider<MatchRepository> matchRepositoryProvider,
      Provider<SyncRepository> syncRepositoryProvider,
      Provider<ScoringEngine> scoringEngineProvider) {
    this.scoringRepositoryProvider = scoringRepositoryProvider;
    this.matchRepositoryProvider = matchRepositoryProvider;
    this.syncRepositoryProvider = syncRepositoryProvider;
    this.scoringEngineProvider = scoringEngineProvider;
  }

  @Override
  public ScoreBallUseCase get() {
    return newInstance(scoringRepositoryProvider.get(), matchRepositoryProvider.get(), syncRepositoryProvider.get(), scoringEngineProvider.get());
  }

  public static ScoreBallUseCase_Factory create(
      Provider<ScoringRepository> scoringRepositoryProvider,
      Provider<MatchRepository> matchRepositoryProvider,
      Provider<SyncRepository> syncRepositoryProvider,
      Provider<ScoringEngine> scoringEngineProvider) {
    return new ScoreBallUseCase_Factory(scoringRepositoryProvider, matchRepositoryProvider, syncRepositoryProvider, scoringEngineProvider);
  }

  public static ScoreBallUseCase newInstance(ScoringRepository scoringRepository,
      MatchRepository matchRepository, SyncRepository syncRepository, ScoringEngine scoringEngine) {
    return new ScoreBallUseCase(scoringRepository, matchRepository, syncRepository, scoringEngine);
  }
}
