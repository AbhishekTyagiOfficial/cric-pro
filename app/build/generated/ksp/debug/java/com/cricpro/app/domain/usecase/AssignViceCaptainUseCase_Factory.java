package com.cricpro.app.domain.usecase;

import com.cricpro.app.domain.repository.TeamRepository;
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
public final class AssignViceCaptainUseCase_Factory implements Factory<AssignViceCaptainUseCase> {
  private final Provider<TeamRepository> teamRepositoryProvider;

  public AssignViceCaptainUseCase_Factory(Provider<TeamRepository> teamRepositoryProvider) {
    this.teamRepositoryProvider = teamRepositoryProvider;
  }

  @Override
  public AssignViceCaptainUseCase get() {
    return newInstance(teamRepositoryProvider.get());
  }

  public static AssignViceCaptainUseCase_Factory create(
      Provider<TeamRepository> teamRepositoryProvider) {
    return new AssignViceCaptainUseCase_Factory(teamRepositoryProvider);
  }

  public static AssignViceCaptainUseCase newInstance(TeamRepository teamRepository) {
    return new AssignViceCaptainUseCase(teamRepository);
  }
}
