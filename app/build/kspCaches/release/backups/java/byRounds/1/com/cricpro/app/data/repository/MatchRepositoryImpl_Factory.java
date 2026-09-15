package com.cricpro.app.data.repository;

import com.cricpro.app.data.local.dao.MatchDao;
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
public final class MatchRepositoryImpl_Factory implements Factory<MatchRepositoryImpl> {
  private final Provider<MatchDao> matchDaoProvider;

  private final Provider<FirestoreService> firestoreServiceProvider;

  public MatchRepositoryImpl_Factory(Provider<MatchDao> matchDaoProvider,
      Provider<FirestoreService> firestoreServiceProvider) {
    this.matchDaoProvider = matchDaoProvider;
    this.firestoreServiceProvider = firestoreServiceProvider;
  }

  @Override
  public MatchRepositoryImpl get() {
    return newInstance(matchDaoProvider.get(), firestoreServiceProvider.get());
  }

  public static MatchRepositoryImpl_Factory create(Provider<MatchDao> matchDaoProvider,
      Provider<FirestoreService> firestoreServiceProvider) {
    return new MatchRepositoryImpl_Factory(matchDaoProvider, firestoreServiceProvider);
  }

  public static MatchRepositoryImpl newInstance(MatchDao matchDao,
      FirestoreService firestoreService) {
    return new MatchRepositoryImpl(matchDao, firestoreService);
  }
}
