package com.cricpro.app.data.repository;

import com.cricpro.app.data.local.dao.TournamentDao;
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
public final class TournamentRepositoryImpl_Factory implements Factory<TournamentRepositoryImpl> {
  private final Provider<TournamentDao> tournamentDaoProvider;

  private final Provider<FirestoreService> firestoreServiceProvider;

  public TournamentRepositoryImpl_Factory(Provider<TournamentDao> tournamentDaoProvider,
      Provider<FirestoreService> firestoreServiceProvider) {
    this.tournamentDaoProvider = tournamentDaoProvider;
    this.firestoreServiceProvider = firestoreServiceProvider;
  }

  @Override
  public TournamentRepositoryImpl get() {
    return newInstance(tournamentDaoProvider.get(), firestoreServiceProvider.get());
  }

  public static TournamentRepositoryImpl_Factory create(
      Provider<TournamentDao> tournamentDaoProvider,
      Provider<FirestoreService> firestoreServiceProvider) {
    return new TournamentRepositoryImpl_Factory(tournamentDaoProvider, firestoreServiceProvider);
  }

  public static TournamentRepositoryImpl newInstance(TournamentDao tournamentDao,
      FirestoreService firestoreService) {
    return new TournamentRepositoryImpl(tournamentDao, firestoreService);
  }
}
