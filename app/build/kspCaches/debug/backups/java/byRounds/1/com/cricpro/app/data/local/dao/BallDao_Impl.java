package com.cricpro.app.data.local.dao;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.cricpro.app.data.local.entity.BallEntity;
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
public final class BallDao_Impl implements BallDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<BallEntity> __insertionAdapterOfBallEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteBall;

  private final SharedSQLiteStatement __preparedStmtOfDeleteLastBall;

  public BallDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfBallEntity = new EntityInsertionAdapter<BallEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `balls` (`ballId`,`matchId`,`inningsNumber`,`overNumber`,`ballNumberInOver`,`strikerId`,`nonStrikerId`,`bowlerId`,`runsScored`,`extraType`,`extraRuns`,`isLegalDelivery`,`wicketType`,`dismissedPlayerId`,`timestamp`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final BallEntity entity) {
        statement.bindString(1, entity.getBallId());
        statement.bindString(2, entity.getMatchId());
        statement.bindLong(3, entity.getInningsNumber());
        statement.bindLong(4, entity.getOverNumber());
        statement.bindLong(5, entity.getBallNumberInOver());
        statement.bindString(6, entity.getStrikerId());
        statement.bindString(7, entity.getNonStrikerId());
        statement.bindString(8, entity.getBowlerId());
        statement.bindLong(9, entity.getRunsScored());
        statement.bindString(10, entity.getExtraType());
        statement.bindLong(11, entity.getExtraRuns());
        final int _tmp = entity.isLegalDelivery() ? 1 : 0;
        statement.bindLong(12, _tmp);
        statement.bindString(13, entity.getWicketType());
        if (entity.getDismissedPlayerId() == null) {
          statement.bindNull(14);
        } else {
          statement.bindString(14, entity.getDismissedPlayerId());
        }
        statement.bindLong(15, entity.getTimestamp());
      }
    };
    this.__preparedStmtOfDeleteBall = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM balls WHERE ballId = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteLastBall = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM balls WHERE matchId = ? AND inningsNumber = ? AND ballId = (SELECT ballId FROM balls WHERE matchId = ? AND inningsNumber = ? ORDER BY timestamp DESC LIMIT 1)";
        return _query;
      }
    };
  }

  @Override
  public Object insertBall(final BallEntity ball, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfBallEntity.insert(ball);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteBall(final String ballId, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteBall.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, ballId);
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
          __preparedStmtOfDeleteBall.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteLastBall(final String matchId, final int inningsNumber,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteLastBall.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, matchId);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, inningsNumber);
        _argIndex = 3;
        _stmt.bindString(_argIndex, matchId);
        _argIndex = 4;
        _stmt.bindLong(_argIndex, inningsNumber);
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
          __preparedStmtOfDeleteLastBall.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<BallEntity>> getBallsForInnings(final String matchId, final int inningsNumber) {
    final String _sql = "SELECT * FROM balls WHERE matchId = ? AND inningsNumber = ? ORDER BY timestamp ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, matchId);
    _argIndex = 2;
    _statement.bindLong(_argIndex, inningsNumber);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"balls"}, new Callable<List<BallEntity>>() {
      @Override
      @NonNull
      public List<BallEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfBallId = CursorUtil.getColumnIndexOrThrow(_cursor, "ballId");
          final int _cursorIndexOfMatchId = CursorUtil.getColumnIndexOrThrow(_cursor, "matchId");
          final int _cursorIndexOfInningsNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "inningsNumber");
          final int _cursorIndexOfOverNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "overNumber");
          final int _cursorIndexOfBallNumberInOver = CursorUtil.getColumnIndexOrThrow(_cursor, "ballNumberInOver");
          final int _cursorIndexOfStrikerId = CursorUtil.getColumnIndexOrThrow(_cursor, "strikerId");
          final int _cursorIndexOfNonStrikerId = CursorUtil.getColumnIndexOrThrow(_cursor, "nonStrikerId");
          final int _cursorIndexOfBowlerId = CursorUtil.getColumnIndexOrThrow(_cursor, "bowlerId");
          final int _cursorIndexOfRunsScored = CursorUtil.getColumnIndexOrThrow(_cursor, "runsScored");
          final int _cursorIndexOfExtraType = CursorUtil.getColumnIndexOrThrow(_cursor, "extraType");
          final int _cursorIndexOfExtraRuns = CursorUtil.getColumnIndexOrThrow(_cursor, "extraRuns");
          final int _cursorIndexOfIsLegalDelivery = CursorUtil.getColumnIndexOrThrow(_cursor, "isLegalDelivery");
          final int _cursorIndexOfWicketType = CursorUtil.getColumnIndexOrThrow(_cursor, "wicketType");
          final int _cursorIndexOfDismissedPlayerId = CursorUtil.getColumnIndexOrThrow(_cursor, "dismissedPlayerId");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final List<BallEntity> _result = new ArrayList<BallEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final BallEntity _item;
            final String _tmpBallId;
            _tmpBallId = _cursor.getString(_cursorIndexOfBallId);
            final String _tmpMatchId;
            _tmpMatchId = _cursor.getString(_cursorIndexOfMatchId);
            final int _tmpInningsNumber;
            _tmpInningsNumber = _cursor.getInt(_cursorIndexOfInningsNumber);
            final int _tmpOverNumber;
            _tmpOverNumber = _cursor.getInt(_cursorIndexOfOverNumber);
            final int _tmpBallNumberInOver;
            _tmpBallNumberInOver = _cursor.getInt(_cursorIndexOfBallNumberInOver);
            final String _tmpStrikerId;
            _tmpStrikerId = _cursor.getString(_cursorIndexOfStrikerId);
            final String _tmpNonStrikerId;
            _tmpNonStrikerId = _cursor.getString(_cursorIndexOfNonStrikerId);
            final String _tmpBowlerId;
            _tmpBowlerId = _cursor.getString(_cursorIndexOfBowlerId);
            final int _tmpRunsScored;
            _tmpRunsScored = _cursor.getInt(_cursorIndexOfRunsScored);
            final String _tmpExtraType;
            _tmpExtraType = _cursor.getString(_cursorIndexOfExtraType);
            final int _tmpExtraRuns;
            _tmpExtraRuns = _cursor.getInt(_cursorIndexOfExtraRuns);
            final boolean _tmpIsLegalDelivery;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsLegalDelivery);
            _tmpIsLegalDelivery = _tmp != 0;
            final String _tmpWicketType;
            _tmpWicketType = _cursor.getString(_cursorIndexOfWicketType);
            final String _tmpDismissedPlayerId;
            if (_cursor.isNull(_cursorIndexOfDismissedPlayerId)) {
              _tmpDismissedPlayerId = null;
            } else {
              _tmpDismissedPlayerId = _cursor.getString(_cursorIndexOfDismissedPlayerId);
            }
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            _item = new BallEntity(_tmpBallId,_tmpMatchId,_tmpInningsNumber,_tmpOverNumber,_tmpBallNumberInOver,_tmpStrikerId,_tmpNonStrikerId,_tmpBowlerId,_tmpRunsScored,_tmpExtraType,_tmpExtraRuns,_tmpIsLegalDelivery,_tmpWicketType,_tmpDismissedPlayerId,_tmpTimestamp);
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
