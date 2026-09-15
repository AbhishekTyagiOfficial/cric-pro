package com.cricpro.app.data.remote;

import com.google.firebase.firestore.FirebaseFirestore;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class FirestoreService_Factory implements Factory<FirestoreService> {
  private final Provider<FirebaseFirestore> firestoreProvider;

  public FirestoreService_Factory(Provider<FirebaseFirestore> firestoreProvider) {
    this.firestoreProvider = firestoreProvider;
  }

  @Override
  public FirestoreService get() {
    return newInstance(firestoreProvider.get());
  }

  public static FirestoreService_Factory create(Provider<FirebaseFirestore> firestoreProvider) {
    return new FirestoreService_Factory(firestoreProvider);
  }

  public static FirestoreService newInstance(FirebaseFirestore firestore) {
    return new FirestoreService(firestore);
  }
}
