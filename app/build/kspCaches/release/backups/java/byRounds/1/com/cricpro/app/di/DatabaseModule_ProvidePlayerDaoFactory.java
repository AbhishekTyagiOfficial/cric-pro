package com.cricpro.app.di;

import com.cricpro.app.data.local.dao.PlayerDao;
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
public final class DatabaseModule_ProvidePlayerDaoFactory implements Factory<PlayerDao> {
  private final Provider<CricProDatabase> dbProvider;

  public DatabaseModule_ProvidePlayerDaoFactory(Provider<CricProDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public PlayerDao get() {
    return providePlayerDao(dbProvider.get());
  }

  public static DatabaseModule_ProvidePlayerDaoFactory create(
      Provider<CricProDatabase> dbProvider) {
    return new DatabaseModule_ProvidePlayerDaoFactory(dbProvider);
  }

  public static PlayerDao providePlayerDao(CricProDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.providePlayerDao(db));
  }
}
