package com.cricpro.app.presentation.match;

import androidx.lifecycle.SavedStateHandle;
import com.cricpro.app.domain.repository.MatchRepository;
import com.cricpro.app.domain.repository.ScoringRepository;
import com.cricpro.app.domain.repository.TeamRepository;
import com.cricpro.app.domain.usecase.EditBallUseCase;
import com.cricpro.app.domain.usecase.ScoreBallUseCase;
import com.cricpro.app.domain.usecase.UndoBallUseCase;
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
public final class ScoringViewModel_Factory implements Factory<ScoringViewModel> {
  private final Provider<MatchRepository> matchRepositoryProvider;

  private final Provider<TeamRepository> teamRepositoryProvider;

  private final Provider<ScoringRepository> scoringRepositoryProvider;

  private final Provider<ScoreBallUseCase> scoreBallUseCaseProvider;

  private final Provider<UndoBallUseCase> undoBallUseCaseProvider;

  private final Provider<EditBallUseCase> editBallUseCaseProvider;

  private final Provider<SavedStateHandle> savedStateHandleProvider;

  public ScoringViewModel_Factory(Provider<MatchRepository> matchRepositoryProvider,
      Provider<TeamRepository> teamRepositoryProvider,
      Provider<ScoringRepository> scoringRepositoryProvider,
      Provider<ScoreBallUseCase> scoreBallUseCaseProvider,
      Provider<UndoBallUseCase> undoBallUseCaseProvider,
      Provider<EditBallUseCase> editBallUseCaseProvider,
      Provider<SavedStateHandle> savedStateHandleProvider) {
    this.matchRepositoryProvider = matchRepositoryProvider;
    this.teamRepositoryProvider = teamRepositoryProvider;
    this.scoringRepositoryProvider = scoringRepositoryProvider;
    this.scoreBallUseCaseProvider = scoreBallUseCaseProvider;
    this.undoBallUseCaseProvider = undoBallUseCaseProvider;
    this.editBallUseCaseProvider = editBallUseCaseProvider;
    this.savedStateHandleProvider = savedStateHandleProvider;
  }

  @Override
  public ScoringViewModel get() {
    return newInstance(matchRepositoryProvider.get(), teamRepositoryProvider.get(), scoringRepositoryProvider.get(), scoreBallUseCaseProvider.get(), undoBallUseCaseProvider.get(), editBallUseCaseProvider.get(), savedStateHandleProvider.get());
  }

  public static ScoringViewModel_Factory create(Provider<MatchRepository> matchRepositoryProvider,
      Provider<TeamRepository> teamRepositoryProvider,
      Provider<ScoringRepository> scoringRepositoryProvider,
      Provider<ScoreBallUseCase> scoreBallUseCaseProvider,
      Provider<UndoBallUseCase> undoBallUseCaseProvider,
      Provider<EditBallUseCase> editBallUseCaseProvider,
      Provider<SavedStateHandle> savedStateHandleProvider) {
    return new ScoringViewModel_Factory(matchRepositoryProvider, teamRepositoryProvider, scoringRepositoryProvider, scoreBallUseCaseProvider, undoBallUseCaseProvider, editBallUseCaseProvider, savedStateHandleProvider);
  }

  public static ScoringViewModel newInstance(MatchRepository matchRepository,
      TeamRepository teamRepository, ScoringRepository scoringRepository,
      ScoreBallUseCase scoreBallUseCase, UndoBallUseCase undoBallUseCase,
      EditBallUseCase editBallUseCase, SavedStateHandle savedStateHandle) {
    return new ScoringViewModel(matchRepository, teamRepository, scoringRepository, scoreBallUseCase, undoBallUseCase, editBallUseCase, savedStateHandle);
  }
}
