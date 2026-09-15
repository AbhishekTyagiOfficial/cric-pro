package com.cricpro.app.di;

import com.cricpro.app.data.local.dao.TournamentDao;
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
public final class DatabaseModule_ProvideTournamentDaoFactory implements Factory<TournamentDao> {
  private final Provider<CricProDatabase> dbProvider;

  public DatabaseModule_ProvideTournamentDaoFactory(Provider<CricProDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public TournamentDao get() {
    return provideTournamentDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideTournamentDaoFactory create(
      Provider<CricProDatabase> dbProvider) {
    return new DatabaseModule_ProvideTournamentDaoFactory(dbProvider);
  }

  public static TournamentDao provideTournamentDao(CricProDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideTournamentDao(db));
  }
}
