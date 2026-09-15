package com.cricpro.app.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.cricpro.app.data.local.entity.MatchEntity;
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
public final class MatchDao_Impl implements MatchDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<MatchEntity> __insertionAdapterOfMatchEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteMatch;

  public MatchDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfMatchEntity = new EntityInsertionAdapter<MatchEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `matches` (`matchId`,`tournamentId`,`title`,`matchType`,`totalOvers`,`groundName`,`matchDate`,`teamAId`,`teamAName`,`teamBId`,`teamBName`,`tossWinnerId`,`tossDecision`,`status`,`currentInningsNumber`,`resultMessage`,`winnerTeamId`,`creatorId`,`scorerId`,`matchJson`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MatchEntity entity) {
        statement.bindString(1, entity.getMatchId());
        if (entity.getTournamentId() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getTournamentId());
        }
        statement.bindString(3, entity.getTitle());
        statement.bindString(4, entity.getMatchType());
        statement.bindLong(5, entity.getTotalOvers());
        statement.bindString(6, entity.getGroundName());
        statement.bindLong(7, entity.getMatchDate());
        statement.bindString(8, entity.getTeamAId());
        statement.bindString(9, entity.getTeamAName());
        statement.bindString(10, entity.getTeamBId());
        statement.bindString(11, entity.getTeamBName());
        if (entity.getTossWinnerId() == null) {
          statement.bindNull(12);
        } else {
          statement.bindString(12, entity.getTossWinnerId());
        }
        if (entity.getTossDecision() == null) {
          statement.bindNull(13);
        } else {
          statement.bindString(13, entity.getTossDecision());
        }
        statement.bindString(14, entity.getStatus());
        statement.bindLong(15, entity.getCurrentInningsNumber());
        statement.bindString(16, entity.getResultMessage());
        if (entity.getWinnerTeamId() == null) {
          statement.bindNull(17);
        } else {
          statement.bindString(17, entity.getWinnerTeamId());
        }
        statement.bindString(18, entity.getCreatorId());
        statement.bindString(19, entity.getScorerId());
        statement.bindString(20, entity.getMatchJson());
      }
    };
    this.__preparedStmtOfDeleteMatch = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM matches WHERE matchId = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertMatch(final MatchEntity match, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfMatchEntity.insert(match);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteMatch(final String matchId, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteMatch.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, matchId);
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
          __preparedStmtOfDeleteMatch.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<MatchEntity>> getMatches() {
    final String _sql = "SELECT * FROM matches WHERE UPPER(status) != 'COMPLETED' ORDER BY matchDate DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"matches"}, new Callable<List<MatchEntity>>() {
      @Override
      @NonNull
      public List<MatchEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfMatchId = CursorUtil.getColumnIndexOrThrow(_cursor, "matchId");
          final int _cursorIndexOfTournamentId = CursorUtil.getColumnIndexOrThrow(_cursor, "tournamentId");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfMatchType = CursorUtil.getColumnIndexOrThrow(_cursor, "matchType");
          final int _cursorIndexOfTotalOvers = CursorUtil.getColumnIndexOrThrow(_cursor, "totalOvers");
          final int _cursorIndexOfGroundName = CursorUtil.getColumnIndexOrThrow(_cursor, "groundName");
          final int _cursorIndexOfMatchDate = CursorUtil.getColumnIndexOrThrow(_cursor, "matchDate");
          final int _cursorIndexOfTeamAId = CursorUtil.getColumnIndexOrThrow(_cursor, "teamAId");
          final int _cursorIndexOfTeamAName = CursorUtil.getColumnIndexOrThrow(_cursor, "teamAName");
          final int _cursorIndexOfTeamBId = CursorUtil.getColumnIndexOrThrow(_cursor, "teamBId");
          final int _cursorIndexOfTeamBName = CursorUtil.getColumnIndexOrThrow(_cursor, "teamBName");
          final int _cursorIndexOfTossWinnerId = CursorUtil.getColumnIndexOrThrow(_cursor, "tossWinnerId");
          final int _cursorIndexOfTossDecision = CursorUtil.getColumnIndexOrThrow(_cursor, "tossDecision");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfCurrentInningsNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "currentInningsNumber");
          final int _cursorIndexOfResultMessage = CursorUtil.getColumnIndexOrThrow(_cursor, "resultMessage");
          final int _cursorIndexOfWinnerTeamId = CursorUtil.getColumnIndexOrThrow(_cursor, "winnerTeamId");
          final int _cursorIndexOfCreatorId = CursorUtil.getColumnIndexOrThrow(_cursor, "creatorId");
          final int _cursorIndexOfScorerId = CursorUtil.getColumnIndexOrThrow(_cursor, "scorerId");
          final int _cursorIndexOfMatchJson = CursorUtil.getColumnIndexOrThrow(_cursor, "matchJson");
          final List<MatchEntity> _result = new ArrayList<MatchEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MatchEntity _item;
            final String _tmpMatchId;
            _tmpMatchId = _cursor.getString(_cursorIndexOfMatchId);
            final String _tmpTournamentId;
            if (_cursor.isNull(_cursorIndexOfTournamentId)) {
              _tmpTournamentId = null;
            } else {
              _tmpTournamentId = _cursor.getString(_cursorIndexOfTournamentId);
            }
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpMatchType;
            _tmpMatchType = _cursor.getString(_cursorIndexOfMatchType);
            final int _tmpTotalOvers;
            _tmpTotalOvers = _cursor.getInt(_cursorIndexOfTotalOvers);
            final String _tmpGroundName;
            _tmpGroundName = _cursor.getString(_cursorIndexOfGroundName);
            final long _tmpMatchDate;
            _tmpMatchDate = _cursor.getLong(_cursorIndexOfMatchDate);
            final String _tmpTeamAId;
            _tmpTeamAId = _cursor.getString(_cursorIndexOfTeamAId);
            final String _tmpTeamAName;
            _tmpTeamAName = _cursor.getString(_cursorIndexOfTeamAName);
            final String _tmpTeamBId;
            _tmpTeamBId = _cursor.getString(_cursorIndexOfTeamBId);
            final String _tmpTeamBName;
            _tmpTeamBName = _cursor.getString(_cursorIndexOfTeamBName);
            final String _tmpTossWinnerId;
            if (_cursor.isNull(_cursorIndexOfTossWinnerId)) {
              _tmpTossWinnerId = null;
            } else {
              _tmpTossWinnerId = _cursor.getString(_cursorIndexOfTossWinnerId);
            }
            final String _tmpTossDecision;
            if (_cursor.isNull(_cursorIndexOfTossDecision)) {
              _tmpTossDecision = null;
            } else {
              _tmpTossDecision = _cursor.getString(_cursorIndexOfTossDecision);
            }
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final int _tmpCurrentInningsNumber;
            _tmpCurrentInningsNumber = _cursor.getInt(_cursorIndexOfCurrentInningsNumber);
            final String _tmpResultMessage;
            _tmpResultMessage = _cursor.getString(_cursorIndexOfResultMessage);
            final String _tmpWinnerTeamId;
            if (_cursor.isNull(_cursorIndexOfWinnerTeamId)) {
              _tmpWinnerTeamId = null;
            } else {
              _tmpWinnerTeamId = _cursor.getString(_cursorIndexOfWinnerTeamId);
            }
            final String _tmpCreatorId;
            _tmpCreatorId = _cursor.getString(_cursorIndexOfCreatorId);
            final String _tmpScorerId;
            _tmpScorerId = _cursor.getString(_cursorIndexOfScorerId);
            final String _tmpMatchJson;
            _tmpMatchJson = _cursor.getString(_cursorIndexOfMatchJson);
            _item = new MatchEntity(_tmpMatchId,_tmpTournamentId,_tmpTitle,_tmpMatchType,_tmpTotalOvers,_tmpGroundName,_tmpMatchDate,_tmpTeamAId,_tmpTeamAName,_tmpTeamBId,_tmpTeamBName,_tmpTossWinnerId,_tmpTossDecision,_tmpStatus,_tmpCurrentInningsNumber,_tmpResultMessage,_tmpWinnerTeamId,_tmpCreatorId,_tmpScorerId,_tmpMatchJson);
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
  public Flow<List<MatchEntity>> getCompletedMatches() {
    final String _sql = "SELECT * FROM matches WHERE UPPER(status) = 'COMPLETED' ORDER BY matchDate DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"matches"}, new Callable<List<MatchEntity>>() {
      @Override
      @NonNull
      public List<MatchEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfMatchId = CursorUtil.getColumnIndexOrThrow(_cursor, "matchId");
          final int _cursorIndexOfTournamentId = CursorUtil.getColumnIndexOrThrow(_cursor, "tournamentId");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfMatchType = CursorUtil.getColumnIndexOrThrow(_cursor, "matchType");
          final int _cursorIndexOfTotalOvers = CursorUtil.getColumnIndexOrThrow(_cursor, "totalOvers");
          final int _cursorIndexOfGroundName = CursorUtil.getColumnIndexOrThrow(_cursor, "groundName");
          final int _cursorIndexOfMatchDate = CursorUtil.getColumnIndexOrThrow(_cursor, "matchDate");
          final int _cursorIndexOfTeamAId = CursorUtil.getColumnIndexOrThrow(_cursor, "teamAId");
          final int _cursorIndexOfTeamAName = CursorUtil.getColumnIndexOrThrow(_cursor, "teamAName");
          final int _cursorIndexOfTeamBId = CursorUtil.getColumnIndexOrThrow(_cursor, "teamBId");
          final int _cursorIndexOfTeamBName = CursorUtil.getColumnIndexOrThrow(_cursor, "teamBName");
          final int _cursorIndexOfTossWinnerId = CursorUtil.getColumnIndexOrThrow(_cursor, "tossWinnerId");
          final int _cursorIndexOfTossDecision = CursorUtil.getColumnIndexOrThrow(_cursor, "tossDecision");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfCurrentInningsNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "currentInningsNumber");
          final int _cursorIndexOfResultMessage = CursorUtil.getColumnIndexOrThrow(_cursor, "resultMessage");
          final int _cursorIndexOfWinnerTeamId = CursorUtil.getColumnIndexOrThrow(_cursor, "winnerTeamId");
          final int _cursorIndexOfCreatorId = CursorUtil.getColumnIndexOrThrow(_cursor, "creatorId");
          final int _cursorIndexOfScorerId = CursorUtil.getColumnIndexOrThrow(_cursor, "scorerId");
          final int _cursorIndexOfMatchJson = CursorUtil.getColumnIndexOrThrow(_cursor, "matchJson");
          final List<MatchEntity> _result = new ArrayList<MatchEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MatchEntity _item;
            final String _tmpMatchId;
            _tmpMatchId = _cursor.getString(_cursorIndexOfMatchId);
            final String _tmpTournamentId;
            if (_cursor.isNull(_cursorIndexOfTournamentId)) {
              _tmpTournamentId = null;
            } else {
              _tmpTournamentId = _cursor.getString(_cursorIndexOfTournamentId);
            }
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpMatchType;
            _tmpMatchType = _cursor.getString(_cursorIndexOfMatchType);
            final int _tmpTotalOvers;
            _tmpTotalOvers = _cursor.getInt(_cursorIndexOfTotalOvers);
            final String _tmpGroundName;
            _tmpGroundName = _cursor.getString(_cursorIndexOfGroundName);
            final long _tmpMatchDate;
            _tmpMatchDate = _cursor.getLong(_cursorIndexOfMatchDate);
            final String _tmpTeamAId;
            _tmpTeamAId = _cursor.getString(_cursorIndexOfTeamAId);
            final String _tmpTeamAName;
            _tmpTeamAName = _cursor.getString(_cursorIndexOfTeamAName);
            final String _tmpTeamBId;
            _tmpTeamBId = _cursor.getString(_cursorIndexOfTeamBId);
            final String _tmpTeamBName;
            _tmpTeamBName = _cursor.getString(_cursorIndexOfTeamBName);
            final String _tmpTossWinnerId;
            if (_cursor.isNull(_cursorIndexOfTossWinnerId)) {
              _tmpTossWinnerId = null;
            } else {
              _tmpTossWinnerId = _cursor.getString(_cursorIndexOfTossWinnerId);
            }
            final String _tmpTossDecision;
            if (_cursor.isNull(_cursorIndexOfTossDecision)) {
              _tmpTossDecision = null;
            } else {
              _tmpTossDecision = _cursor.getString(_cursorIndexOfTossDecision);
            }
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final int _tmpCurrentInningsNumber;
            _tmpCurrentInningsNumber = _cursor.getInt(_cursorIndexOfCurrentInningsNumber);
            final String _tmpResultMessage;
            _tmpResultMessage = _cursor.getString(_cursorIndexOfResultMessage);
            final String _tmpWinnerTeamId;
            if (_cursor.isNull(_cursorIndexOfWinnerTeamId)) {
              _tmpWinnerTeamId = null;
            } else {
              _tmpWinnerTeamId = _cursor.getString(_cursorIndexOfWinnerTeamId);
            }
            final String _tmpCreatorId;
            _tmpCreatorId = _cursor.getString(_cursorIndexOfCreatorId);
            final String _tmpScorerId;
            _tmpScorerId = _cursor.getString(_cursorIndexOfScorerId);
            final String _tmpMatchJson;
            _tmpMatchJson = _cursor.getString(_cursorIndexOfMatchJson);
            _item = new MatchEntity(_tmpMatchId,_tmpTournamentId,_tmpTitle,_tmpMatchType,_tmpTotalOvers,_tmpGroundName,_tmpMatchDate,_tmpTeamAId,_tmpTeamAName,_tmpTeamBId,_tmpTeamBName,_tmpTossWinnerId,_tmpTossDecision,_tmpStatus,_tmpCurrentInningsNumber,_tmpResultMessage,_tmpWinnerTeamId,_tmpCreatorId,_tmpScorerId,_tmpMatchJson);
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
  public Flow<MatchEntity> getMatchById(final String matchId) {
    final String _sql = "SELECT * FROM matches WHERE matchId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, matchId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"matches"}, new Callable<MatchEntity>() {
      @Override
      @Nullable
      public MatchEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfMatchId = CursorUtil.getColumnIndexOrThrow(_cursor, "matchId");
          final int _cursorIndexOfTournamentId = CursorUtil.getColumnIndexOrThrow(_cursor, "tournamentId");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfMatchType = CursorUtil.getColumnIndexOrThrow(_cursor, "matchType");
          final int _cursorIndexOfTotalOvers = CursorUtil.getColumnIndexOrThrow(_cursor, "totalOvers");
          final int _cursorIndexOfGroundName = CursorUtil.getColumnIndexOrThrow(_cursor, "groundName");
          final int _cursorIndexOfMatchDate = CursorUtil.getColumnIndexOrThrow(_cursor, "matchDate");
          final int _cursorIndexOfTeamAId = CursorUtil.getColumnIndexOrThrow(_cursor, "teamAId");
          final int _cursorIndexOfTeamAName = CursorUtil.getColumnIndexOrThrow(_cursor, "teamAName");
          final int _cursorIndexOfTeamBId = CursorUtil.getColumnIndexOrThrow(_cursor, "teamBId");
          final int _cursorIndexOfTeamBName = CursorUtil.getColumnIndexOrThrow(_cursor, "teamBName");
          final int _cursorIndexOfTossWinnerId = CursorUtil.getColumnIndexOrThrow(_cursor, "tossWinnerId");
          final int _cursorIndexOfTossDecision = CursorUtil.getColumnIndexOrThrow(_cursor, "tossDecision");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfCurrentInningsNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "currentInningsNumber");
          final int _cursorIndexOfResultMessage = CursorUtil.getColumnIndexOrThrow(_cursor, "resultMessage");
          final int _cursorIndexOfWinnerTeamId = CursorUtil.getColumnIndexOrThrow(_cursor, "winnerTeamId");
          final int _cursorIndexOfCreatorId = CursorUtil.getColumnIndexOrThrow(_cursor, "creatorId");
          final int _cursorIndexOfScorerId = CursorUtil.getColumnIndexOrThrow(_cursor, "scorerId");
          final int _cursorIndexOfMatchJson = CursorUtil.getColumnIndexOrThrow(_cursor, "matchJson");
          final MatchEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpMatchId;
            _tmpMatchId = _cursor.getString(_cursorIndexOfMatchId);
            final String _tmpTournamentId;
            if (_cursor.isNull(_cursorIndexOfTournamentId)) {
              _tmpTournamentId = null;
            } else {
              _tmpTournamentId = _cursor.getString(_cursorIndexOfTournamentId);
            }
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpMatchType;
            _tmpMatchType = _cursor.getString(_cursorIndexOfMatchType);
            final int _tmpTotalOvers;
            _tmpTotalOvers = _cursor.getInt(_cursorIndexOfTotalOvers);
            final String _tmpGroundName;
            _tmpGroundName = _cursor.getString(_cursorIndexOfGroundName);
            final long _tmpMatchDate;
            _tmpMatchDate = _cursor.getLong(_cursorIndexOfMatchDate);
            final String _tmpTeamAId;
            _tmpTeamAId = _cursor.getString(_cursorIndexOfTeamAId);
            final String _tmpTeamAName;
            _tmpTeamAName = _cursor.getString(_cursorIndexOfTeamAName);
            final String _tmpTeamBId;
            _tmpTeamBId = _cursor.getString(_cursorIndexOfTeamBId);
            final String _tmpTeamBName;
            _tmpTeamBName = _cursor.getString(_cursorIndexOfTeamBName);
            final String _tmpTossWinnerId;
            if (_cursor.isNull(_cursorIndexOfTossWinnerId)) {
              _tmpTossWinnerId = null;
            } else {
              _tmpTossWinnerId = _cursor.getString(_cursorIndexOfTossWinnerId);
            }
            final String _tmpTossDecision;
            if (_cursor.isNull(_cursorIndexOfTossDecision)) {
              _tmpTossDecision = null;
            } else {
              _tmpTossDecision = _cursor.getString(_cursorIndexOfTossDecision);
            }
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final int _tmpCurrentInningsNumber;
            _tmpCurrentInningsNumber = _cursor.getInt(_cursorIndexOfCurrentInningsNumber);
            final String _tmpResultMessage;
            _tmpResultMessage = _cursor.getString(_cursorIndexOfResultMessage);
            final String _tmpWinnerTeamId;
            if (_cursor.isNull(_cursorIndexOfWinnerTeamId)) {
              _tmpWinnerTeamId = null;
            } else {
              _tmpWinnerTeamId = _cursor.getString(_cursorIndexOfWinnerTeamId);
            }
            final String _tmpCreatorId;
            _tmpCreatorId = _cursor.getString(_cursorIndexOfCreatorId);
            final String _tmpScorerId;
            _tmpScorerId = _cursor.getString(_cursorIndexOfScorerId);
            final String _tmpMatchJson;
            _tmpMatchJson = _cursor.getString(_cursorIndexOfMatchJson);
            _result = new MatchEntity(_tmpMatchId,_tmpTournamentId,_tmpTitle,_tmpMatchType,_tmpTotalOvers,_tmpGroundName,_tmpMatchDate,_tmpTeamAId,_tmpTeamAName,_tmpTeamBId,_tmpTeamBName,_tmpTossWinnerId,_tmpTossDecision,_tmpStatus,_tmpCurrentInningsNumber,_tmpResultMessage,_tmpWinnerTeamId,_tmpCreatorId,_tmpScorerId,_tmpMatchJson);
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
  public Object getMatchByIdDirect(final String matchId,
      final Continuation<? super MatchEntity> $completion) {
    final String _sql = "SELECT * FROM matches WHERE matchId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, matchId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<MatchEntity>() {
      @Override
      @Nullable
      public MatchEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfMatchId = CursorUtil.getColumnIndexOrThrow(_cursor, "matchId");
          final int _cursorIndexOfTournamentId = CursorUtil.getColumnIndexOrThrow(_cursor, "tournamentId");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfMatchType = CursorUtil.getColumnIndexOrThrow(_cursor, "matchType");
          final int _cursorIndexOfTotalOvers = CursorUtil.getColumnIndexOrThrow(_cursor, "totalOvers");
          final int _cursorIndexOfGroundName = CursorUtil.getColumnIndexOrThrow(_cursor, "groundName");
          final int _cursorIndexOfMatchDate = CursorUtil.getColumnIndexOrThrow(_cursor, "matchDate");
          final int _cursorIndexOfTeamAId = CursorUtil.getColumnIndexOrThrow(_cursor, "teamAId");
          final int _cursorIndexOfTeamAName = CursorUtil.getColumnIndexOrThrow(_cursor, "teamAName");
          final int _cursorIndexOfTeamBId = CursorUtil.getColumnIndexOrThrow(_cursor, "teamBId");
          final int _cursorIndexOfTeamBName = CursorUtil.getColumnIndexOrThrow(_cursor, "teamBName");
          final int _cursorIndexOfTossWinnerId = CursorUtil.getColumnIndexOrThrow(_cursor, "tossWinnerId");
          final int _cursorIndexOfTossDecision = CursorUtil.getColumnIndexOrThrow(_cursor, "tossDecision");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfCurrentInningsNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "currentInningsNumber");
          final int _cursorIndexOfResultMessage = CursorUtil.getColumnIndexOrThrow(_cursor, "resultMessage");
          final int _cursorIndexOfWinnerTeamId = CursorUtil.getColumnIndexOrThrow(_cursor, "winnerTeamId");
          final int _cursorIndexOfCreatorId = CursorUtil.getColumnIndexOrThrow(_cursor, "creatorId");
          final int _cursorIndexOfScorerId = CursorUtil.getColumnIndexOrThrow(_cursor, "scorerId");
          final int _cursorIndexOfMatchJson = CursorUtil.getColumnIndexOrThrow(_cursor, "matchJson");
          final MatchEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpMatchId;
            _tmpMatchId = _cursor.getString(_cursorIndexOfMatchId);
            final String _tmpTournamentId;
            if (_cursor.isNull(_cursorIndexOfTournamentId)) {
              _tmpTournamentId = null;
            } else {
              _tmpTournamentId = _cursor.getString(_cursorIndexOfTournamentId);
            }
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpMatchType;
            _tmpMatchType = _cursor.getString(_cursorIndexOfMatchType);
            final int _tmpTotalOvers;
            _tmpTotalOvers = _cursor.getInt(_cursorIndexOfTotalOvers);
            final String _tmpGroundName;
            _tmpGroundName = _cursor.getString(_cursorIndexOfGroundName);
            final long _tmpMatchDate;
            _tmpMatchDate = _cursor.getLong(_cursorIndexOfMatchDate);
            final String _tmpTeamAId;
            _tmpTeamAId = _cursor.getString(_cursorIndexOfTeamAId);
            final String _tmpTeamAName;
            _tmpTeamAName = _cursor.getString(_cursorIndexOfTeamAName);
            final String _tmpTeamBId;
            _tmpTeamBId = _cursor.getString(_cursorIndexOfTeamBId);
            final String _tmpTeamBName;
            _tmpTeamBName = _cursor.getString(_cursorIndexOfTeamBName);
            final String _tmpTossWinnerId;
            if (_cursor.isNull(_cursorIndexOfTossWinnerId)) {
              _tmpTossWinnerId = null;
            } else {
              _tmpTossWinnerId = _cursor.getString(_cursorIndexOfTossWinnerId);
            }
            final String _tmpTossDecision;
            if (_cursor.isNull(_cursorIndexOfTossDecision)) {
              _tmpTossDecision = null;
            } else {
              _tmpTossDecision = _cursor.getString(_cursorIndexOfTossDecision);
            }
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final int _tmpCurrentInningsNumber;
            _tmpCurrentInningsNumber = _cursor.getInt(_cursorIndexOfCurrentInningsNumber);
            final String _tmpResultMessage;
            _tmpResultMessage = _cursor.getString(_cursorIndexOfResultMessage);
            final String _tmpWinnerTeamId;
            if (_cursor.isNull(_cursorIndexOfWinnerTeamId)) {
              _tmpWinnerTeamId = null;
            } else {
              _tmpWinnerTeamId = _cursor.getString(_cursorIndexOfWinnerTeamId);
            }
            final String _tmpCreatorId;
            _tmpCreatorId = _cursor.getString(_cursorIndexOfCreatorId);
            final String _tmpScorerId;
            _tmpScorerId = _cursor.getString(_cursorIndexOfScorerId);
            final String _tmpMatchJson;
            _tmpMatchJson = _cursor.getString(_cursorIndexOfMatchJson);
            _result = new MatchEntity(_tmpMatchId,_tmpTournamentId,_tmpTitle,_tmpMatchType,_tmpTotalOvers,_tmpGroundName,_tmpMatchDate,_tmpTeamAId,_tmpTeamAName,_tmpTeamBId,_tmpTeamBName,_tmpTossWinnerId,_tmpTossDecision,_tmpStatus,_tmpCurrentInningsNumber,_tmpResultMessage,_tmpWinnerTeamId,_tmpCreatorId,_tmpScorerId,_tmpMatchJson);
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
