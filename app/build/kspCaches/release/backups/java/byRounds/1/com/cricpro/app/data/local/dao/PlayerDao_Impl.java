package com.cricpro.app.data.local.dao;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.cricpro.app.data.local.entity.PlayerEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class PlayerDao_Impl implements PlayerDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<PlayerEntity> __insertionAdapterOfPlayerEntity;

  private final EntityDeletionOrUpdateAdapter<PlayerEntity> __updateAdapterOfPlayerEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeletePlayer;

  private final SharedSQLiteStatement __preparedStmtOfDeletePlayersForTeam;

  public PlayerDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfPlayerEntity = new EntityInsertionAdapter<PlayerEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `players` (`playerId`,`teamId`,`name`,`profilePhoto`,`role`,`battingStyle`,`bowlingStyle`,`isCaptain`,`isViceCaptain`,`matches`,`runs`,`wickets`,`ballsFaced`,`highestScore`,`oversBowled`,`runsConceded`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final PlayerEntity entity) {
        statement.bindString(1, entity.getPlayerId());
        statement.bindString(2, entity.getTeamId());
        statement.bindString(3, entity.getName());
        statement.bindString(4, entity.getProfilePhoto());
        statement.bindString(5, entity.getRole());
        statement.bindString(6, entity.getBattingStyle());
        statement.bindString(7, entity.getBowlingStyle());
        final int _tmp = entity.isCaptain() ? 1 : 0;
        statement.bindLong(8, _tmp);
        final int _tmp_1 = entity.isViceCaptain() ? 1 : 0;
        statement.bindLong(9, _tmp_1);
        statement.bindLong(10, entity.getMatches());
        statement.bindLong(11, entity.getRuns());
        statement.bindLong(12, entity.getWickets());
        statement.bindLong(13, entity.getBallsFaced());
        statement.bindLong(14, entity.getHighestScore());
        statement.bindDouble(15, entity.getOversBowled());
        statement.bindLong(16, entity.getRunsConceded());
      }
    };
    this.__updateAdapterOfPlayerEntity = new EntityDeletionOrUpdateAdapter<PlayerEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `players` SET `playerId` = ?,`teamId` = ?,`name` = ?,`profilePhoto` = ?,`role` = ?,`battingStyle` = ?,`bowlingStyle` = ?,`isCaptain` = ?,`isViceCaptain` = ?,`matches` = ?,`runs` = ?,`wickets` = ?,`ballsFaced` = ?,`highestScore` = ?,`oversBowled` = ?,`runsConceded` = ? WHERE `playerId` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final PlayerEntity entity) {
        statement.bindString(1, entity.getPlayerId());
        statement.bindString(2, entity.getTeamId());
        statement.bindString(3, entity.getName());
        statement.bindString(4, entity.getProfilePhoto());
        statement.bindString(5, entity.getRole());
        statement.bindString(6, entity.getBattingStyle());
        statement.bindString(7, entity.getBowlingStyle());
        final int _tmp = entity.isCaptain() ? 1 : 0;
        statement.bindLong(8, _tmp);
        final int _tmp_1 = entity.isViceCaptain() ? 1 : 0;
        statement.bindLong(9, _tmp_1);
        statement.bindLong(10, entity.getMatches());
        statement.bindLong(11, entity.getRuns());
        statement.bindLong(12, entity.getWickets());
        statement.bindLong(13, entity.getBallsFaced());
        statement.bindLong(14, entity.getHighestScore());
        statement.bindDouble(15, entity.getOversBowled());
        statement.bindLong(16, entity.getRunsConceded());
        statement.bindString(17, entity.getPlayerId());
      }
    };
    this.__preparedStmtOfDeletePlayer = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM players WHERE playerId = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeletePlayersForTeam = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM players WHERE teamId = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertPlayer(final PlayerEntity player,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfPlayerEntity.insert(player);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertPlayers(final List<PlayerEntity> players,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfPlayerEntity.insert(players);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updatePlayer(final PlayerEntity player,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfPlayerEntity.handle(player);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deletePlayer(final String playerId, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeletePlayer.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, playerId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeletePlayer.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deletePlayersForTeam(final String teamId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeletePlayersForTeam.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, teamId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeletePlayersForTeam.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<PlayerEntity>> getPlayersForTeam(final String teamId) {
    final String _sql = "SELECT * FROM players WHERE teamId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, teamId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"players"}, new Callable<List<PlayerEntity>>() {
      @Override
      @NonNull
      public List<PlayerEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfPlayerId = CursorUtil.getColumnIndexOrThrow(_cursor, "playerId");
          final int _cursorIndexOfTeamId = CursorUtil.getColumnIndexOrThrow(_cursor, "teamId");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfProfilePhoto = CursorUtil.getColumnIndexOrThrow(_cursor, "profilePhoto");
          final int _cursorIndexOfRole = CursorUtil.getColumnIndexOrThrow(_cursor, "role");
          final int _cursorIndexOfBattingStyle = CursorUtil.getColumnIndexOrThrow(_cursor, "battingStyle");
          final int _cursorIndexOfBowlingStyle = CursorUtil.getColumnIndexOrThrow(_cursor, "bowlingStyle");
          final int _cursorIndexOfIsCaptain = CursorUtil.getColumnIndexOrThrow(_cursor, "isCaptain");
          final int _cursorIndexOfIsViceCaptain = CursorUtil.getColumnIndexOrThrow(_cursor, "isViceCaptain");
          final int _cursorIndexOfMatches = CursorUtil.getColumnIndexOrThrow(_cursor, "matches");
          final int _cursorIndexOfRuns = CursorUtil.getColumnIndexOrThrow(_cursor, "runs");
          final int _cursorIndexOfWickets = CursorUtil.getColumnIndexOrThrow(_cursor, "wickets");
          final int _cursorIndexOfBallsFaced = CursorUtil.getColumnIndexOrThrow(_cursor, "ballsFaced");
          final int _cursorIndexOfHighestScore = CursorUtil.getColumnIndexOrThrow(_cursor, "highestScore");
          final int _cursorIndexOfOversBowled = CursorUtil.getColumnIndexOrThrow(_cursor, "oversBowled");
          final int _cursorIndexOfRunsConceded = CursorUtil.getColumnIndexOrThrow(_cursor, "runsConceded");
          final List<PlayerEntity> _result = new ArrayList<PlayerEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PlayerEntity _item;
            final String _tmpPlayerId;
            _tmpPlayerId = _cursor.getString(_cursorIndexOfPlayerId);
            final String _tmpTeamId;
            _tmpTeamId = _cursor.getString(_cursorIndexOfTeamId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpProfilePhoto;
            _tmpProfilePhoto = _cursor.getString(_cursorIndexOfProfilePhoto);
            final String _tmpRole;
            _tmpRole = _cursor.getString(_cursorIndexOfRole);
            final String _tmpBattingStyle;
            _tmpBattingStyle = _cursor.getString(_cursorIndexOfBattingStyle);
            final String _tmpBowlingStyle;
            _tmpBowlingStyle = _cursor.getString(_cursorIndexOfBowlingStyle);
            final boolean _tmpIsCaptain;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsCaptain);
            _tmpIsCaptain = _tmp != 0;
            final boolean _tmpIsViceCaptain;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsViceCaptain);
            _tmpIsViceCaptain = _tmp_1 != 0;
            final int _tmpMatches;
            _tmpMatches = _cursor.getInt(_cursorIndexOfMatches);
            final int _tmpRuns;
            _tmpRuns = _cursor.getInt(_cursorIndexOfRuns);
            final int _tmpWickets;
            _tmpWickets = _cursor.getInt(_cursorIndexOfWickets);
            final int _tmpBallsFaced;
            _tmpBallsFaced = _cursor.getInt(_cursorIndexOfBallsFaced);
            final int _tmpHighestScore;
            _tmpHighestScore = _cursor.getInt(_cursorIndexOfHighestScore);
            final double _tmpOversBowled;
            _tmpOversBowled = _cursor.getDouble(_cursorIndexOfOversBowled);
            final int _tmpRunsConceded;
            _tmpRunsConceded = _cursor.getInt(_cursorIndexOfRunsConceded);
            _item = new PlayerEntity(_tmpPlayerId,_tmpTeamId,_tmpName,_tmpProfilePhoto,_tmpRole,_tmpBattingStyle,_tmpBowlingStyle,_tmpIsCaptain,_tmpIsViceCaptain,_tmpMatches,_tmpRuns,_tmpWickets,_tmpBallsFaced,_tmpHighestScore,_tmpOversBowled,_tmpRunsConceded);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<PlayerEntity> getPlayerById(final String playerId) {
    final String _sql = "SELECT * FROM players WHERE playerId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, playerId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"players"}, new Callable<PlayerEntity>() {
      @Override
      @Nullable
      public PlayerEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfPlayerId = CursorUtil.getColumnIndexOrThrow(_cursor, "playerId");
          final int _cursorIndexOfTeamId = CursorUtil.getColumnIndexOrThrow(_cursor, "teamId");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfProfilePhoto = CursorUtil.getColumnIndexOrThrow(_cursor, "profilePhoto");
          final int _cursorIndexOfRole = CursorUtil.getColumnIndexOrThrow(_cursor, "role");
          final int _cursorIndexOfBattingStyle = CursorUtil.getColumnIndexOrThrow(_cursor, "battingStyle");
          final int _cursorIndexOfBowlingStyle = CursorUtil.getColumnIndexOrThrow(_cursor, "bowlingStyle");
          final int _cursorIndexOfIsCaptain = CursorUtil.getColumnIndexOrThrow(_cursor, "isCaptain");
          final int _cursorIndexOfIsViceCaptain = CursorUtil.getColumnIndexOrThrow(_cursor, "isViceCaptain");
          final int _cursorIndexOfMatches = CursorUtil.getColumnIndexOrThrow(_cursor, "matches");
          final int _cursorIndexOfRuns = CursorUtil.getColumnIndexOrThrow(_cursor, "runs");
          final int _cursorIndexOfWickets = CursorUtil.getColumnIndexOrThrow(_cursor, "wickets");
          final int _cursorIndexOfBallsFaced = CursorUtil.getColumnIndexOrThrow(_cursor, "ballsFaced");
          final int _cursorIndexOfHighestScore = CursorUtil.getColumnIndexOrThrow(_cursor, "highestScore");
          final int _cursorIndexOfOversBowled = CursorUtil.getColumnIndexOrThrow(_cursor, "oversBowled");
          final int _cursorIndexOfRunsConceded = CursorUtil.getColumnIndexOrThrow(_cursor, "runsConceded");
          final PlayerEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpPlayerId;
            _tmpPlayerId = _cursor.getString(_cursorIndexOfPlayerId);
            final String _tmpTeamId;
            _tmpTeamId = _cursor.getString(_cursorIndexOfTeamId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpProfilePhoto;
            _tmpProfilePhoto = _cursor.getString(_cursorIndexOfProfilePhoto);
            final String _tmpRole;
            _tmpRole = _cursor.getString(_cursorIndexOfRole);
            final String _tmpBattingStyle;
            _tmpBattingStyle = _cursor.getString(_cursorIndexOfBattingStyle);
            final String _tmpBowlingStyle;
            _tmpBowlingStyle = _cursor.getString(_cursorIndexOfBowlingStyle);
            final boolean _tmpIsCaptain;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsCaptain);
            _tmpIsCaptain = _tmp != 0;
            final boolean _tmpIsViceCaptain;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsViceCaptain);
            _tmpIsViceCaptain = _tmp_1 != 0;
            final int _tmpMatches;
            _tmpMatches = _cursor.getInt(_cursorIndexOfMatches);
            final int _tmpRuns;
            _tmpRuns = _cursor.getInt(_cursorIndexOfRuns);
            final int _tmpWickets;
            _tmpWickets = _cursor.getInt(_cursorIndexOfWickets);
            final int _tmpBallsFaced;
            _tmpBallsFaced = _cursor.getInt(_cursorIndexOfBallsFaced);
            final int _tmpHighestScore;
            _tmpHighestScore = _cursor.getInt(_cursorIndexOfHighestScore);
            final double _tmpOversBowled;
            _tmpOversBowled = _cursor.getDouble(_cursorIndexOfOversBowled);
            final int _tmpRunsConceded;
            _tmpRunsConceded = _cursor.getInt(_cursorIndexOfRunsConceded);
            _result = new PlayerEntity(_tmpPlayerId,_tmpTeamId,_tmpName,_tmpProfilePhoto,_tmpRole,_tmpBattingStyle,_tmpBowlingStyle,_tmpIsCaptain,_tmpIsViceCaptain,_tmpMatches,_tmpRuns,_tmpWickets,_tmpBallsFaced,_tmpHighestScore,_tmpOversBowled,_tmpRunsConceded);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<PlayerEntity>> searchPlayers(final String query) {
    final String _sql = "SELECT * FROM players WHERE name LIKE '%' || ? || '%'";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, query);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"players"}, new Callable<List<PlayerEntity>>() {
      @Override
      @NonNull
      public List<PlayerEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfPlayerId = CursorUtil.getColumnIndexOrThrow(_cursor, "playerId");
          final int _cursorIndexOfTeamId = CursorUtil.getColumnIndexOrThrow(_cursor, "teamId");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfProfilePhoto = CursorUtil.getColumnIndexOrThrow(_cursor, "profilePhoto");
          final int _cursorIndexOfRole = CursorUtil.getColumnIndexOrThrow(_cursor, "role");
          final int _cursorIndexOfBattingStyle = CursorUtil.getColumnIndexOrThrow(_cursor, "battingStyle");
          final int _cursorIndexOfBowlingStyle = CursorUtil.getColumnIndexOrThrow(_cursor, "bowlingStyle");
          final int _cursorIndexOfIsCaptain = CursorUtil.getColumnIndexOrThrow(_cursor, "isCaptain");
          final int _cursorIndexOfIsViceCaptain = CursorUtil.getColumnIndexOrThrow(_cursor, "isViceCaptain");
          final int _cursorIndexOfMatches = CursorUtil.getColumnIndexOrThrow(_cursor, "matches");
          final int _cursorIndexOfRuns = CursorUtil.getColumnIndexOrThrow(_cursor, "runs");
          final int _cursorIndexOfWickets = CursorUtil.getColumnIndexOrThrow(_cursor, "wickets");
          final int _cursorIndexOfBallsFaced = CursorUtil.getColumnIndexOrThrow(_cursor, "ballsFaced");
          final int _cursorIndexOfHighestScore = CursorUtil.getColumnIndexOrThrow(_cursor, "highestScore");
          final int _cursorIndexOfOversBowled = CursorUtil.getColumnIndexOrThrow(_cursor, "oversBowled");
          final int _cursorIndexOfRunsConceded = CursorUtil.getColumnIndexOrThrow(_cursor, "runsConceded");
          final List<PlayerEntity> _result = new ArrayList<PlayerEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PlayerEntity _item;
            final String _tmpPlayerId;
            _tmpPlayerId = _cursor.getString(_cursorIndexOfPlayerId);
            final String _tmpTeamId;
            _tmpTeamId = _cursor.getString(_cursorIndexOfTeamId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpProfilePhoto;
            _tmpProfilePhoto = _cursor.getString(_cursorIndexOfProfilePhoto);
            final String _tmpRole;
            _tmpRole = _cursor.getString(_cursorIndexOfRole);
            final String _tmpBattingStyle;
            _tmpBattingStyle = _cursor.getString(_cursorIndexOfBattingStyle);
            final String _tmpBowlingStyle;
            _tmpBowlingStyle = _cursor.getString(_cursorIndexOfBowlingStyle);
            final boolean _tmpIsCaptain;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsCaptain);
            _tmpIsCaptain = _tmp != 0;
            final boolean _tmpIsViceCaptain;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsViceCaptain);
            _tmpIsViceCaptain = _tmp_1 != 0;
            final int _tmpMatches;
            _tmpMatches = _cursor.getInt(_cursorIndexOfMatches);
            final int _tmpRuns;
            _tmpRuns = _cursor.getInt(_cursorIndexOfRuns);
            final int _tmpWickets;
            _tmpWickets = _cursor.getInt(_cursorIndexOfWickets);
            final int _tmpBallsFaced;
            _tmpBallsFaced = _cursor.getInt(_cursorIndexOfBallsFaced);
            final int _tmpHighestScore;
            _tmpHighestScore = _cursor.getInt(_cursorIndexOfHighestScore);
            final double _tmpOversBowled;
            _tmpOversBowled = _cursor.getDouble(_cursorIndexOfOversBowled);
            final int _tmpRunsConceded;
            _tmpRunsConceded = _cursor.getInt(_cursorIndexOfRunsConceded);
            _item = new PlayerEntity(_tmpPlayerId,_tmpTeamId,_tmpName,_tmpProfilePhoto,_tmpRole,_tmpBattingStyle,_tmpBowlingStyle,_tmpIsCaptain,_tmpIsViceCaptain,_tmpMatches,_tmpRuns,_tmpWickets,_tmpBallsFaced,_tmpHighestScore,_tmpOversBowled,_tmpRunsConceded);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
