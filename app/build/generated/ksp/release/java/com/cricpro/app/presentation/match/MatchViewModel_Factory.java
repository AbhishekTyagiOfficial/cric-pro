package com.cricpro.app.presentation.match;

import com.cricpro.app.domain.repository.MatchRepository;
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
public final class MatchViewModel_Factory implements Factory<MatchViewModel> {
  private final Provider<MatchRepository> matchRepositoryProvider;

  public MatchViewModel_Factory(Provider<MatchRepository> matchRepositoryProvider) {
    this.matchRepositoryProvider = matchRepositoryProvider;
  }

  @Override
  public MatchViewModel get() {
    return newInstance(matchRepositoryProvider.get());
  }

  public static MatchViewModel_Factory create(Provider<MatchRepository> matchRepositoryProvider) {
    return new MatchViewModel_Factory(matchRepositoryProvider);
  }

  public static MatchViewModel newInstance(MatchRepository matchRepository) {
    return new MatchViewModel(matchRepository);
  }
}
