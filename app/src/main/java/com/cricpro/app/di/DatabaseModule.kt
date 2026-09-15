package com.cricpro.app.di

import android.content.Context
import androidx.room.Room
import com.cricpro.app.data.local.dao.*
import com.cricpro.app.data.local.db.CricProDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): CricProDatabase {
        return Room.databaseBuilder(
            context,
            CricProDatabase::class.java,
            "cricpro.db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideTeamDao(db: CricProDatabase): TeamDao = db.teamDao()

    @Provides
    fun providePlayerDao(db: CricProDatabase): PlayerDao = db.playerDao()

    @Provides
    fun provideMatchDao(db: CricProDatabase): MatchDao = db.matchDao()

    @Provides
    fun provideBallDao(db: CricProDatabase): BallDao = db.ballDao()

    @Provides
    fun provideTournamentDao(db: CricProDatabase): TournamentDao = db.tournamentDao()

    @Provides
    fun provideSyncQueueDao(db: CricProDatabase): SyncQueueDao = db.syncQueueDao()
}
