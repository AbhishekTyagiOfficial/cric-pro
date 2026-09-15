package com.cricpro.app.di;

import com.cricpro.app.data.local.dao.SyncQueueDao;
import com.cricpro.app.data.local.db.CricProDatabase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class DatabaseModule_ProvideSyncQueueDaoFactory implements Factory<SyncQueueDao> {
  private final Provider<CricProDatabase> dbProvider;

  public DatabaseModule_ProvideSyncQueueDaoFactory(Provider<CricProDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public SyncQueueDao get() {
    return provideSyncQueueDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideSyncQueueDaoFactory create(
      Provider<CricProDatabase> dbProvider) {
    return new DatabaseModule_ProvideSyncQueueDaoFactory(dbProvider);
  }

  public static SyncQueueDao provideSyncQueueDao(CricProDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideSyncQueueDao(db));
  }
}
