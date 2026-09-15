package com.cricpro.app.data.repository;

import com.cricpro.app.data.local.dao.PlayerDao;
import com.cricpro.app.data.local.dao.TeamDao;
import com.cricpro.app.data.remote.FirestoreService;
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
public final class TeamRepositoryImpl_Factory implements Factory<TeamRepositoryImpl> {
  private final Provider<TeamDao> teamDaoProvider;

  private final Provider<PlayerDao> playerDaoProvider;

  private final Provider<FirestoreService> firestoreServiceProvider;

  public TeamRepositoryImpl_Factory(Provider<TeamDao> teamDaoProvider,
      Provider<PlayerDao> playerDaoProvider, Provider<FirestoreService> firestoreServiceProvider) {
    this.teamDaoProvider = teamDaoProvider;
    this.playerDaoProvider = playerDaoProvider;
    this.firestoreServiceProvider = firestoreServiceProvider;
  }

  @Override
  public TeamRepositoryImpl get() {
    return newInstance(teamDaoProvider.get(), playerDaoProvider.get(), firestoreServiceProvider.get());
  }

  public static TeamRepositoryImpl_Factory create(Provider<TeamDao> teamDaoProvider,
      Provider<PlayerDao> playerDaoProvider, Provider<FirestoreService> firestoreServiceProvider) {
    return new TeamRepositoryImpl_Factory(teamDaoProvider, playerDaoProvider, firestoreServiceProvider);
  }

  public static TeamRepositoryImpl newInstance(TeamDao teamDao, PlayerDao playerDao,
      FirestoreService firestoreService) {
    return new TeamRepositoryImpl(teamDao, playerDao, firestoreService);
  }
}
