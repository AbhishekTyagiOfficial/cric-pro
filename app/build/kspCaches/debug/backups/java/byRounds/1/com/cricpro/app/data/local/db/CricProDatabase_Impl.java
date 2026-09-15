package com.cricpro.app.data.local.db;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.cricpro.app.data.local.dao.BallDao;
import com.cricpro.app.data.local.dao.BallDao_Impl;
import com.cricpro.app.data.local.dao.MatchDao;
import com.cricpro.app.data.local.dao.MatchDao_Impl;
import com.cricpro.app.data.local.dao.PlayerDao;
import com.cricpro.app.data.local.dao.PlayerDao_Impl;
import com.cricpro.app.data.local.dao.SyncQueueDao;
import com.cricpro.app.data.local.dao.SyncQueueDao_Impl;
import com.cricpro.app.data.local.dao.TeamDao;
import com.cricpro.app.data.local.dao.TeamDao_Impl;
import com.cricpro.app.data.local.dao.TournamentDao;
import com.cricpro.app.data.local.dao.TournamentDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class CricProDatabase_Impl extends CricProDatabase {
  private volatile TeamDao _teamDao;

  private volatile PlayerDao _playerDao;

  private volatile MatchDao _matchDao;

  private volatile BallDao _ballDao;

  private volatile TournamentDao _tournamentDao;

  private volatile SyncQueueDao _syncQueueDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `teams` (`teamId` TEXT NOT NULL, `teamName` TEXT NOT NULL, `teamLogo` TEXT NOT NULL, `ownerId` TEXT NOT NULL, `captainId` TEXT, `viceCaptainId` TEXT, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, PRIMARY KEY(`teamId`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `players` (`playerId` TEXT NOT NULL, `teamId` TEXT NOT NULL, `name` TEXT NOT NULL, `profilePhoto` TEXT NOT NULL, `role` TEXT NOT NULL, `battingStyle` TEXT NOT NULL, `bowlingStyle` TEXT NOT NULL, `isCaptain` INTEGER NOT NULL, `isViceCaptain` INTEGER NOT NULL, `matches` INTEGER NOT NULL, `runs` INTEGER NOT NULL, `wickets` INTEGER NOT NULL, `ballsFaced` INTEGER NOT NULL, `highestScore` INTEGER NOT NULL, `oversBowled` REAL NOT NULL, `runsConceded` INTEGER NOT NULL, PRIMARY KEY(`playerId`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `matches` (`matchId` TEXT NOT NULL, `tournamentId` TEXT, `title` TEXT NOT NULL, `matchType` TEXT NOT NULL, `totalOvers` INTEGER NOT NULL, `groundName` TEXT NOT NULL, `matchDate` INTEGER NOT NULL, `teamAId` TEXT NOT NULL, `teamAName` TEXT NOT NULL, `teamBId` TEXT NOT NULL, `teamBName` TEXT NOT NULL, `tossWinnerId` TEXT, `tossDecision` TEXT, `status` TEXT NOT NULL, `currentInningsNumber` INTEGER NOT NULL, `resultMessage` TEXT NOT NULL, `winnerTeamId` TEXT, `creatorId` TEXT NOT NULL, `scorerId` TEXT NOT NULL, `matchJson` TEXT NOT NULL, PRIMARY KEY(`matchId`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `balls` (`ballId` TEXT NOT NULL, `matchId` TEXT NOT NULL, `inningsNumber` INTEGER NOT NULL, `overNumber` INTEGER NOT NULL, `ballNumberInOver` INTEGER NOT NULL, `strikerId` TEXT NOT NULL, `nonStrikerId` TEXT NOT NULL, `bowlerId` TEXT NOT NULL, `runsScored` INTEGER NOT NULL, `extraType` TEXT NOT NULL, `extraRuns` INTEGER NOT NULL, `isLegalDelivery` INTEGER NOT NULL, `wicketType` TEXT NOT NULL, `dismissedPlayerId` TEXT, `timestamp` INTEGER NOT NULL, PRIMARY KEY(`ballId`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `tournaments` (`tournamentId` TEXT NOT NULL, `name` TEXT NOT NULL, `logoUrl` TEXT NOT NULL, `type` TEXT NOT NULL, `organizerId` TEXT NOT NULL, `startDate` INTEGER NOT NULL, `endDate` INTEGER NOT NULL, `tournamentJson` TEXT NOT NULL, PRIMARY KEY(`tournamentId`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `sync_queue` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `action` TEXT NOT NULL, `entityId` TEXT NOT NULL, `payloadJson` TEXT NOT NULL, `createdAt` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '4790666a0e684f8c81aae419bcb5b06a')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `teams`");
        db.execSQL("DROP TABLE IF EXISTS `players`");
        db.execSQL("DROP TABLE IF EXISTS `matches`");
        db.execSQL("DROP TABLE IF EXISTS `balls`");
        db.execSQL("DROP TABLE IF EXISTS `tournaments`");
        db.execSQL("DROP TABLE IF EXISTS `sync_queue`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsTeams = new HashMap<String, TableInfo.Column>(8);
        _columnsTeams.put("teamId", new TableInfo.Column("teamId", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTeams.put("teamName", new TableInfo.Column("teamName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTeams.put("teamLogo", new TableInfo.Column("teamLogo", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTeams.put("ownerId", new TableInfo.Column("ownerId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTeams.put("captainId", new TableInfo.Column("captainId", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTeams.put("viceCaptainId", new TableInfo.Column("viceCaptainId", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTeams.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTeams.put("updatedAt", new TableInfo.Column("updatedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysTeams = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesTeams = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoTeams = new TableInfo("teams", _columnsTeams, _foreignKeysTeams, _indicesTeams);
        final TableInfo _existingTeams = TableInfo.read(db, "teams");
        if (!_infoTeams.equals(_existingTeams)) {
          return new RoomOpenHelper.ValidationResult(false, "teams(com.cricpro.app.data.local.entity.TeamEntity).\n"
                  + " Expected:\n" + _infoTeams + "\n"
                  + " Found:\n" + _existingTeams);
        }
        final HashMap<String, TableInfo.Column> _columnsPlayers = new HashMap<String, TableInfo.Column>(16);
        _columnsPlayers.put("playerId", new TableInfo.Column("playerId", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPlayers.put("teamId", new TableInfo.Column("teamId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPlayers.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPlayers.put("profilePhoto", new TableInfo.Column("profilePhoto", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPlayers.put("role", new TableInfo.Column("role", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPlayers.put("battingStyle", new TableInfo.Column("battingStyle", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPlayers.put("bowlingStyle", new TableInfo.Column("bowlingStyle", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPlayers.put("isCaptain", new TableInfo.Column("isCaptain", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPlayers.put("isViceCaptain", new TableInfo.Column("isViceCaptain", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPlayers.put("matches", new TableInfo.Column("matches", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPlayers.put("runs", new TableInfo.Column("runs", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPlayers.put("wickets", new TableInfo.Column("wickets", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPlayers.put("ballsFaced", new TableInfo.Column("ballsFaced", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPlayers.put("highestScore", new TableInfo.Column("highestScore", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPlayers.put("oversBowled", new TableInfo.Column("oversBowled", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPlayers.put("runsConceded", new TableInfo.Column("runsConceded", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysPlayers = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesPlayers = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoPlayers = new TableInfo("players", _columnsPlayers, _foreignKeysPlayers, _indicesPlayers);
        final TableInfo _existingPlayers = TableInfo.read(db, "players");
        if (!_infoPlayers.equals(_existingPlayers)) {
          return new RoomOpenHelper.ValidationResult(false, "players(com.cricpro.app.data.local.entity.PlayerEntity).\n"
                  + " Expected:\n" + _infoPlayers + "\n"
                  + " Found:\n" + _existingPlayers);
        }
        final HashMap<String, TableInfo.Column> _columnsMatches = new HashMap<String, TableInfo.Column>(20);
        _columnsMatches.put("matchId", new TableInfo.Column("matchId", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMatches.put("tournamentId", new TableInfo.Column("tournamentId", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMatches.put("title", new TableInfo.Column("title", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMatches.put("matchType", new TableInfo.Column("matchType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMatches.put("totalOvers", new TableInfo.Column("totalOvers", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMatches.put("groundName", new TableInfo.Column("groundName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMatches.put("matchDate", new TableInfo.Column("matchDate", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMatches.put("teamAId", new TableInfo.Column("teamAId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMatches.put("teamAName", new TableInfo.Column("teamAName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMatches.put("teamBId", new TableInfo.Column("teamBId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMatches.put("teamBName", new TableInfo.Column("teamBName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMatches.put("tossWinnerId", new TableInfo.Column("tossWinnerId", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMatches.put("tossDecision", new TableInfo.Column("tossDecision", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMatches.put("status", new TableInfo.Column("status", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMatches.put("currentInningsNumber", new TableInfo.Column("currentInningsNumber", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMatches.put("resultMessage", new TableInfo.Column("resultMessage", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMatches.put("winnerTeamId", new TableInfo.Column("winnerTeamId", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMatches.put("creatorId", new TableInfo.Column("creatorId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMatches.put("scorerId", new TableInfo.Column("scorerId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMatches.put("matchJson", new TableInfo.Column("matchJson", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysMatches = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesMatches = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoMatches = new TableInfo("matches", _columnsMatches, _foreignKeysMatches, _indicesMatches);
        final TableInfo _existingMatches = TableInfo.read(db, "matches");
        if (!_infoMatches.equals(_existingMatches)) {
          return new RoomOpenHelper.ValidationResult(false, "matches(com.cricpro.app.data.local.entity.MatchEntity).\n"
                  + " Expected:\n" + _infoMatches + "\n"
                  + " Found:\n" + _existingMatches);
        }
        final HashMap<String, TableInfo.Column> _columnsBalls = new HashMap<String, TableInfo.Column>(15);
        _columnsBalls.put("ballId", new TableInfo.Column("ballId", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBalls.put("matchId", new TableInfo.Column("matchId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBalls.put("inningsNumber", new TableInfo.Column("inningsNumber", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBalls.put("overNumber", new TableInfo.Column("overNumber", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBalls.put("ballNumberInOver", new TableInfo.Column("ballNumberInOver", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBalls.put("strikerId", new TableInfo.Column("strikerId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBalls.put("nonStrikerId", new TableInfo.Column("nonStrikerId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBalls.put("bowlerId", new TableInfo.Column("bowlerId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBalls.put("runsScored", new TableInfo.Column("runsScored", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBalls.put("extraType", new TableInfo.Column("extraType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBalls.put("extraRuns", new TableInfo.Column("extraRuns", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBalls.put("isLegalDelivery", new TableInfo.Column("isLegalDelivery", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBalls.put("wicketType", new TableInfo.Column("wicketType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBalls.put("dismissedPlayerId", new TableInfo.Column("dismissedPlayerId", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBalls.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysBalls = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesBalls = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoBalls = new TableInfo("balls", _columnsBalls, _foreignKeysBalls, _indicesBalls);
        final TableInfo _existingBalls = TableInfo.read(db, "balls");
        if (!_infoBalls.equals(_existingBalls)) {
          return new RoomOpenHelper.ValidationResult(false, "balls(com.cricpro.app.data.local.entity.BallEntity).\n"
                  + " Expected:\n" + _infoBalls + "\n"
                  + " Found:\n" + _existingBalls);
        }
        final HashMap<String, TableInfo.Column> _columnsTournaments = new HashMap<String, TableInfo.Column>(8);
        _columnsTournaments.put("tournamentId", new TableInfo.Column("tournamentId", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTournaments.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTournaments.put("logoUrl", new TableInfo.Column("logoUrl", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTournaments.put("type", new TableInfo.Column("type", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTournaments.put("organizerId", new TableInfo.Column("organizerId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTournaments.put("startDate", new TableInfo.Column("startDate", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTournaments.put("endDate", new TableInfo.Column("endDate", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTournaments.put("tournamentJson", new TableInfo.Column("tournamentJson", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysTournaments = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesTournaments = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoTournaments = new TableInfo("tournaments", _columnsTournaments, _foreignKeysTournaments, _indicesTournaments);
        final TableInfo _existingTournaments = TableInfo.read(db, "tournaments");
        if (!_infoTournaments.equals(_existingTournaments)) {
          return new RoomOpenHelper.ValidationResult(false, "tournaments(com.cricpro.app.data.local.entity.TournamentEntity).\n"
                  + " Expected:\n" + _infoTournaments + "\n"
                  + " Found:\n" + _existingTournaments);
        }
        final HashMap<String, TableInfo.Column> _columnsSyncQueue = new HashMap<String, TableInfo.Column>(5);
        _columnsSyncQueue.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncQueue.put("action", new TableInfo.Column("action", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncQueue.put("entityId", new TableInfo.Column("entityId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncQueue.put("payloadJson", new TableInfo.Column("payloadJson", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncQueue.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysSyncQueue = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesSyncQueue = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoSyncQueue = new TableInfo("sync_queue", _columnsSyncQueue, _foreignKeysSyncQueue, _indicesSyncQueue);
        final TableInfo _existingSyncQueue = TableInfo.read(db, "sync_queue");
        if (!_infoSyncQueue.equals(_existingSyncQueue)) {
          return new RoomOpenHelper.ValidationResult(false, "sync_queue(com.cricpro.app.data.local.entity.SyncQueueEntity).\n"
                  + " Expected:\n" + _infoSyncQueue + "\n"
                  + " Found:\n" + _existingSyncQueue);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "4790666a0e684f8c81aae419bcb5b06a", "3a170c49619f42a7b025a731a96a59bf");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "teams","players","matches","balls","tournaments","sync_queue");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `teams`");
      _db.execSQL("DELETE FROM `players`");
      _db.execSQL("DELETE FROM `matches`");
      _db.execSQL("DELETE FROM `balls`");
      _db.execSQL("DELETE FROM `tournaments`");
      _db.execSQL("DELETE FROM `sync_queue`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(TeamDao.class, TeamDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(PlayerDao.class, PlayerDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(MatchDao.class, MatchDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(BallDao.class, BallDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(TournamentDao.class, TournamentDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(SyncQueueDao.class, SyncQueueDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public TeamDao teamDao() {
    if (_teamDao != null) {
      return _teamDao;
    } else {
      synchronized(this) {
        if(_teamDao == null) {
          _teamDao = new TeamDao_Impl(this);
        }
        return _teamDao;
      }
    }
  }

  @Override
  public PlayerDao playerDao() {
    if (_playerDao != null) {
      return _playerDao;
    } else {
      synchronized(this) {
        if(_playerDao == null) {
          _playerDao = new PlayerDao_Impl(this);
        }
        return _playerDao;
      }
    }
  }

  @Override
  public MatchDao matchDao() {
    if (_matchDao != null) {
      return _matchDao;
    } else {
      synchronized(this) {
        if(_matchDao == null) {
          _matchDao = new MatchDao_Impl(this);
        }
        return _matchDao;
      }
    }
  }

  @Override
  public BallDao ballDao() {
    if (_ballDao != null) {
      return _ballDao;
    } else {
      synchronized(this) {
        if(_ballDao == null) {
          _ballDao = new BallDao_Impl(this);
        }
        return _ballDao;
      }
    }
  }

  @Override
  public TournamentDao tournamentDao() {
    if (_tournamentDao != null) {
      return _tournamentDao;
    } else {
      synchronized(this) {
        if(_tournamentDao == null) {
          _tournamentDao = new TournamentDao_Impl(this);
        }
        return _tournamentDao;
      }
    }
  }

  @Override
  public SyncQueueDao syncQueueDao() {
    if (_syncQueueDao != null) {
      return _syncQueueDao;
    } else {
      synchronized(this) {
        if(_syncQueueDao == null) {
          _syncQueueDao = new SyncQueueDao_Impl(this);
        }
        return _syncQueueDao;
      }
    }
  }
}
