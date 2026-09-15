package com.cricpro.app.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.cricpro.app.data.local.dao.*
import com.cricpro.app.data.local.entity.*

@Database(
    entities = [
        TeamEntity::class,
        PlayerEntity::class,
        MatchEntity::class,
        BallEntity::class,
        TournamentEntity::class,
        SyncQueueEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class CricProDatabase : RoomDatabase() {
    abstract fun teamDao(): TeamDao
    abstract fun playerDao(): PlayerDao
    abstract fun matchDao(): MatchDao
    abstract fun ballDao(): BallDao
    abstract fun tournamentDao(): TournamentDao
    abstract fun syncQueueDao(): SyncQueueDao
}
