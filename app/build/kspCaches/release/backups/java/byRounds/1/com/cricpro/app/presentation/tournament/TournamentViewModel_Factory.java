package com.cricpro.app.presentation.tournament;

import com.cricpro.app.domain.repository.TournamentRepository;
import com.cricpro.app.domain.usecase.CalculatePointsTableUseCase;
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
public final class TournamentViewModel_Factory implements Factory<TournamentViewModel> {
  private final Provider<TournamentRepository> tournamentRepositoryProvider;

  private final Provider<CalculatePointsTableUseCase> calculatePointsTableUseCaseProvider;

  public TournamentViewModel_Factory(Provider<TournamentRepository> tournamentRepositoryProvider,
      Provider<CalculatePointsTableUseCase> calculatePointsTableUseCaseProvider) {
    this.tournamentRepositoryProvider = tournamentRepositoryProvider;
    this.calculatePointsTableUseCaseProvider = calculatePointsTableUseCaseProvider;
  }

  @Override
  public TournamentViewModel get() {
    return newInstance(tournamentRepositoryProvider.get(), calculatePointsTableUseCaseProvider.get());
  }

  public static TournamentViewModel_Factory create(
      Provider<TournamentRepository> tournamentRepositoryProvider,
      Provider<CalculatePointsTableUseCase> calculatePointsTableUseCaseProvider) {
    return new TournamentViewModel_Factory(tournamentRepositoryProvider, calculatePointsTableUseCaseProvider);
  }

  public static TournamentViewModel newInstance(TournamentRepository tournamentRepository,
      CalculatePointsTableUseCase calculatePointsTableUseCase) {
    return new TournamentViewModel(tournamentRepository, calculatePointsTableUseCase);
  }
}
