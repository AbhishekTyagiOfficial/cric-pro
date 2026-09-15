package com.cricpro.app;

import androidx.hilt.work.HiltWorkerFactory;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class CricProApplication_MembersInjector implements MembersInjector<CricProApplication> {
  private final Provider<HiltWorkerFactory> workerFactoryProvider;

  public CricProApplication_MembersInjector(Provider<HiltWorkerFactory> workerFactoryProvider) {
    this.workerFactoryProvider = workerFactoryProvider;
  }

  public static MembersInjector<CricProApplication> create(
      Provider<HiltWorkerFactory> workerFactoryProvider) {
    return new CricProApplication_MembersInjector(workerFactoryProvider);
  }

  @Override
  public void injectMembers(CricProApplication instance) {
    injectWorkerFactory(instance, workerFactoryProvider.get());
  }

  @InjectedFieldSignature("com.cricpro.app.CricProApplication.workerFactory")
  public static void injectWorkerFactory(CricProApplication instance,
      HiltWorkerFactory workerFactory) {
    instance.workerFactory = workerFactory;
  }
}
