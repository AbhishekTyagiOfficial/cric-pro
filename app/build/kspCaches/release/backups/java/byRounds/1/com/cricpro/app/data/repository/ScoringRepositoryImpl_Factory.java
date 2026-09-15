package com.cricpro.app.data.repository;

import com.cricpro.app.data.local.dao.BallDao;
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
public final class ScoringRepositoryImpl_Factory implements Factory<ScoringRepositoryImpl> {
  private final Provider<BallDao> ballDaoProvider;

  private final Provider<MatchDao> matchDaoProvider;

  private final Provider<FirestoreService> firestoreServiceProvider;

  public ScoringRepositoryImpl_Factory(Provider<BallDao> ballDaoProvider,
      Provider<MatchDao> matchDaoProvider, Provider<FirestoreService> firestoreServiceProvider) {
    this.ballDaoProvider = ballDaoProvider;
    this.matchDaoProvider = matchDaoProvider;
    this.firestoreServiceProvider = firestoreServiceProvider;
  }

  @Override
  public ScoringRepositoryImpl get() {
    return newInstance(ballDaoProvider.get(), matchDaoProvider.get(), firestoreServiceProvider.get());
  }

  public static ScoringRepositoryImpl_Factory create(Provider<BallDao> ballDaoProvider,
      Provider<MatchDao> matchDaoProvider, Provider<FirestoreService> firestoreServiceProvider) {
    return new ScoringRepositoryImpl_Factory(ballDaoProvider, matchDaoProvider, firestoreServiceProvider);
  }

  public static ScoringRepositoryImpl newInstance(BallDao ballDao, MatchDao matchDao,
      FirestoreService firestoreService) {
    return new ScoringRepositoryImpl(ballDao, matchDao, firestoreService);
  }
}
