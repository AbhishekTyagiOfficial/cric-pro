package com.cricpro.app;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.hilt.work.HiltWorkerFactory;
import androidx.hilt.work.WorkerAssistedFactory;
import androidx.hilt.work.WorkerFactoryModule_ProvideFactoryFactory;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import androidx.work.ListenableWorker;
import androidx.work.WorkerParameters;
import com.cricpro.app.data.local.dao.BallDao;
import com.cricpro.app.data.local.dao.MatchDao;
import com.cricpro.app.data.local.dao.PlayerDao;
import com.cricpro.app.data.local.dao.SyncQueueDao;
import com.cricpro.app.data.local.dao.TeamDao;
import com.cricpro.app.data.local.dao.TournamentDao;
import com.cricpro.app.data.local.db.CricProDatabase;
import com.cricpro.app.data.remote.FirebaseAuthService;
import com.cricpro.app.data.remote.FirestoreService;
import com.cricpro.app.data.repository.AuthRepositoryImpl;
import com.cricpro.app.data.repository.MatchRepositoryImpl;
import com.cricpro.app.data.repository.ScoringRepositoryImpl;
import com.cricpro.app.data.repository.SyncRepositoryImpl;
import com.cricpro.app.data.repository.TeamRepositoryImpl;
import com.cricpro.app.data.repository.TournamentRepositoryImpl;
import com.cricpro.app.data.sync.SyncWorker;
import com.cricpro.app.data.sync.SyncWorker_AssistedFactory;
import com.cricpro.app.di.DatabaseModule_ProvideBallDaoFactory;
import com.cricpro.app.di.DatabaseModule_ProvideDatabaseFactory;
import com.cricpro.app.di.DatabaseModule_ProvideMatchDaoFactory;
import com.cricpro.app.di.DatabaseModule_ProvidePlayerDaoFactory;
import com.cricpro.app.di.DatabaseModule_ProvideSyncQueueDaoFactory;
import com.cricpro.app.di.DatabaseModule_ProvideTeamDaoFactory;
import com.cricpro.app.di.DatabaseModule_ProvideTournamentDaoFactory;
import com.cricpro.app.di.FirebaseModule_ProvideFirebaseAuthFactory;
import com.cricpro.app.di.FirebaseModule_ProvideFirebaseFirestoreFactory;
import com.cricpro.app.di.FirebaseModule_ProvideScoringEngineFactory;
import com.cricpro.app.domain.engine.ScoringEngine;
import com.cricpro.app.domain.repository.AuthRepository;
import com.cricpro.app.domain.repository.MatchRepository;
import com.cricpro.app.domain.repository.ScoringRepository;
import com.cricpro.app.domain.repository.SyncRepository;
import com.cricpro.app.domain.repository.TeamRepository;
import com.cricpro.app.domain.repository.TournamentRepository;
import com.cricpro.app.domain.usecase.AssignCaptainUseCase;
import com.cricpro.app.domain.usecase.AssignViceCaptainUseCase;
import com.cricpro.app.domain.usecase.CalculatePointsTableUseCase;
import com.cricpro.app.domain.usecase.EditBallUseCase;
import com.cricpro.app.domain.usecase.ScoreBallUseCase;
import com.cricpro.app.domain.usecase.UndoBallUseCase;
import com.cricpro.app.presentation.auth.AuthViewModel;
import com.cricpro.app.presentation.auth.AuthViewModel_HiltModules;
import com.cricpro.app.presentation.home.HomeViewModel;
import com.cricpro.app.presentation.home.HomeViewModel_HiltModules;
import com.cricpro.app.presentation.match.MatchViewModel;
import com.cricpro.app.presentation.match.MatchViewModel_HiltModules;
import com.cricpro.app.presentation.match.ScoringViewModel;
import com.cricpro.app.presentation.match.ScoringViewModel_HiltModules;
import com.cricpro.app.presentation.team.TeamViewModel;
import com.cricpro.app.presentation.team.TeamViewModel_HiltModules;
import com.cricpro.app.presentation.tournament.TournamentViewModel;
import com.cricpro.app.presentation.tournament.TournamentViewModel_HiltModules;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import dagger.hilt.android.ActivityRetainedLifecycle;
import dagger.hilt.android.ViewModelLifecycle;
import dagger.hilt.android.internal.builders.ActivityComponentBuilder;
import dagger.hilt.android.internal.builders.ActivityRetainedComponentBuilder;
import dagger.hilt.android.internal.builders.FragmentComponentBuilder;
import dagger.hilt.android.internal.builders.ServiceComponentBuilder;
import dagger.hilt.android.internal.builders.ViewComponentBuilder;
import dagger.hilt.android.internal.builders.ViewModelComponentBuilder;
import dagger.hilt.android.internal.builders.ViewWithFragmentComponentBuilder;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories_InternalFactoryFactory_Factory;
import dagger.hilt.android.internal.managers.ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory;
import dagger.hilt.android.internal.managers.SavedStateHandleHolder;
import dagger.hilt.android.internal.modules.ApplicationContextModule;
import dagger.hilt.android.internal.modules.ApplicationContextModule_ProvideContextFactory;
import dagger.internal.DaggerGenerated;
import dagger.internal.DoubleCheck;
import dagger.internal.IdentifierNameString;
import dagger.internal.KeepFieldType;
import dagger.internal.LazyClassKeyMap;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.SingleCheck;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

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
public final class DaggerCricProApplication_HiltComponents_SingletonC {
  private DaggerCricProApplication_HiltComponents_SingletonC() {
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private ApplicationContextModule applicationContextModule;

    private Builder() {
    }

    public Builder applicationContextModule(ApplicationContextModule applicationContextModule) {
      this.applicationContextModule = Preconditions.checkNotNull(applicationContextModule);
      return this;
    }

    public CricProApplication_HiltComponents.SingletonC build() {
      Preconditions.checkBuilderRequirement(applicationContextModule, ApplicationContextModule.class);
      return new SingletonCImpl(applicationContextModule);
    }
  }

  private static final class ActivityRetainedCBuilder implements CricProApplication_HiltComponents.ActivityRetainedC.Builder {
    private final SingletonCImpl singletonCImpl;

    private SavedStateHandleHolder savedStateHandleHolder;

    private ActivityRetainedCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ActivityRetainedCBuilder savedStateHandleHolder(
        SavedStateHandleHolder savedStateHandleHolder) {
      this.savedStateHandleHolder = Preconditions.checkNotNull(savedStateHandleHolder);
      return this;
    }

    @Override
    public CricProApplication_HiltComponents.ActivityRetainedC build() {
      Preconditions.checkBuilderRequirement(savedStateHandleHolder, SavedStateHandleHolder.class);
      return new ActivityRetainedCImpl(singletonCImpl, savedStateHandleHolder);
    }
  }

  private static final class ActivityCBuilder implements CricProApplication_HiltComponents.ActivityC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private Activity activity;

    private ActivityCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ActivityCBuilder activity(Activity activity) {
      this.activity = Preconditions.checkNotNull(activity);
      return this;
    }

    @Override
    public CricProApplication_HiltComponents.ActivityC build() {
      Preconditions.checkBuilderRequirement(activity, Activity.class);
      return new ActivityCImpl(singletonCImpl, activityRetainedCImpl, activity);
    }
  }

  private static final class FragmentCBuilder implements CricProApplication_HiltComponents.FragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private Fragment fragment;

    private FragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public FragmentCBuilder fragment(Fragment fragment) {
      this.fragment = Preconditions.checkNotNull(fragment);
      return this;
    }

    @Override
    public CricProApplication_HiltComponents.FragmentC build() {
      Preconditions.checkBuilderRequirement(fragment, Fragment.class);
      return new FragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragment);
    }
  }

  private static final class ViewWithFragmentCBuilder implements CricProApplication_HiltComponents.ViewWithFragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private View view;

    private ViewWithFragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;
    }

    @Override
    public ViewWithFragmentCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public CricProApplication_HiltComponents.ViewWithFragmentC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewWithFragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl, view);
    }
  }

  private static final class ViewCBuilder implements CricProApplication_HiltComponents.ViewC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private View view;

    private ViewCBuilder(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public ViewCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public CricProApplication_HiltComponents.ViewC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, view);
    }
  }

  private static final class ViewModelCBuilder implements CricProApplication_HiltComponents.ViewModelC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private SavedStateHandle savedStateHandle;

    private ViewModelLifecycle viewModelLifecycle;

    private ViewModelCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ViewModelCBuilder savedStateHandle(SavedStateHandle handle) {
      this.savedStateHandle = Preconditions.checkNotNull(handle);
      return this;
    }

    @Override
    public ViewModelCBuilder viewModelLifecycle(ViewModelLifecycle viewModelLifecycle) {
      this.viewModelLifecycle = Preconditions.checkNotNull(viewModelLifecycle);
      return this;
    }

    @Override
    public CricProApplication_HiltComponents.ViewModelC build() {
      Preconditions.checkBuilderRequirement(savedStateHandle, SavedStateHandle.class);
      Preconditions.checkBuilderRequirement(viewModelLifecycle, ViewModelLifecycle.class);
      return new ViewModelCImpl(singletonCImpl, activityRetainedCImpl, savedStateHandle, viewModelLifecycle);
    }
  }

  private static final class ServiceCBuilder implements CricProApplication_HiltComponents.ServiceC.Builder {
    private final SingletonCImpl singletonCImpl;

    private Service service;

    private ServiceCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ServiceCBuilder service(Service service) {
      this.service = Preconditions.checkNotNull(service);
      return this;
    }

    @Override
    public CricProApplication_HiltComponents.ServiceC build() {
      Preconditions.checkBuilderRequirement(service, Service.class);
      return new ServiceCImpl(singletonCImpl, service);
    }
  }

  private static final class ViewWithFragmentCImpl extends CricProApplication_HiltComponents.ViewWithFragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private final ViewWithFragmentCImpl viewWithFragmentCImpl = this;

    private ViewWithFragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;


    }
  }

  private static final class FragmentCImpl extends CricProApplication_HiltComponents.FragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl = this;

    private FragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        Fragment fragmentParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return activityCImpl.getHiltInternalFactoryFactory();
    }

    @Override
    public ViewWithFragmentComponentBuilder viewWithFragmentComponentBuilder() {
      return new ViewWithFragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl);
    }
  }

  private static final class ViewCImpl extends CricProApplication_HiltComponents.ViewC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final ViewCImpl viewCImpl = this;

    private ViewCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }
  }

  private static final class ActivityCImpl extends CricProApplication_HiltComponents.ActivityC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl = this;

    private ActivityCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, Activity activityParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;


    }

    @Override
    public void injectMainActivity(MainActivity mainActivity) {
    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return DefaultViewModelFactories_InternalFactoryFactory_Factory.newInstance(getViewModelKeys(), new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl));
    }

    @Override
    public Map<Class<?>, Boolean> getViewModelKeys() {
      return LazyClassKeyMap.<Boolean>of(ImmutableMap.<String, Boolean>builderWithExpectedSize(6).put(LazyClassKeyProvider.com_cricpro_app_presentation_auth_AuthViewModel, AuthViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_cricpro_app_presentation_home_HomeViewModel, HomeViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_cricpro_app_presentation_match_MatchViewModel, MatchViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_cricpro_app_presentation_match_ScoringViewModel, ScoringViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_cricpro_app_presentation_team_TeamViewModel, TeamViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_cricpro_app_presentation_tournament_TournamentViewModel, TournamentViewModel_HiltModules.KeyModule.provide()).build());
    }

    @Override
    public ViewModelComponentBuilder getViewModelComponentBuilder() {
      return new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public FragmentComponentBuilder fragmentComponentBuilder() {
      return new FragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @Override
    public ViewComponentBuilder viewComponentBuilder() {
      return new ViewCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @IdentifierNameString
    private static final class LazyClassKeyProvider {
      static String com_cricpro_app_presentation_team_TeamViewModel = "com.cricpro.app.presentation.team.TeamViewModel";

      static String com_cricpro_app_presentation_tournament_TournamentViewModel = "com.cricpro.app.presentation.tournament.TournamentViewModel";

      static String com_cricpro_app_presentation_home_HomeViewModel = "com.cricpro.app.presentation.home.HomeViewModel";

      static String com_cricpro_app_presentation_match_MatchViewModel = "com.cricpro.app.presentation.match.MatchViewModel";

      static String com_cricpro_app_presentation_auth_AuthViewModel = "com.cricpro.app.presentation.auth.AuthViewModel";

      static String com_cricpro_app_presentation_match_ScoringViewModel = "com.cricpro.app.presentation.match.ScoringViewModel";

      @KeepFieldType
      TeamViewModel com_cricpro_app_presentation_team_TeamViewModel2;

      @KeepFieldType
      TournamentViewModel com_cricpro_app_presentation_tournament_TournamentViewModel2;

      @KeepFieldType
      HomeViewModel com_cricpro_app_presentation_home_HomeViewModel2;

      @KeepFieldType
      MatchViewModel com_cricpro_app_presentation_match_MatchViewModel2;

      @KeepFieldType
      AuthViewModel com_cricpro_app_presentation_auth_AuthViewModel2;

      @KeepFieldType
      ScoringViewModel com_cricpro_app_presentation_match_ScoringViewModel2;
    }
  }

  private static final class ViewModelCImpl extends CricProApplication_HiltComponents.ViewModelC {
    private final SavedStateHandle savedStateHandle;

    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ViewModelCImpl viewModelCImpl = this;

    private Provider<AuthViewModel> authViewModelProvider;

    private Provider<HomeViewModel> homeViewModelProvider;

    private Provider<MatchViewModel> matchViewModelProvider;

    private Provider<ScoringViewModel> scoringViewModelProvider;

    private Provider<TeamViewModel> teamViewModelProvider;

    private Provider<TournamentViewModel> tournamentViewModelProvider;

    private ViewModelCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, SavedStateHandle savedStateHandleParam,
        ViewModelLifecycle viewModelLifecycleParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.savedStateHandle = savedStateHandleParam;
      initialize(savedStateHandleParam, viewModelLifecycleParam);

    }

    private ScoreBallUseCase scoreBallUseCase() {
      return new ScoreBallUseCase(singletonCImpl.bindScoringRepositoryProvider.get(), singletonCImpl.bindMatchRepositoryProvider.get(), singletonCImpl.bindSyncRepositoryProvider.get(), singletonCImpl.provideScoringEngineProvider.get());
    }

    private UndoBallUseCase undoBallUseCase() {
      return new UndoBallUseCase(singletonCImpl.bindScoringRepositoryProvider.get(), singletonCImpl.provideScoringEngineProvider.get());
    }

    private EditBallUseCase editBallUseCase() {
      return new EditBallUseCase(singletonCImpl.bindScoringRepositoryProvider.get(), singletonCImpl.provideScoringEngineProvider.get());
    }

    private AssignCaptainUseCase assignCaptainUseCase() {
      return new AssignCaptainUseCase(singletonCImpl.bindTeamRepositoryProvider.get());
    }

    private AssignViceCaptainUseCase assignViceCaptainUseCase() {
      return new AssignViceCaptainUseCase(singletonCImpl.bindTeamRepositoryProvider.get());
    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandle savedStateHandleParam,
        final ViewModelLifecycle viewModelLifecycleParam) {
      this.authViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 0);
      this.homeViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 1);
      this.matchViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 2);
      this.scoringViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 3);
      this.teamViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 4);
      this.tournamentViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 5);
    }

    @Override
    public Map<Class<?>, javax.inject.Provider<ViewModel>> getHiltViewModelMap() {
      return LazyClassKeyMap.<javax.inject.Provider<ViewModel>>of(ImmutableMap.<String, javax.inject.Provider<ViewModel>>builderWithExpectedSize(6).put(LazyClassKeyProvider.com_cricpro_app_presentation_auth_AuthViewModel, ((Provider) authViewModelProvider)).put(LazyClassKeyProvider.com_cricpro_app_presentation_home_HomeViewModel, ((Provider) homeViewModelProvider)).put(LazyClassKeyProvider.com_cricpro_app_presentation_match_MatchViewModel, ((Provider) matchViewModelProvider)).put(LazyClassKeyProvider.com_cricpro_app_presentation_match_ScoringViewModel, ((Provider) scoringViewModelProvider)).put(LazyClassKeyProvider.com_cricpro_app_presentation_team_TeamViewModel, ((Provider) teamViewModelProvider)).put(LazyClassKeyProvider.com_cricpro_app_presentation_tournament_TournamentViewModel, ((Provider) tournamentViewModelProvider)).build());
    }

    @Override
    public Map<Class<?>, Object> getHiltViewModelAssistedMap() {
      return ImmutableMap.<Class<?>, Object>of();
    }

    @IdentifierNameString
    private static final class LazyClassKeyProvider {
      static String com_cricpro_app_presentation_tournament_TournamentViewModel = "com.cricpro.app.presentation.tournament.TournamentViewModel";

      static String com_cricpro_app_presentation_team_TeamViewModel = "com.cricpro.app.presentation.team.TeamViewModel";

      static String com_cricpro_app_presentation_match_MatchViewModel = "com.cricpro.app.presentation.match.MatchViewModel";

      static String com_cricpro_app_presentation_auth_AuthViewModel = "com.cricpro.app.presentation.auth.AuthViewModel";

      static String com_cricpro_app_presentation_match_ScoringViewModel = "com.cricpro.app.presentation.match.ScoringViewModel";

      static String com_cricpro_app_presentation_home_HomeViewModel = "com.cricpro.app.presentation.home.HomeViewModel";

      @KeepFieldType
      TournamentViewModel com_cricpro_app_presentation_tournament_TournamentViewModel2;

      @KeepFieldType
      TeamViewModel com_cricpro_app_presentation_team_TeamViewModel2;

      @KeepFieldType
      MatchViewModel com_cricpro_app_presentation_match_MatchViewModel2;

      @KeepFieldType
      AuthViewModel com_cricpro_app_presentation_auth_AuthViewModel2;

      @KeepFieldType
      ScoringViewModel com_cricpro_app_presentation_match_ScoringViewModel2;

      @KeepFieldType
      HomeViewModel com_cricpro_app_presentation_home_HomeViewModel2;
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final ViewModelCImpl viewModelCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          ViewModelCImpl viewModelCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.viewModelCImpl = viewModelCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.cricpro.app.presentation.auth.AuthViewModel 
          return (T) new AuthViewModel(singletonCImpl.bindAuthRepositoryProvider.get());

          case 1: // com.cricpro.app.presentation.home.HomeViewModel 
          return (T) new HomeViewModel(singletonCImpl.bindMatchRepositoryProvider.get(), singletonCImpl.bindTeamRepositoryProvider.get(), singletonCImpl.bindTournamentRepositoryProvider.get());

          case 2: // com.cricpro.app.presentation.match.MatchViewModel 
          return (T) new MatchViewModel(singletonCImpl.bindMatchRepositoryProvider.get());

          case 3: // com.cricpro.app.presentation.match.ScoringViewModel 
          return (T) new ScoringViewModel(singletonCImpl.bindMatchRepositoryProvider.get(), singletonCImpl.bindTeamRepositoryProvider.get(), singletonCImpl.bindScoringRepositoryProvider.get(), viewModelCImpl.scoreBallUseCase(), viewModelCImpl.undoBallUseCase(), viewModelCImpl.editBallUseCase(), viewModelCImpl.savedStateHandle);

          case 4: // com.cricpro.app.presentation.team.TeamViewModel 
          return (T) new TeamViewModel(singletonCImpl.bindTeamRepositoryProvider.get(), viewModelCImpl.assignCaptainUseCase(), viewModelCImpl.assignViceCaptainUseCase());

          case 5: // com.cricpro.app.presentation.tournament.TournamentViewModel 
          return (T) new TournamentViewModel(singletonCImpl.bindTournamentRepositoryProvider.get(), new CalculatePointsTableUseCase());

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ActivityRetainedCImpl extends CricProApplication_HiltComponents.ActivityRetainedC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl = this;

    private Provider<ActivityRetainedLifecycle> provideActivityRetainedLifecycleProvider;

    private ActivityRetainedCImpl(SingletonCImpl singletonCImpl,
        SavedStateHandleHolder savedStateHandleHolderParam) {
      this.singletonCImpl = singletonCImpl;

      initialize(savedStateHandleHolderParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandleHolder savedStateHandleHolderParam) {
      this.provideActivityRetainedLifecycleProvider = DoubleCheck.provider(new SwitchingProvider<ActivityRetainedLifecycle>(singletonCImpl, activityRetainedCImpl, 0));
    }

    @Override
    public ActivityComponentBuilder activityComponentBuilder() {
      return new ActivityCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public ActivityRetainedLifecycle getActivityRetainedLifecycle() {
      return provideActivityRetainedLifecycleProvider.get();
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // dagger.hilt.android.ActivityRetainedLifecycle 
          return (T) ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory.provideActivityRetainedLifecycle();

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ServiceCImpl extends CricProApplication_HiltComponents.ServiceC {
    private final SingletonCImpl singletonCImpl;

    private final ServiceCImpl serviceCImpl = this;

    private ServiceCImpl(SingletonCImpl singletonCImpl, Service serviceParam) {
      this.singletonCImpl = singletonCImpl;


    }
  }

  private static final class SingletonCImpl extends CricProApplication_HiltComponents.SingletonC {
    private final ApplicationContextModule applicationContextModule;

    private final SingletonCImpl singletonCImpl = this;

    private Provider<CricProDatabase> provideDatabaseProvider;

    private Provider<FirebaseFirestore> provideFirebaseFirestoreProvider;

    private Provider<FirestoreService> firestoreServiceProvider;

    private Provider<SyncRepositoryImpl> syncRepositoryImplProvider;

    private Provider<SyncRepository> bindSyncRepositoryProvider;

    private Provider<SyncWorker_AssistedFactory> syncWorker_AssistedFactoryProvider;

    private Provider<FirebaseAuth> provideFirebaseAuthProvider;

    private Provider<FirebaseAuthService> firebaseAuthServiceProvider;

    private Provider<AuthRepositoryImpl> authRepositoryImplProvider;

    private Provider<AuthRepository> bindAuthRepositoryProvider;

    private Provider<MatchRepositoryImpl> matchRepositoryImplProvider;

    private Provider<MatchRepository> bindMatchRepositoryProvider;

    private Provider<TeamRepositoryImpl> teamRepositoryImplProvider;

    private Provider<TeamRepository> bindTeamRepositoryProvider;

    private Provider<TournamentRepositoryImpl> tournamentRepositoryImplProvider;

    private Provider<TournamentRepository> bindTournamentRepositoryProvider;

    private Provider<ScoringRepositoryImpl> scoringRepositoryImplProvider;

    private Provider<ScoringRepository> bindScoringRepositoryProvider;

    private Provider<ScoringEngine> provideScoringEngineProvider;

    private SingletonCImpl(ApplicationContextModule applicationContextModuleParam) {
      this.applicationContextModule = applicationContextModuleParam;
      initialize(applicationContextModuleParam);

    }

    private SyncQueueDao syncQueueDao() {
      return DatabaseModule_ProvideSyncQueueDaoFactory.provideSyncQueueDao(provideDatabaseProvider.get());
    }

    private Map<String, javax.inject.Provider<WorkerAssistedFactory<? extends ListenableWorker>>> mapOfStringAndProviderOfWorkerAssistedFactoryOf(
        ) {
      return ImmutableMap.<String, javax.inject.Provider<WorkerAssistedFactory<? extends ListenableWorker>>>of("com.cricpro.app.data.sync.SyncWorker", ((Provider) syncWorker_AssistedFactoryProvider));
    }

    private HiltWorkerFactory hiltWorkerFactory() {
      return WorkerFactoryModule_ProvideFactoryFactory.provideFactory(mapOfStringAndProviderOfWorkerAssistedFactoryOf());
    }

    private MatchDao matchDao() {
      return DatabaseModule_ProvideMatchDaoFactory.provideMatchDao(provideDatabaseProvider.get());
    }

    private TeamDao teamDao() {
      return DatabaseModule_ProvideTeamDaoFactory.provideTeamDao(provideDatabaseProvider.get());
    }

    private PlayerDao playerDao() {
      return DatabaseModule_ProvidePlayerDaoFactory.providePlayerDao(provideDatabaseProvider.get());
    }

    private TournamentDao tournamentDao() {
      return DatabaseModule_ProvideTournamentDaoFactory.provideTournamentDao(provideDatabaseProvider.get());
    }

    private BallDao ballDao() {
      return DatabaseModule_ProvideBallDaoFactory.provideBallDao(provideDatabaseProvider.get());
    }

    @SuppressWarnings("unchecked")
    private void initialize(final ApplicationContextModule applicationContextModuleParam) {
      this.provideDatabaseProvider = DoubleCheck.provider(new SwitchingProvider<CricProDatabase>(singletonCImpl, 2));
      this.provideFirebaseFirestoreProvider = DoubleCheck.provider(new SwitchingProvider<FirebaseFirestore>(singletonCImpl, 4));
      this.firestoreServiceProvider = DoubleCheck.provider(new SwitchingProvider<FirestoreService>(singletonCImpl, 3));
      this.syncRepositoryImplProvider = new SwitchingProvider<>(singletonCImpl, 1);
      this.bindSyncRepositoryProvider = DoubleCheck.provider((Provider) syncRepositoryImplProvider);
      this.syncWorker_AssistedFactoryProvider = SingleCheck.provider(new SwitchingProvider<SyncWorker_AssistedFactory>(singletonCImpl, 0));
      this.provideFirebaseAuthProvider = DoubleCheck.provider(new SwitchingProvider<FirebaseAuth>(singletonCImpl, 7));
      this.firebaseAuthServiceProvider = DoubleCheck.provider(new SwitchingProvider<FirebaseAuthService>(singletonCImpl, 6));
      this.authRepositoryImplProvider = new SwitchingProvider<>(singletonCImpl, 5);
      this.bindAuthRepositoryProvider = DoubleCheck.provider((Provider) authRepositoryImplProvider);
      this.matchRepositoryImplProvider = new SwitchingProvider<>(singletonCImpl, 8);
      this.bindMatchRepositoryProvider = DoubleCheck.provider((Provider) matchRepositoryImplProvider);
      this.teamRepositoryImplProvider = new SwitchingProvider<>(singletonCImpl, 9);
      this.bindTeamRepositoryProvider = DoubleCheck.provider((Provider) teamRepositoryImplProvider);
      this.tournamentRepositoryImplProvider = new SwitchingProvider<>(singletonCImpl, 10);
      this.bindTournamentRepositoryProvider = DoubleCheck.provider((Provider) tournamentRepositoryImplProvider);
      this.scoringRepositoryImplProvider = new SwitchingProvider<>(singletonCImpl, 11);
      this.bindScoringRepositoryProvider = DoubleCheck.provider((Provider) scoringRepositoryImplProvider);
      this.provideScoringEngineProvider = DoubleCheck.provider(new SwitchingProvider<ScoringEngine>(singletonCImpl, 12));
    }

    @Override
    public void injectCricProApplication(CricProApplication cricProApplication) {
      injectCricProApplication2(cricProApplication);
    }

    @Override
    public Set<Boolean> getDisableFragmentGetContextFix() {
      return ImmutableSet.<Boolean>of();
    }

    @Override
    public ActivityRetainedComponentBuilder retainedComponentBuilder() {
      return new ActivityRetainedCBuilder(singletonCImpl);
    }

    @Override
    public ServiceComponentBuilder serviceComponentBuilder() {
      return new ServiceCBuilder(singletonCImpl);
    }

    @CanIgnoreReturnValue
    private CricProApplication injectCricProApplication2(CricProApplication instance) {
      CricProApplication_MembersInjector.injectWorkerFactory(instance, hiltWorkerFactory());
      return instance;
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.cricpro.app.data.sync.SyncWorker_AssistedFactory 
          return (T) new SyncWorker_AssistedFactory() {
            @Override
            public SyncWorker create(Context context, WorkerParameters params) {
              return new SyncWorker(context, params, singletonCImpl.bindSyncRepositoryProvider.get());
            }
          };

          case 1: // com.cricpro.app.data.repository.SyncRepositoryImpl 
          return (T) new SyncRepositoryImpl(singletonCImpl.syncQueueDao(), singletonCImpl.firestoreServiceProvider.get());

          case 2: // com.cricpro.app.data.local.db.CricProDatabase 
          return (T) DatabaseModule_ProvideDatabaseFactory.provideDatabase(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 3: // com.cricpro.app.data.remote.FirestoreService 
          return (T) new FirestoreService(singletonCImpl.provideFirebaseFirestoreProvider.get());

          case 4: // com.google.firebase.firestore.FirebaseFirestore 
          return (T) FirebaseModule_ProvideFirebaseFirestoreFactory.provideFirebaseFirestore();

          case 5: // com.cricpro.app.data.repository.AuthRepositoryImpl 
          return (T) new AuthRepositoryImpl(singletonCImpl.firebaseAuthServiceProvider.get(), singletonCImpl.provideFirebaseAuthProvider.get());

          case 6: // com.cricpro.app.data.remote.FirebaseAuthService 
          return (T) new FirebaseAuthService(singletonCImpl.provideFirebaseAuthProvider.get(), singletonCImpl.provideFirebaseFirestoreProvider.get());

          case 7: // com.google.firebase.auth.FirebaseAuth 
          return (T) FirebaseModule_ProvideFirebaseAuthFactory.provideFirebaseAuth();

          case 8: // com.cricpro.app.data.repository.MatchRepositoryImpl 
          return (T) new MatchRepositoryImpl(singletonCImpl.matchDao(), singletonCImpl.firestoreServiceProvider.get());

          case 9: // com.cricpro.app.data.repository.TeamRepositoryImpl 
          return (T) new TeamRepositoryImpl(singletonCImpl.teamDao(), singletonCImpl.playerDao(), singletonCImpl.firestoreServiceProvider.get());

          case 10: // com.cricpro.app.data.repository.TournamentRepositoryImpl 
          return (T) new TournamentRepositoryImpl(singletonCImpl.tournamentDao(), singletonCImpl.firestoreServiceProvider.get());

          case 11: // com.cricpro.app.data.repository.ScoringRepositoryImpl 
          return (T) new ScoringRepositoryImpl(singletonCImpl.ballDao(), singletonCImpl.matchDao(), singletonCImpl.firestoreServiceProvider.get());

          case 12: // com.cricpro.app.domain.engine.ScoringEngine 
          return (T) FirebaseModule_ProvideScoringEngineFactory.provideScoringEngine();

          default: throw new AssertionError(id);
        }
      }
    }
  }
}
