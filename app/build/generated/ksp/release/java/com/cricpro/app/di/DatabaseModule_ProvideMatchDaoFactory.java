package com.cricpro.app.di;

import com.cricpro.app.data.local.dao.MatchDao;
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
public final class DatabaseModule_ProvideMatchDaoFactory implements Factory<MatchDao> {
  private final Provider<CricProDatabase> dbProvider;

  public DatabaseModule_ProvideMatchDaoFactory(Provider<CricProDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public MatchDao get() {
    return provideMatchDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideMatchDaoFactory create(Provider<CricProDatabase> dbProvider) {
    return new DatabaseModule_ProvideMatchDaoFactory(dbProvider);
  }

  public static MatchDao provideMatchDao(CricProDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideMatchDao(db));
  }
}
