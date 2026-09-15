package com.cricpro.app.presentation.home;

import com.cricpro.app.domain.repository.MatchRepository;
import com.cricpro.app.domain.repository.TeamRepository;
import com.cricpro.app.domain.repository.TournamentRepository;
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
public final class HomeViewModel_Factory implements Factory<HomeViewModel> {
  private final Provider<MatchRepository> matchRepositoryProvider;

  private final Provider<TeamRepository> teamRepositoryProvider;

  private final Provider<TournamentRepository> tournamentRepositoryProvider;

  public HomeViewModel_Factory(Provider<MatchRepository> matchRepositoryProvider,
      Provider<TeamRepository> teamRepositoryProvider,
      Provider<TournamentRepository> tournamentRepositoryProvider) {
    this.matchRepositoryProvider = matchRepositoryProvider;
    this.teamRepositoryProvider = teamRepositoryProvider;
    this.tournamentRepositoryProvider = tournamentRepositoryProvider;
  }

  @Override
  public HomeViewModel get() {
    return newInstance(matchRepositoryProvider.get(), teamRepositoryProvider.get(), tournamentRepositoryProvider.get());
  }

  public static HomeViewModel_Factory create(Provider<MatchRepository> matchRepositoryProvider,
      Provider<TeamRepository> teamRepositoryProvider,
      Provider<TournamentRepository> tournamentRepositoryProvider) {
    return new HomeViewModel_Factory(matchRepositoryProvider, teamRepositoryProvider, tournamentRepositoryProvider);
  }

  public static HomeViewModel newInstance(MatchRepository matchRepository,
      TeamRepository teamRepository, TournamentRepository tournamentRepository) {
    return new HomeViewModel(matchRepository, teamRepository, tournamentRepository);
  }
}
