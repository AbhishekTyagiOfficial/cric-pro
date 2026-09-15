package com.cricpro.app.utils

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import com.cricpro.app.domain.model.Match
import java.io.File
import java.io.FileOutputStream

object PdfExporter {

    fun exportMatchScorecard(context: Context, match: Match): File? {
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 Size
        val page = document.startPage(pageInfo)
        val canvas = page.canvas

        val paint = Paint().apply {
            color = Color.BLACK
            textSize = 16f
            isFakeBoldText = true
        }

        // Header
        canvas.drawText("CricPro Match Scorecard", 40f, 50f, paint)

        paint.textSize = 12f
        paint.isFakeBoldText = false
        canvas.drawText("Match: ${match.teamA.teamName} vs ${match.teamB.teamName}", 40f, 80f, paint)
        canvas.drawText("Type: ${match.matchType.name} (${match.totalOvers} Overs)", 40f, 100f, paint)
        canvas.drawText("Ground: ${match.groundName}", 40f, 120f, paint)
        canvas.drawText("Result: ${match.resultMessage.ifEmpty { "In Progress" }}", 40f, 140f, paint)

        paint.isFakeBoldText = true
        canvas.drawText("1st Innings: ${match.firstInnings?.totalRuns ?: 0}/${match.firstInnings?.wickets ?: 0} (${match.firstInnings?.oversFormatted ?: "0.0"} overs)", 40f, 180f, paint)
        canvas.drawText("2nd Innings: ${match.secondInnings?.totalRuns ?: 0}/${match.secondInnings?.wickets ?: 0} (${match.secondInnings?.oversFormatted ?: "0.0"} overs)", 40f, 210f, paint)

        document.finishPage(page)

        val file = File(context.cacheDir, "Match_${match.matchId}_Scorecard.pdf")
        return try {
            document.writeTo(FileOutputStream(file))
            document.close()
            file
        } catch (e: Exception) {
            document.close()
            null
        }
    }
}

object ExcelExporter {

    fun exportMatchToCsv(context: Context, match: Match): File? {
        val file = File(context.cacheDir, "Match_${match.matchId}_Export.csv")
        return try {
            val writer = file.bufferedWriter()
            writer.write("Match Title,${match.title}\n")
            writer.write("Team A,${match.teamA.teamName}\n")
            writer.write("Team B,${match.teamB.teamName}\n")
            writer.write("Result,${match.resultMessage}\n\n")

            writer.write("Innings,Team,Runs,Wickets,Overs\n")
            writer.write("1st Innings,${match.firstInnings?.battingTeamId},${match.firstInnings?.totalRuns},${match.firstInnings?.wickets},${match.firstInnings?.oversFormatted}\n")
            writer.write("2nd Innings,${match.secondInnings?.battingTeamId},${match.secondInnings?.totalRuns},${match.secondInnings?.wickets},${match.secondInnings?.oversFormatted}\n")

            writer.flush()
            writer.close()
            file
        } catch (e: Exception) {
            null
        }
    }
}
