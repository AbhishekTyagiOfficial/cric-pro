package com.cricpro.app.presentation.team;

import com.cricpro.app.domain.repository.TeamRepository;
import com.cricpro.app.domain.usecase.AssignCaptainUseCase;
import com.cricpro.app.domain.usecase.AssignViceCaptainUseCase;
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
public final class TeamViewModel_Factory implements Factory<TeamViewModel> {
  private final Provider<TeamRepository> teamRepositoryProvider;

  private final Provider<AssignCaptainUseCase> assignCaptainUseCaseProvider;

  private final Provider<AssignViceCaptainUseCase> assignViceCaptainUseCaseProvider;

  public TeamViewModel_Factory(Provider<TeamRepository> teamRepositoryProvider,
      Provider<AssignCaptainUseCase> assignCaptainUseCaseProvider,
      Provider<AssignViceCaptainUseCase> assignViceCaptainUseCaseProvider) {
    this.teamRepositoryProvider = teamRepositoryProvider;
    this.assignCaptainUseCaseProvider = assignCaptainUseCaseProvider;
    this.assignViceCaptainUseCaseProvider = assignViceCaptainUseCaseProvider;
  }

  @Override
  public TeamViewModel get() {
    return newInstance(teamRepositoryProvider.get(), assignCaptainUseCaseProvider.get(), assignViceCaptainUseCaseProvider.get());
  }

  public static TeamViewModel_Factory create(Provider<TeamRepository> teamRepositoryProvider,
      Provider<AssignCaptainUseCase> assignCaptainUseCaseProvider,
      Provider<AssignViceCaptainUseCase> assignViceCaptainUseCaseProvider) {
    return new TeamViewModel_Factory(teamRepositoryProvider, assignCaptainUseCaseProvider, assignViceCaptainUseCaseProvider);
  }

  public static TeamViewModel newInstance(TeamRepository teamRepository,
      AssignCaptainUseCase assignCaptainUseCase,
      AssignViceCaptainUseCase assignViceCaptainUseCase) {
    return new TeamViewModel(teamRepository, assignCaptainUseCase, assignViceCaptainUseCase);
  }
}
