package com.cricpro.app.data.sync;

import android.content.Context;
import androidx.work.WorkerParameters;
import com.cricpro.app.domain.repository.SyncRepository;
import dagger.internal.DaggerGenerated;
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
public final class SyncWorker_Factory {
  private final Provider<SyncRepository> syncRepositoryProvider;

  public SyncWorker_Factory(Provider<SyncRepository> syncRepositoryProvider) {
    this.syncRepositoryProvider = syncRepositoryProvider;
  }

  public SyncWorker get(Context context, WorkerParameters params) {
    return newInstance(context, params, syncRepositoryProvider.get());
  }

  public static SyncWorker_Factory create(Provider<SyncRepository> syncRepositoryProvider) {
    return new SyncWorker_Factory(syncRepositoryProvider);
  }

  public static SyncWorker newInstance(Context context, WorkerParameters params,
      SyncRepository syncRepository) {
    return new SyncWorker(context, params, syncRepository);
  }
}
