package me.alecosta.scatbo

/*
User
GroupChat
Message
PhotoSize
Audio
Document
Sticker
Video
Voice
Contact
Location
Update
InputFile
UserProfilePhotos
ReplyKeyboardMarkup
ReplyKeyboardHide
ForceReply
*/

// ----- User

class User(uid: Int, fn: String, ln: String, un: String)
{
    val id: Int           = uid
    val firstName: String = fn
    val lastName: String  = ln
    val username: String  = un

    def fullName = if (lastName == null || lastName.isEmpty) firstName else firstName + " " + lastName
}

object User
{
    def apply(userId: Int, firstName: String, lastName: String, username: String): User =
        new User(userId, firstName, lastName, username)
}

// ----- GroupChat

class GroupChat(gid: Int, t: String)
{
    val id: Int       = gid
    val title: String = t
}

object GroupChat
{
    def apply(groupChatId: Int, title: String): GroupChat =
        new GroupChat(groupChatId, title)
}

// ----- Message

class Message(mid: Int, f: User, d: Int, t: String, uChat: User, gChat: GroupChat)
{
    val id: Int              = mid
    val from: User           = f
    val date: Int            = d
    val userChat: User       = uChat
    val groupChat: GroupChat = gChat
    val text: String         = t

    def belongsToUser = userChat != null
    def belongsToGroup = groupChat != null

    def chatId = if (belongsToUser) userChat.id else if (belongsToGroup) groupChat.id else 0
    def chatName = if (belongsToUser) userChat.fullName else if (belongsToGroup) groupChat.title else ""

    def isCommand(cmd: String): Boolean =
    {
        if (text == null) return false

        val strCmd = "/" + cmd
        val botUser = Telegram.botUser

        if (text.equals(strCmd) || text.startsWith(strCmd + " ")) return true
        
        if (!botUser.isEmpty()) {
            val strCmdFull = strCmd + "@" + botUser
            if (text.equals(strCmdFull) || text.startsWith(strCmdFull + " ")) return true
        }
        
        return false
    }
}

object Message
{
    def apply(messageId: Int, from: User, date: Int, text: String, userChat: User, groupChat: GroupChat): Message =
        new Message(messageId, from, date, text, userChat, groupChat)
}

// ----- Others

class PhotoSize()
class Audio()
class Document()
class Sticker()
class Video()
class Voice()
class Contact()
class Location()
class Update()
class InputFile()
class UserProfilePhotos()
class ReplyKeyboardMarkup()
class ReplyKeyboardHide()
class ForceReply()
