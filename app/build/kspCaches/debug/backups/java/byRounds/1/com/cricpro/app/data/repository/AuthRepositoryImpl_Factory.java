package com.cricpro.app.data.repository;

import com.cricpro.app.data.remote.FirebaseAuthService;
import com.google.firebase.auth.FirebaseAuth;
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
public final class AuthRepositoryImpl_Factory implements Factory<AuthRepositoryImpl> {
  private final Provider<FirebaseAuthService> authServiceProvider;

  private final Provider<FirebaseAuth> firebaseAuthProvider;

  public AuthRepositoryImpl_Factory(Provider<FirebaseAuthService> authServiceProvider,
      Provider<FirebaseAuth> firebaseAuthProvider) {
    this.authServiceProvider = authServiceProvider;
    this.firebaseAuthProvider = firebaseAuthProvider;
  }

  @Override
  public AuthRepositoryImpl get() {
    return newInstance(authServiceProvider.get(), firebaseAuthProvider.get());
  }

  public static AuthRepositoryImpl_Factory create(Provider<FirebaseAuthService> authServiceProvider,
      Provider<FirebaseAuth> firebaseAuthProvider) {
    return new AuthRepositoryImpl_Factory(authServiceProvider, firebaseAuthProvider);
  }

  public static AuthRepositoryImpl newInstance(FirebaseAuthService authService,
      FirebaseAuth firebaseAuth) {
    return new AuthRepositoryImpl(authService, firebaseAuth);
  }
}
