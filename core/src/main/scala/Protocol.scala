package me.alecosta.scatbo

import spray.json._

import scala.RuntimeException

/*
  Update example:

  {
    "update_id": 123456789,
    "message": {
      "text": "/start",
      "chat": {
        "id": 12345678,
        "first_name": "John",
        "last_name": "Doe",
        "username": "johndoe"
      },
      "message_id": 123,
      "date": 1440270162,
      "from": {
        "id": 12345678,
        "first_name": "John",
        "last_name": "Doe",
        "username": "johndoe"
      }
    }
  }
*/

object TelegramProtocol extends DefaultJsonProtocol
{
    def readMandatoryField[T](fromObj: JsObject, field: String)(implicit reader: JsonReader[T]): T = {
        try {
            fromObj.getFields(field)(0).convertTo[T]
        } catch {
            case e: Exception => { throw new RuntimeException("Error while parsing \"" + field + "\"", e) }
        }
    }

    def readOptionalField[T](fromObj: JsObject, field: String)(implicit reader: JsonReader[T]): Option[T] = {
        try {
            Some[T](fromObj.getFields(field)(0).convertTo[T])
        } catch {
            case e: Exception => { None }
        }
    }

    def writeOptionalField[T](objMap: collection.mutable.Map[String, JsValue], field: String, attribute: Option[T])(implicit writer: JsonWriter[T]): Unit = {
        attribute match {
            case Some(value: T) => objMap += ( field -> value.toJson )
            case _ =>
        }
    }

    implicit object UserJsonFormat extends RootJsonFormat[User] {
        def write(u: User): JsObject = {
            val objMap = collection.mutable.Map[String,JsValue](
                "id" -> JsNumber(u.id),
                "first_name" -> JsString(u.firstName)
            )

            writeOptionalField(objMap, "last_name", u.lastName)
            writeOptionalField(objMap, "username", u.username)

            JsObject(objMap.toMap)
        }

        def read(value: JsValue): User = {
            val fromObj = value.asJsObject()

            User(
                id = readMandatoryField[Int](fromObj, "id"),
                firstName = readMandatoryField[String](fromObj, "first_name"),
                lastName = readOptionalField[String](fromObj, "last_name"),
                username = readOptionalField[String](fromObj, "username")
            )
        }
    }

    implicit object GroupChatJsonFormat extends RootJsonFormat[GroupChat] {
        def write(g: GroupChat): JsObject =
            JsObject(Map(
                "id" -> JsNumber(g.id),
                "title" -> JsString(g.title)
            )
        )

        def read(value: JsValue): GroupChat = {
            val fromObj = value.asJsObject()

            GroupChat(
                id = readMandatoryField[Int](fromObj, "id"),
                title = readMandatoryField[String](fromObj, "title")
            )
        }
    }

    implicit object PhotoSizeJsonFormat extends RootJsonFormat[PhotoSize] {
        def write(p: PhotoSize): JsObject = {
            val objMap = collection.mutable.Map[String,JsValue](
                "file_id" -> JsString(p.id),
                "width" -> JsNumber(p.width),
                "height" -> JsNumber(p.height)
            )

            writeOptionalField(objMap, "file_size", p.fileSize)

            JsObject(objMap.toMap)
        }

        def read(value: JsValue): PhotoSize = {
            val fromObj = value.asJsObject()

            PhotoSize(
                id = readMandatoryField[String](fromObj, "file_id"),
                width = readMandatoryField[Int](fromObj, "width"),
                height = readMandatoryField[Int](fromObj, "height"),
                fileSize = readOptionalField[Int](fromObj, "file_size")
            )
        }
    }

    implicit object AudioJsonFormat extends RootJsonFormat[Audio] {
        def write(m: Audio): JsObject = {
            val objMap = collection.mutable.Map[String,JsValue](
                "file_id" -> JsString(m.id),
                "duration" -> JsNumber(m.duration)
            )

            writeOptionalField[String](objMap, "performer", m.performer)
            writeOptionalField[String](objMap, "title", m.title)
            writeOptionalField[String](objMap, "mime_type", m.mimeType)
            writeOptionalField[Int](objMap, "file_size", m.fileSize)

            JsObject(objMap.toMap)
        }

        def read(value: JsValue): Audio = {
            val fromObj = value.asJsObject()

            Audio(
                id = readMandatoryField[String](fromObj, "file_id"),
                duration = readMandatoryField[Int](fromObj, "duration"),
                performer = readOptionalField[String](fromObj, "performer"),
                title = readOptionalField[String](fromObj, "title"),
                mimeType = readOptionalField[String](fromObj, "mime_type"),
                fileSize = readOptionalField[Int](fromObj, "file_size")
            )
        }
    }

    implicit object DocumentJsonFormat extends RootJsonFormat[Document] {
        def write(m: Document): JsObject = {
            val objMap = collection.mutable.Map[String,JsValue](
                "file_id" -> JsString(m.id)
            )

            writeOptionalField(objMap, "thumb", m.thumb)
            writeOptionalField(objMap, "file_name", m.fileName)
            writeOptionalField(objMap, "mime_type", m.mimeType)
            writeOptionalField(objMap, "file_size", m.fileSize)

            JsObject(objMap.toMap)
        }

        def read(value: JsValue): Document = {
            val fromObj = value.asJsObject()
            Document(
                id = readMandatoryField[String](fromObj, "file_id"),
                thumb = readOptionalField[PhotoSize](fromObj, "thumb"),
                fileName = readOptionalField[String](fromObj, "file_name"),
                mimeType = readOptionalField[String](fromObj, "mime_tipe"),
                fileSize = readOptionalField[Int](fromObj, "file_size")
            )
        }
    }

    implicit object StickerJsonFormat extends RootJsonFormat[Sticker] {
        def write(m: Sticker): JsObject = {
            val objMap = collection.mutable.Map[String,JsValue](
                "file_id" -> JsString(m.id),
                "width" -> JsNumber(m.width),
                "height" -> JsNumber(m.height)
            )

            writeOptionalField(objMap, "thumb", m.thumb)
            writeOptionalField(objMap, "file_size", m.fileSize)

            JsObject(objMap.toMap)
        }

        def read(value: JsValue): Sticker = {
            val fromObj = value.asJsObject()

            Sticker(
                id = readMandatoryField[String](fromObj, "file_id"),
                width = readMandatoryField[Int](fromObj, "width"),
                height = readMandatoryField[Int](fromObj, "height"),
                thumb = readOptionalField[PhotoSize](fromObj, "thumb"),
                fileSize = readOptionalField[Int](fromObj, "file_size")
            )
        }
    }

    implicit object VideoJsonFormat extends RootJsonFormat[Video] {
        def write(m: Video): JsObject = {
            val objMap = collection.mutable.Map[String,JsValue](
                "file_id" -> JsString(m.id),
                "width" -> JsNumber(m.width),
                "height" -> JsNumber(m.height),
                "duration" -> JsNumber(m.duration)
            )

            writeOptionalField(objMap, "thumb", m.thumb)
            writeOptionalField(objMap, "mime_type", m.mimeType)
            writeOptionalField(objMap, "file_size", m.fileSize)

            JsObject(objMap.toMap)
        }

        def read(value: JsValue): Video = {
            val fromObj = value.asJsObject()

            Video(
                id = readMandatoryField[String](fromObj, "file_id"),
                width = readMandatoryField[Int](fromObj, "width"),
                height = readMandatoryField[Int](fromObj, "height"),
                duration = readMandatoryField[Int](fromObj, "duration"),
                thumb = readOptionalField[PhotoSize](fromObj, "thumb"),
                mimeType = readOptionalField[String](fromObj, "mime_tipe"),
                fileSize = readOptionalField[Int](fromObj, "file_size")
            )
        }
    }

    implicit object VoiceJsonFormat extends RootJsonFormat[Voice] {
        def write(m: Voice): JsObject = {
            val objMap = collection.mutable.Map[String,JsValue](
                "file_id" -> JsString(m.id),
                "duration" -> JsNumber(m.duration)
            )

            writeOptionalField(objMap, "mime_type", m.mimeType)
            writeOptionalField(objMap, "file_size", m.fileSize)

            JsObject(objMap.toMap)
        }

        def read(value: JsValue): Voice = {
            val fromObj = value.asJsObject()

            Voice(
                id = readMandatoryField[String](fromObj, "file_id"),
                duration = readMandatoryField[Int](fromObj, "duration"),
                mimeType = readOptionalField[String](fromObj, "mime_tipe"),
                fileSize = readOptionalField[Int](fromObj, "file_size")
            )
        }
    }

    implicit object ContactJsonFormat extends RootJsonFormat[Contact] {
        def write(m: Contact): JsObject = {
            val objMap = collection.mutable.Map[String,JsValue](
                "phone_number" -> JsString(m.phoneNumber),
                "first_name" -> JsString(m.firstName)
            )

            writeOptionalField(objMap, "last_name", m.lastName)
            writeOptionalField(objMap, "user_id", m.userId)

            JsObject(objMap.toMap)
        }

        def read(value: JsValue): Contact = {
            val fromObj = value.asJsObject()

            Contact(
                phoneNumber = readMandatoryField[String](fromObj, "phone_number"),
                firstName = readMandatoryField[String](fromObj, "first_name"),
                lastName = readOptionalField[String](fromObj, "last_name"),
                userId = readOptionalField[Int](fromObj, "user_id")
            )
        }
    }

    implicit object LocationJsonFormat extends RootJsonFormat[Location] {
        def write(m: Location): JsObject = {
            val objMap = collection.mutable.Map[String,JsValue](
                "longitude" -> JsNumber(m.longitude),
                "latitude" -> JsNumber(m.latitude)
            )
            JsObject(objMap.toMap)
        }

        def read(value: JsValue): Location = {
            val fromObj = value.asJsObject()

            Location(
                longitude = readMandatoryField[Double](fromObj, "longitude"),
                latitude = readMandatoryField[Double](fromObj, "latitude")
            )
        }
    }

    implicit object UserProfilePhotosJsonFormat extends RootJsonFormat[UserProfilePhotos] {
        def write(m: UserProfilePhotos): JsObject = {
            val objMap = collection.mutable.Map[String,JsValue](
                "total_count" -> JsNumber(m.totalCount)
                // TODO: implement photos
            )
            JsObject(objMap.toMap)
        }

        def read(value: JsValue): UserProfilePhotos = {
            val fromObj = value.asJsObject()

            UserProfilePhotos(
                totalCount = readMandatoryField[Int](fromObj, "total_count"),
                photos = List[PhotoSize]() // TODO: implement photos
            )
        }
    }

    implicit object FileJsonFormat extends RootJsonFormat[File] {
        def write(m: File): JsObject = {
            val objMap = collection.mutable.Map[String,JsValue](
                "file_id" -> JsNumber(m.id)
            )

            writeOptionalField(objMap, "file_size", m.fileSize)
            writeOptionalField(objMap, "file_path", m.filePath)

            JsObject(objMap.toMap)
        }

        def read(value: JsValue): File = {
            val fromObj = value.asJsObject()

            File(
                id = readMandatoryField[String](fromObj, "file_id"),
                fileSize = readOptionalField[Int](fromObj, "file_size"),
                filePath = readOptionalField[String](fromObj, "file_path")
            )
        }
    }

    implicit object ReplyKeyboardMarkupJsonFormat extends RootJsonFormat[ReplyKeyboardMarkup] {
        def write(m: ReplyKeyboardMarkup): JsObject = {
            val objMap = collection.mutable.Map[String,JsValue](
                "keyboard" -> JsString("") // TODO: implement keyboard
            )

            writeOptionalField(objMap, "resize_keyboard", m.resize)
            writeOptionalField(objMap, "one_time_keyboard", m.oneTime)
            writeOptionalField(objMap, "selective", m.selective)

            JsObject(objMap.toMap)
        }

        def read(value: JsValue): ReplyKeyboardMarkup = {
            val fromObj = value.asJsObject()

            ReplyKeyboardMarkup(
                keyboard = List[List[String]](), // TODO: implement keyboard
                resize = readOptionalField[Boolean](fromObj, "resize_keyboard"),
                oneTime = readOptionalField[Boolean](fromObj, "one_time_keyboard"),
                selective = readOptionalField[Boolean](fromObj, "selective")
            )
        }
    }

    implicit object ReplyKeyboardHideJsonFormat extends RootJsonFormat[ReplyKeyboardHide] {
        def write(m: ReplyKeyboardHide): JsObject = {
            val objMap = collection.mutable.Map[String,JsValue](
                "hide_keyboard" -> JsBoolean(true)
            )

            writeOptionalField(objMap, "selective", m.selective)

            JsObject(objMap.toMap)
        }

        def read(value: JsValue): ReplyKeyboardHide = {
            val fromObj = value.asJsObject()

            ReplyKeyboardHide(
                selective = readOptionalField[Boolean](fromObj, "selective")
            )
        }
    }

    implicit object ForceReplyJsonFormat extends RootJsonFormat[ForceReply] {
        def write(m: ForceReply): JsObject = {
            val objMap = collection.mutable.Map[String,JsValue](
                "force_reply" -> JsBoolean(true)
            )

            writeOptionalField(objMap, "selective", m.selective)

            JsObject(objMap.toMap)
        }

        def read(value: JsValue): ForceReply = {
            val fromObj = value.asJsObject()

            ForceReply(
                selective = readOptionalField[Boolean](fromObj, "selective")
            )
        }
    }

    implicit object MessageJsonFormat extends RootJsonFormat[Message] {
        def write(m: Message): JsObject = {
            val objMap = collection.mutable.Map[String,JsValue](
                "message_id" -> JsNumber(m.id),
                "date" -> JsNumber(m.date),
                "from" -> m.from.toJson
            )

            writeOptionalField(objMap, "chat", m.userChat)
            writeOptionalField(objMap, "chat", m.groupChat)
            writeOptionalField(objMap, "forward_from", m.forwardFrom)
            writeOptionalField(objMap, "forward_date", m.forwardDate)
            writeOptionalField(objMap, "reply_to_message", m.replyToMessage)
            writeOptionalField(objMap, "text", m.text)
            writeOptionalField(objMap, "audio", m.audio)
            writeOptionalField(objMap, "document", m.document)
            writeOptionalField(objMap, "photo", m.photo)
            writeOptionalField(objMap, "sticker", m.sticker)
            writeOptionalField(objMap, "video", m.video)
            writeOptionalField(objMap, "voice", m.voice)
            writeOptionalField(objMap, "caption", m.caption)
            writeOptionalField(objMap, "contact", m.contact)
            writeOptionalField(objMap, "location", m.location)
            writeOptionalField(objMap, "new_chat_participant", m.newChatParticipant)
            writeOptionalField(objMap, "left_chat_participant", m.leftChatParticipant)
            writeOptionalField(objMap, "new_chat_title", m.newChatTitle)
            writeOptionalField(objMap, "new_chat_photo", m.newChatPhoto)
            writeOptionalField(objMap, "delete_chat_photo", m.deleteChatPhoto)
            writeOptionalField(objMap, "group_chat_created", m.groupChatCreated)

            JsObject(objMap.toMap)
        }

        def read(value: JsValue): Message = {
            val fromObj = value.asJsObject()

            Message(
                id = readMandatoryField[Int](fromObj, "message_id"),
                from = readMandatoryField[User](fromObj, "from"),
                date = readMandatoryField[Int](fromObj, "date"),
                userChat = readOptionalField[User](fromObj, "chat"),
                groupChat = readOptionalField[GroupChat](fromObj, "chat"),
                forwardFrom = readOptionalField[User](fromObj, "forward_from"),
                forwardDate = readOptionalField[Int](fromObj, "forward_date"),
                replyToMessage = readOptionalField[Message](fromObj, "reply_to_message"),
                text = readOptionalField[String](fromObj, "text"),
                audio = readOptionalField[Audio](fromObj, "audio"),
                document = readOptionalField[Document](fromObj, "document"),
                photo = readOptionalField[List[PhotoSize]](fromObj, "photo"),
                sticker = readOptionalField[Sticker](fromObj, "sticker"),
                video = readOptionalField[Video](fromObj, "video"),
                caption = readOptionalField[String](fromObj, "caption"),
                contact = readOptionalField[Contact](fromObj, "contact"),
                location = readOptionalField[Location](fromObj, "location"),
                newChatParticipant = readOptionalField[User](fromObj, "new_chat_participant"),
                leftChatParticipant = readOptionalField[User](fromObj, "left_chat_participant"),
                newChatTitle = readOptionalField[String](fromObj, "new_chat_title"),
                newChatPhoto = readOptionalField[List[PhotoSize]](fromObj, "new_chat_photo"),
                deleteChatPhoto = readOptionalField[Boolean](fromObj, "delete_chat_photo"),
                groupChatCreated = readOptionalField[Boolean](fromObj, "group_chat_photo")
            )
        }

        implicit object UpdateJsonFormat extends RootJsonFormat[Update] {
            def write(m: Update): JsObject = {
                val objMap = collection.mutable.Map[String,JsValue](
                "update_id" -> JsNumber(m.id)
            )

                writeOptionalField(objMap, "message", m.message)

                JsObject(objMap.toMap)
            }

            def read(value: JsValue): Update = {
                val fromObj = value.asJsObject()

                Update(
                    id = readMandatoryField[Int](fromObj, "update_id"),
                    message = readOptionalField[Message](fromObj, "message")
                )
            }
        }
    }
}
