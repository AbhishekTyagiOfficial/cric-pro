package com.cricpro.app.di;

import com.cricpro.app.data.local.dao.BallDao;
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
public final class DatabaseModule_ProvideBallDaoFactory implements Factory<BallDao> {
  private final Provider<CricProDatabase> dbProvider;

  public DatabaseModule_ProvideBallDaoFactory(Provider<CricProDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public BallDao get() {
    return provideBallDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideBallDaoFactory create(Provider<CricProDatabase> dbProvider) {
    return new DatabaseModule_ProvideBallDaoFactory(dbProvider);
  }

  public static BallDao provideBallDao(CricProDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideBallDao(db));
  }
}
