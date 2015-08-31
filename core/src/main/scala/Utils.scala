package me.alecosta.scatbo

import java.text.SimpleDateFormat
import java.util.Date

object Logger
{
    def log(msg: String): Unit =
    {
        msg.split("\n").foreach(logLine)
    }

    private def logLine(line: String): Unit =
    {
        val dateStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        println("[" + dateStr + "] " + line)
    }
}
