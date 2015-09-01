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
    def apply(userId: Int, firstName: String, lastName: String = null, username: String = null): User =
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

class Message( mid: Int,
               f: User,
               d: Int,
               uChat: User,
               gChat: GroupChat,
               fwdFrom: User,
               repToMsg: Message,
               txt: String,
               au: Audio,
               doc: Document,
               ph: List[PhotoSize],
               st: Sticker,
               vi: Video,
               vo: Voice,
               ca: String,
               co: Contact,
               loc: Location,
               newChPrt: User,
               leftChPrt: User,
               newChTit: String,
               newChPh: List[PhotoSize],
               delChPh: Boolean,
               gChCrtd: Boolean
             )
{
    val id: Int = mid
    val from: User = f
    val date: Int = d
    val userChat: User = uChat 
    val groupChat: GroupChat = gChat
    val forwardFrom: User = fwdFrom
    val replyToMessage: Message = repToMsg
    val text: String = txt
    val audio: Audio = au
    val document: Document = doc
    val photo: List[PhotoSize] = ph
    val sticker: Sticker = st
    val video: Video = vi
    val voice: Voice = vo
    val caption: String = ca
    val contact: Contact = co
    val location: Location = loc
    val newChatParticipant: User = newChPrt
    val leftChatParticipant: User = leftChPrt
    val newChatTitle: String = newChTit
    val newChatPhoto: List[PhotoSize] = newChPh
    val deleteChatPhoto: Boolean = delChPh
    val groupChatCreated: Boolean = gChCrtd

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
    def apply( messageId: Int,
               from: User,
               date: Int,
               userChat: User = null,
               groupChat: GroupChat = null,
               forwardFrom: User = null,
               replyToMessage: Message = null,
               text: String = null,
               audio: Audio = null,
               document: Document = null,
               photo: List[PhotoSize] = null,
               sticker: Sticker = null,
               video: Video = null,
               voice: Voice = null,
               caption: String = null,
               contact: Contact = null,
               location: Location = null,
               newChatParticipant: User = null,
               leftChatParticipant: User = null,
               newChatTitle: String = null,
               newChatPhoto: List[PhotoSize] = null,
               deleteChatPhoto: Boolean = false,
               groupChatCreated: Boolean = false
             ): Message =
        new Message(
               messageId,
               from,
               date,
               userChat,
               groupChat,
               forwardFrom,
               replyToMessage,
               text,
               audio,
               document,
               photo,
               sticker,
               video,
               voice,
               caption,
               contact,
               location,
               newChatParticipant,
               leftChatParticipant,
               newChatTitle,
               newChatPhoto,
               deleteChatPhoto,
               groupChatCreated
             )
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
