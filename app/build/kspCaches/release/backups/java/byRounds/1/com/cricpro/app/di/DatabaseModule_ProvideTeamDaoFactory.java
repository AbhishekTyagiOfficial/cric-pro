package com.cricpro.app.di;

import com.cricpro.app.data.local.dao.TeamDao;
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
public final class DatabaseModule_ProvideTeamDaoFactory implements Factory<TeamDao> {
  private final Provider<CricProDatabase> dbProvider;

  public DatabaseModule_ProvideTeamDaoFactory(Provider<CricProDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public TeamDao get() {
    return provideTeamDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideTeamDaoFactory create(Provider<CricProDatabase> dbProvider) {
    return new DatabaseModule_ProvideTeamDaoFactory(dbProvider);
  }

  public static TeamDao provideTeamDao(CricProDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideTeamDao(db));
  }
}
