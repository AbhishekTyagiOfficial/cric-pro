package com.cricpro.app.data.repository;

import com.cricpro.app.data.local.dao.PlayerDao;
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
public final class PlayerRepositoryImpl_Factory implements Factory<PlayerRepositoryImpl> {
  private final Provider<PlayerDao> playerDaoProvider;

  public PlayerRepositoryImpl_Factory(Provider<PlayerDao> playerDaoProvider) {
    this.playerDaoProvider = playerDaoProvider;
  }

  @Override
  public PlayerRepositoryImpl get() {
    return newInstance(playerDaoProvider.get());
  }

  public static PlayerRepositoryImpl_Factory create(Provider<PlayerDao> playerDaoProvider) {
    return new PlayerRepositoryImpl_Factory(playerDaoProvider);
  }

  public static PlayerRepositoryImpl newInstance(PlayerDao playerDao) {
    return new PlayerRepositoryImpl(playerDao);
  }
}
