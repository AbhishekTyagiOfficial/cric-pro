package com.cricpro.app.data.repository;

import com.cricpro.app.data.local.dao.SyncQueueDao;
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
public final class SyncRepositoryImpl_Factory implements Factory<SyncRepositoryImpl> {
  private final Provider<SyncQueueDao> syncQueueDaoProvider;

  private final Provider<FirestoreService> firestoreServiceProvider;

  public SyncRepositoryImpl_Factory(Provider<SyncQueueDao> syncQueueDaoProvider,
      Provider<FirestoreService> firestoreServiceProvider) {
    this.syncQueueDaoProvider = syncQueueDaoProvider;
    this.firestoreServiceProvider = firestoreServiceProvider;
  }

  @Override
  public SyncRepositoryImpl get() {
    return newInstance(syncQueueDaoProvider.get(), firestoreServiceProvider.get());
  }

  public static SyncRepositoryImpl_Factory create(Provider<SyncQueueDao> syncQueueDaoProvider,
      Provider<FirestoreService> firestoreServiceProvider) {
    return new SyncRepositoryImpl_Factory(syncQueueDaoProvider, firestoreServiceProvider);
  }

  public static SyncRepositoryImpl newInstance(SyncQueueDao syncQueueDao,
      FirestoreService firestoreService) {
    return new SyncRepositoryImpl(syncQueueDao, firestoreService);
  }
}
