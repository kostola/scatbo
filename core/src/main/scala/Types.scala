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

case class Message( id: Int,
               from: User,
               date: Int,
               userChat: Option[User] = None,
               groupChat: Option[GroupChat] = None,
               forwardFrom: Option[User] = None,
               forwardDate: Option[Int] = None,
               replyToMessage: Option[Message] = None,
               text: Option[String] = None,
               audio: Option[Audio] = None,
               document: Option[Document] = None,
               photo: Option[List[PhotoSize]] = None,
               sticker: Option[Sticker] = None,
               video: Option[Video] = None,
               voice: Option[Voice] = None,
               caption: Option[String] = None,
               contact: Option[Contact] = None,
               location: Option[Location] = None,
               newChatParticipant: Option[User] = None,
               leftChatParticipant: Option[User] = None,
               newChatTitle: Option[String] = None,
               newChatPhoto: Option[List[PhotoSize]] = None,
               deleteChatPhoto: Option[Boolean] = None,
               groupChatCreated: Option[Boolean] = None
                     )
{
    def belongsToUser = userChat != None
    def belongsToGroup = groupChat != None

    def chatId: Int = userChat match {
        case Some(user: User) => user.id
        case None => groupChat match {
            case Some(group: GroupChat) => group.id
            case None => 0
        }
    }

    def chatName: String = userChat match {
        case Some(user: User) => user.fullName
        case None => groupChat match {
            case Some(group: GroupChat) => group.title
            case None => ""
        }
    }

    def isCommand(cmd: String): Boolean = text match {
        case None => false
        case Some(txt: String) => {
            val strCmd = "/" + cmd
            val botUser = Telegram.botUser

            if (txt.equals(strCmd) || txt.startsWith(strCmd + " ")) {
                true
            } else if (!botUser.isEmpty()) {
                val strCmdFull = strCmd + "@" + botUser
                txt.equals(strCmdFull) || txt.startsWith(strCmdFull + " ")
            } else {
                false
            }
        }
    }
}

case class User(id: Int, firstName: String, lastName: Option[String] = None, username: Option[String] = None)
{
    def fullName: String = firstName + (lastName match {
        case Some(value: String) => if (value.isEmpty) "" else " " + value
        case None => ""
    })
}

case class GroupChat(id: Int, title: String)

case class PhotoSize(id: String, width: Int, height: Int, fileSize: Option[Int] = None)

case class Audio(id: String, duration: Int, performer: Option[String] = None, title: Option[String] = None, mimeType: Option[String] = None, fileSize: Option[Int] = None)

case class Document(id: String, thumb: Option[PhotoSize] = None, fileName: Option[String] = None, mimeType: Option[String] = None, fileSize: Option[Int] = None)

case class Sticker(id: String, width: Int, height: Int, thumb: Option[PhotoSize] = None, fileSize: Option[Int] = None)

case class Video(id: String, width: Int, height: Int, duration: Int, thumb: Option[PhotoSize] = None, mimeType: Option[String] = None, fileSize: Option[Int] = None)

case class Voice(id: String, duration: Int, mimeType: Option[String] = None, fileSize: Option[Int] = None)

case class Contact(phoneNumber: String, firstName: String, lastName: Option[String] = None, userId: Option[Int] = None)

case class Location(longitude: Double, latitude: Double)

case class Update(id: Int, message: Option[Message] = None)

case class UserProfilePhotos(totalCount: Int, photos: List[PhotoSize])

case class File(id: String, fileSize: Option[Int] = None, filePath: Option[String] = None)
{
    def fileUrl(botToken: String) = filePath match {
        case Some(path) => {
            Some[String]("https://api.telegram.org/file/bot" + botToken + "/" + path)
        }
        case None => None
    }
}

case class ReplyKeyboardMarkup(keyboard: List[List[String]], resize: Option[Boolean] = None, oneTime: Option[Boolean] = None, selective: Option[Boolean] = None)

case class ReplyKeyboardHide(selective: Option[Boolean] = None)

case class ForceReply(selective: Option[Boolean] = None)
