package com.cricpro.app.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
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
import com.cricpro.app.data.local.entity.TeamEntity;
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
public final class TeamDao_Impl implements TeamDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<TeamEntity> __insertionAdapterOfTeamEntity;

  private final EntityDeletionOrUpdateAdapter<TeamEntity> __updateAdapterOfTeamEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteTeam;

  public TeamDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfTeamEntity = new EntityInsertionAdapter<TeamEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `teams` (`teamId`,`teamName`,`teamLogo`,`ownerId`,`captainId`,`viceCaptainId`,`createdAt`,`updatedAt`) VALUES (?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final TeamEntity entity) {
        statement.bindString(1, entity.getTeamId());
        statement.bindString(2, entity.getTeamName());
        statement.bindString(3, entity.getTeamLogo());
        statement.bindString(4, entity.getOwnerId());
        if (entity.getCaptainId() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getCaptainId());
        }
        if (entity.getViceCaptainId() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getViceCaptainId());
        }
        statement.bindLong(7, entity.getCreatedAt());
        statement.bindLong(8, entity.getUpdatedAt());
      }
    };
    this.__updateAdapterOfTeamEntity = new EntityDeletionOrUpdateAdapter<TeamEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `teams` SET `teamId` = ?,`teamName` = ?,`teamLogo` = ?,`ownerId` = ?,`captainId` = ?,`viceCaptainId` = ?,`createdAt` = ?,`updatedAt` = ? WHERE `teamId` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final TeamEntity entity) {
        statement.bindString(1, entity.getTeamId());
        statement.bindString(2, entity.getTeamName());
        statement.bindString(3, entity.getTeamLogo());
        statement.bindString(4, entity.getOwnerId());
        if (entity.getCaptainId() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getCaptainId());
        }
        if (entity.getViceCaptainId() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getViceCaptainId());
        }
        statement.bindLong(7, entity.getCreatedAt());
        statement.bindLong(8, entity.getUpdatedAt());
        statement.bindString(9, entity.getTeamId());
      }
    };
    this.__preparedStmtOfDeleteTeam = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM teams WHERE teamId = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertTeam(final TeamEntity team, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfTeamEntity.insert(team);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateTeam(final TeamEntity team, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfTeamEntity.handle(team);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteTeam(final String teamId, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteTeam.acquire();
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
          __preparedStmtOfDeleteTeam.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<TeamEntity>> getTeams() {
    final String _sql = "SELECT * FROM teams ORDER BY updatedAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"teams"}, new Callable<List<TeamEntity>>() {
      @Override
      @NonNull
      public List<TeamEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfTeamId = CursorUtil.getColumnIndexOrThrow(_cursor, "teamId");
          final int _cursorIndexOfTeamName = CursorUtil.getColumnIndexOrThrow(_cursor, "teamName");
          final int _cursorIndexOfTeamLogo = CursorUtil.getColumnIndexOrThrow(_cursor, "teamLogo");
          final int _cursorIndexOfOwnerId = CursorUtil.getColumnIndexOrThrow(_cursor, "ownerId");
          final int _cursorIndexOfCaptainId = CursorUtil.getColumnIndexOrThrow(_cursor, "captainId");
          final int _cursorIndexOfViceCaptainId = CursorUtil.getColumnIndexOrThrow(_cursor, "viceCaptainId");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<TeamEntity> _result = new ArrayList<TeamEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TeamEntity _item;
            final String _tmpTeamId;
            _tmpTeamId = _cursor.getString(_cursorIndexOfTeamId);
            final String _tmpTeamName;
            _tmpTeamName = _cursor.getString(_cursorIndexOfTeamName);
            final String _tmpTeamLogo;
            _tmpTeamLogo = _cursor.getString(_cursorIndexOfTeamLogo);
            final String _tmpOwnerId;
            _tmpOwnerId = _cursor.getString(_cursorIndexOfOwnerId);
            final String _tmpCaptainId;
            if (_cursor.isNull(_cursorIndexOfCaptainId)) {
              _tmpCaptainId = null;
            } else {
              _tmpCaptainId = _cursor.getString(_cursorIndexOfCaptainId);
            }
            final String _tmpViceCaptainId;
            if (_cursor.isNull(_cursorIndexOfViceCaptainId)) {
              _tmpViceCaptainId = null;
            } else {
              _tmpViceCaptainId = _cursor.getString(_cursorIndexOfViceCaptainId);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new TeamEntity(_tmpTeamId,_tmpTeamName,_tmpTeamLogo,_tmpOwnerId,_tmpCaptainId,_tmpViceCaptainId,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Flow<TeamEntity> getTeamById(final String teamId) {
    final String _sql = "SELECT * FROM teams WHERE teamId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, teamId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"teams"}, new Callable<TeamEntity>() {
      @Override
      @Nullable
      public TeamEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfTeamId = CursorUtil.getColumnIndexOrThrow(_cursor, "teamId");
          final int _cursorIndexOfTeamName = CursorUtil.getColumnIndexOrThrow(_cursor, "teamName");
          final int _cursorIndexOfTeamLogo = CursorUtil.getColumnIndexOrThrow(_cursor, "teamLogo");
          final int _cursorIndexOfOwnerId = CursorUtil.getColumnIndexOrThrow(_cursor, "ownerId");
          final int _cursorIndexOfCaptainId = CursorUtil.getColumnIndexOrThrow(_cursor, "captainId");
          final int _cursorIndexOfViceCaptainId = CursorUtil.getColumnIndexOrThrow(_cursor, "viceCaptainId");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final TeamEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpTeamId;
            _tmpTeamId = _cursor.getString(_cursorIndexOfTeamId);
            final String _tmpTeamName;
            _tmpTeamName = _cursor.getString(_cursorIndexOfTeamName);
            final String _tmpTeamLogo;
            _tmpTeamLogo = _cursor.getString(_cursorIndexOfTeamLogo);
            final String _tmpOwnerId;
            _tmpOwnerId = _cursor.getString(_cursorIndexOfOwnerId);
            final String _tmpCaptainId;
            if (_cursor.isNull(_cursorIndexOfCaptainId)) {
              _tmpCaptainId = null;
            } else {
              _tmpCaptainId = _cursor.getString(_cursorIndexOfCaptainId);
            }
            final String _tmpViceCaptainId;
            if (_cursor.isNull(_cursorIndexOfViceCaptainId)) {
              _tmpViceCaptainId = null;
            } else {
              _tmpViceCaptainId = _cursor.getString(_cursorIndexOfViceCaptainId);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _result = new TeamEntity(_tmpTeamId,_tmpTeamName,_tmpTeamLogo,_tmpOwnerId,_tmpCaptainId,_tmpViceCaptainId,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Object getTeamByIdDirect(final String teamId,
      final Continuation<? super TeamEntity> $completion) {
    final String _sql = "SELECT * FROM teams WHERE teamId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, teamId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<TeamEntity>() {
      @Override
      @Nullable
      public TeamEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfTeamId = CursorUtil.getColumnIndexOrThrow(_cursor, "teamId");
          final int _cursorIndexOfTeamName = CursorUtil.getColumnIndexOrThrow(_cursor, "teamName");
          final int _cursorIndexOfTeamLogo = CursorUtil.getColumnIndexOrThrow(_cursor, "teamLogo");
          final int _cursorIndexOfOwnerId = CursorUtil.getColumnIndexOrThrow(_cursor, "ownerId");
          final int _cursorIndexOfCaptainId = CursorUtil.getColumnIndexOrThrow(_cursor, "captainId");
          final int _cursorIndexOfViceCaptainId = CursorUtil.getColumnIndexOrThrow(_cursor, "viceCaptainId");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final TeamEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpTeamId;
            _tmpTeamId = _cursor.getString(_cursorIndexOfTeamId);
            final String _tmpTeamName;
            _tmpTeamName = _cursor.getString(_cursorIndexOfTeamName);
            final String _tmpTeamLogo;
            _tmpTeamLogo = _cursor.getString(_cursorIndexOfTeamLogo);
            final String _tmpOwnerId;
            _tmpOwnerId = _cursor.getString(_cursorIndexOfOwnerId);
            final String _tmpCaptainId;
            if (_cursor.isNull(_cursorIndexOfCaptainId)) {
              _tmpCaptainId = null;
            } else {
              _tmpCaptainId = _cursor.getString(_cursorIndexOfCaptainId);
            }
            final String _tmpViceCaptainId;
            if (_cursor.isNull(_cursorIndexOfViceCaptainId)) {
              _tmpViceCaptainId = null;
            } else {
              _tmpViceCaptainId = _cursor.getString(_cursorIndexOfViceCaptainId);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _result = new TeamEntity(_tmpTeamId,_tmpTeamName,_tmpTeamLogo,_tmpOwnerId,_tmpCaptainId,_tmpViceCaptainId,_tmpCreatedAt,_tmpUpdatedAt);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
