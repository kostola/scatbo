package me.alecosta.scatbo

import java.io.FileInputStream
import scalaj.http._

object Telegram {
    var botToken: String = ""
    var botUser: String = ""

    def urlFor(method: String): String =
        "https://api.telegram.org/bot" + botToken + "/" + method

    def canConnect =
        getMe.is2xx

    def getMe =
        Http(urlFor("getMe")).asString

    def getUpdates(offset: Int = -1) =
        Http(urlFor("getUpdates" + ( if (offset < 0) "" else ("?offset=" + offset)))).asString

    // TODO: missing 'disable_web_page_preview' and 'reply_markup' parameters
    def sendMessage(chatId: Int, text: String, replyTo: Int = -1) = {
        val p = collection.mutable.Map[String,String](
            "chat_id" -> chatId.toString,
            "text" -> text
        )
        if (replyTo > 0) {
            p += ( "reply_to_message_id" -> replyTo.toString )
        }

        Http(urlFor("sendMessage")).params(p.toMap).asString
    }

    def forwardMessage(msgId: Int, fromChatId: Int, toChatId: Int) = {
        val p = collection.mutable.Map[String,String](
            "chat_id" -> toChatId.toString,
            "from_chat_id" -> fromChatId.toString,
            "message_id" -> msgId.toString
        )

        Http(urlFor("forwardMessage")).params(p.toMap).asString
    }

    def sendChatAction(chatId: Int, action: String)
    {
        Http(urlFor("sendChatAction")).params(Map("chat_id" -> chatId.toString, "action" -> action)).asString
    }

    def sendPhoto(chatId: Int, imgPath: String, replyTo: Int = -1) = {
        val imgType = typeOf(imgPath)
        val isGif = imgType == "image/gif"
        val imgCmd = if (isGif) "Document" else "Photo"

        sendFile(imgCmd, chatId, imgPath, imgType, replyTo)
    }

    private def typeOf(filePath: String): String = {
        if (filePath.endsWith(".gif")) return "image/gif"
        if (filePath.endsWith(".png")) return "image/png"
        if (filePath.endsWith(".jpg")) return "image/jpeg"
        if (filePath.endsWith(".jpeg")) return "image/jpeg"
        return ""
    }

    private def sendFile(what: String, chatId: Int, filePath: String, fileType: String, replyTo: Int = -1) = {
        val apiCmd = "send" + what
        val fileParName = what.toLowerCase

        val p = collection.mutable.Map[String,String](
            "chat_id" -> chatId.toString
        )
        if (replyTo > 0) {
            p += ( "reply_to_message_id" -> replyTo.toString )
        }

        val fileName = if (filePath.lastIndexOf("/") < 0) filePath else filePath.substring(filePath.lastIndexOf("/") + 1)

        val is = new FileInputStream(filePath)
        val fileBytes = Stream.continually(is.read).takeWhile(-1 !=).map(_.toByte).toArray

        Http(urlFor(apiCmd))
            .params(p.toMap)
            .postMulti(MultiPart(fileParName, fileName, fileType, fileBytes))
            .asString
    }
}
