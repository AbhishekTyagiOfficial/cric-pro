package com.cricpro.app.data.local.dao;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.cricpro.app.data.local.entity.TournamentEntity;
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
public final class TournamentDao_Impl implements TournamentDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<TournamentEntity> __insertionAdapterOfTournamentEntity;

  public TournamentDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfTournamentEntity = new EntityInsertionAdapter<TournamentEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `tournaments` (`tournamentId`,`name`,`logoUrl`,`type`,`organizerId`,`startDate`,`endDate`,`tournamentJson`) VALUES (?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final TournamentEntity entity) {
        statement.bindString(1, entity.getTournamentId());
        statement.bindString(2, entity.getName());
        statement.bindString(3, entity.getLogoUrl());
        statement.bindString(4, entity.getType());
        statement.bindString(5, entity.getOrganizerId());
        statement.bindLong(6, entity.getStartDate());
        statement.bindLong(7, entity.getEndDate());
        statement.bindString(8, entity.getTournamentJson());
      }
    };
  }

  @Override
  public Object insertTournament(final TournamentEntity tournament,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfTournamentEntity.insert(tournament);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<TournamentEntity>> getTournaments() {
    final String _sql = "SELECT * FROM tournaments ORDER BY startDate DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"tournaments"}, new Callable<List<TournamentEntity>>() {
      @Override
      @NonNull
      public List<TournamentEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfTournamentId = CursorUtil.getColumnIndexOrThrow(_cursor, "tournamentId");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfLogoUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "logoUrl");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfOrganizerId = CursorUtil.getColumnIndexOrThrow(_cursor, "organizerId");
          final int _cursorIndexOfStartDate = CursorUtil.getColumnIndexOrThrow(_cursor, "startDate");
          final int _cursorIndexOfEndDate = CursorUtil.getColumnIndexOrThrow(_cursor, "endDate");
          final int _cursorIndexOfTournamentJson = CursorUtil.getColumnIndexOrThrow(_cursor, "tournamentJson");
          final List<TournamentEntity> _result = new ArrayList<TournamentEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TournamentEntity _item;
            final String _tmpTournamentId;
            _tmpTournamentId = _cursor.getString(_cursorIndexOfTournamentId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpLogoUrl;
            _tmpLogoUrl = _cursor.getString(_cursorIndexOfLogoUrl);
            final String _tmpType;
            _tmpType = _cursor.getString(_cursorIndexOfType);
            final String _tmpOrganizerId;
            _tmpOrganizerId = _cursor.getString(_cursorIndexOfOrganizerId);
            final long _tmpStartDate;
            _tmpStartDate = _cursor.getLong(_cursorIndexOfStartDate);
            final long _tmpEndDate;
            _tmpEndDate = _cursor.getLong(_cursorIndexOfEndDate);
            final String _tmpTournamentJson;
            _tmpTournamentJson = _cursor.getString(_cursorIndexOfTournamentJson);
            _item = new TournamentEntity(_tmpTournamentId,_tmpName,_tmpLogoUrl,_tmpType,_tmpOrganizerId,_tmpStartDate,_tmpEndDate,_tmpTournamentJson);
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
  public Flow<TournamentEntity> getTournamentById(final String tournamentId) {
    final String _sql = "SELECT * FROM tournaments WHERE tournamentId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, tournamentId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"tournaments"}, new Callable<TournamentEntity>() {
      @Override
      @Nullable
      public TournamentEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfTournamentId = CursorUtil.getColumnIndexOrThrow(_cursor, "tournamentId");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfLogoUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "logoUrl");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfOrganizerId = CursorUtil.getColumnIndexOrThrow(_cursor, "organizerId");
          final int _cursorIndexOfStartDate = CursorUtil.getColumnIndexOrThrow(_cursor, "startDate");
          final int _cursorIndexOfEndDate = CursorUtil.getColumnIndexOrThrow(_cursor, "endDate");
          final int _cursorIndexOfTournamentJson = CursorUtil.getColumnIndexOrThrow(_cursor, "tournamentJson");
          final TournamentEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpTournamentId;
            _tmpTournamentId = _cursor.getString(_cursorIndexOfTournamentId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpLogoUrl;
            _tmpLogoUrl = _cursor.getString(_cursorIndexOfLogoUrl);
            final String _tmpType;
            _tmpType = _cursor.getString(_cursorIndexOfType);
            final String _tmpOrganizerId;
            _tmpOrganizerId = _cursor.getString(_cursorIndexOfOrganizerId);
            final long _tmpStartDate;
            _tmpStartDate = _cursor.getLong(_cursorIndexOfStartDate);
            final long _tmpEndDate;
            _tmpEndDate = _cursor.getLong(_cursorIndexOfEndDate);
            final String _tmpTournamentJson;
            _tmpTournamentJson = _cursor.getString(_cursorIndexOfTournamentJson);
            _result = new TournamentEntity(_tmpTournamentId,_tmpName,_tmpLogoUrl,_tmpType,_tmpOrganizerId,_tmpStartDate,_tmpEndDate,_tmpTournamentJson);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
