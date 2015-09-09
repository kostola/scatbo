package me.alecosta.scatbo

import spray.json._

/*
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
    implicit object UserJsonFormat extends RootJsonFormat[User] {
        def write(u: User): JsObject = {
            val objMap = collection.mutable.Map(
                "id" -> JsNumber(u.id),
                "first_name" -> JsString(u.firstName)
            )

            if (u.lastName != null) objMap += ( "last_name" -> JsString(u.lastName) )
            if (u.username != null) objMap += ( "username" -> JsString(u.username) )

            JsObject(objMap.toMap)
        }

        def read(value: JsValue): User = {
            val fromObj = value.asJsObject()

            var id: Int = 0
            var firstName: String = ""
            var lastName: String = null
            var username: String = null

            try {
                id = fromObj.getFields("id")(0).convertTo[Int]
                try {
                    firstName = fromObj.getFields("first_name")(0).convertTo[String]
                } catch {
                    case e: Exception => {}
                }
                try {
                    lastName = fromObj.getFields("last_name")(0).convertTo[String]
                } catch {
                    case e: Exception => {}
                }
                try {
                    username = fromObj.getFields("username")(0).convertTo[String]
                } catch {
                    case e: Exception => {}
                }
            } catch {
                case e: Exception => {}
            }

            User(id, firstName, lastName, username)
        }
    }

    implicit object GroupChatJsonFormat extends RootJsonFormat[GroupChat] {
        def write(g: GroupChat): JsObject =
            JsObject(Map(
                "id" -> JsNumber(g.id),
                "title" -> JsString(g.title)
            ))

        def read(value: JsValue): GroupChat = {
            val fromObj = value.asJsObject()

            var id: Int = 0
            var title: String = ""

            try {
                id = fromObj.getFields("id")(0).convertTo[Int]
                try {
                    title = fromObj.getFields("title")(0).convertTo[String]
                } catch {
                    case e: Exception => {}
                }
            } catch {
                case e: Exception => {}
            }

            GroupChat(id, title)
        }
    }

    implicit object PhotoSizeJsonFormat extends RootJsonFormat[PhotoSize] {
        def write(p: PhotoSize): JsObject = {
            val objMap = collection.mutable.Map(
                "file_id" -> JsString(p.fileId),
                "width" -> JsNumber(p.width),
                "height" -> JsNumber(p.height)
            )

            if (p.fileSize > 0) objMap += ( "file_size" -> JsNumber(p.fileSize) )

            JsObject(objMap.toMap)
        }

        def read(value: JsValue): PhotoSize = {
            val fromObj = value.asJsObject()

            var fileId: String = ""
            var width: Int = -1
            var height: Int = -1
            var fileSize: Int = -1

            try {
                fileId = fromObj.getFields("file_id")(0).convertTo[String]
                try {
                    width = fromObj.getFields("width")(0).convertTo[Int]
                } catch {
                    case e: Exception => {}
                }
                try {
                    height = fromObj.getFields("height")(0).convertTo[Int]
                } catch {
                    case e: Exception => {}
                }
                try {
                    fileSize = fromObj.getFields("file_size")(0).convertTo[Int]
                } catch {
                    case e: Exception => {}
                }
            } catch {
                case e: Exception => {}
            }

            PhotoSize(fileId, width, height, fileSize)
        }
    }

    implicit object MessageJsonFormat extends RootJsonFormat[Message] {
        def write(m: Message): JsObject = {
            val objMap = collection.mutable.Map(
                "message_id" -> JsNumber(m.id),
                "date" -> JsNumber(m.date),
                "from" -> m.from.toJson
            )

            if (m.userChat != null) objMap += ( "chat " -> m.userChat.toJson )
            else if (m.groupChat != null) objMap += ( "chat" -> m.groupChat.toJson )

            if (m.text != null) objMap += ( "text" -> JsString(m.text) )

            JsObject(objMap.toMap)
        }

        def read(value: JsValue): Message = {
            val fromObj = value.asJsObject()

            var id: Int = 0
            var date: Int = 0
            var text: String = null
            var from: User = null
            var uChat: User = null
            var gChat: GroupChat = null
            var photo: List[PhotoSize] = null

            try {
                id = fromObj.getFields("message_id")(0).convertTo[Int]
                try {
                    date = fromObj.getFields("date")(0).convertTo[Int]
                } catch {
                    case e: Exception => {}
                }
                try {
                    from = fromObj.getFields("from")(0).convertTo[User]
                } catch {
                    case e: Exception => {}
                }
                try {
                    text = fromObj.getFields("text")(0).convertTo[String]
                } catch {
                    case e: Exception => {}
                }

                val chatId = fromObj.getFields("chat")(0).asJsObject.getFields("id")(0).convertTo[Int]

                if (chatId < 0) {
                    gChat = fromObj.getFields("chat")(0).convertTo[GroupChat]
                } else {
                    uChat = fromObj.getFields("chat")(0).convertTo[User]
                }

                try {
                    photo = fromObj.getFields("photo")(0).convertTo[JsArray].elements.toList.map(_.convertTo[PhotoSize])
                } catch {
                    case e: Exception => {}
                }
            } catch {
                case e: Exception => {}
            }

            Message(id, from, date, uChat, gChat, text = text, photo = photo)
        }
    }
}
