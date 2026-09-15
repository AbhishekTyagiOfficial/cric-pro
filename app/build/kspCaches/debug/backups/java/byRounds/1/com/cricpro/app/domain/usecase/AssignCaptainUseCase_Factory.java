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
public final class AssignCaptainUseCase_Factory implements Factory<AssignCaptainUseCase> {
  private final Provider<TeamRepository> teamRepositoryProvider;

  public AssignCaptainUseCase_Factory(Provider<TeamRepository> teamRepositoryProvider) {
    this.teamRepositoryProvider = teamRepositoryProvider;
  }

  @Override
  public AssignCaptainUseCase get() {
    return newInstance(teamRepositoryProvider.get());
  }

  public static AssignCaptainUseCase_Factory create(
      Provider<TeamRepository> teamRepositoryProvider) {
    return new AssignCaptainUseCase_Factory(teamRepositoryProvider);
  }

  public static AssignCaptainUseCase newInstance(TeamRepository teamRepository) {
    return new AssignCaptainUseCase(teamRepository);
  }
}
