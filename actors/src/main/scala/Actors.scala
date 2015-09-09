package me.alecosta.scatbo

import akka.actor._
import scala.concurrent.duration._
import scalaj.http._
import spray.json._

import TelegramProtocol._

// ----- messages

case class StartCheckingForUpdates()
case class CheckForUpdates()
case class SubscribeForCommand(cmd: String)
case class SubscribeActorForCommand(a: ActorRef, cmd: String)
case class MessageReceived(m: Message)

// ----- actors

class UpdatesActor(delay: Int) extends Actor
{
    var offset = 0
    private val subscriptions = collection.mutable.Map[String, List[ActorRef]]()

    def subscribe(a: ActorRef, c: String) {
        if (! subscriptions.contains(c)) {
            subscriptions += ( c -> List(a) )
        } else {
            if (! subscriptions(c).contains(a)) {
                subscriptions(c) = subscriptions(c) ++ List(a)
            }
        }
    }

    def receive = {
        case StartCheckingForUpdates => {
            import context.dispatcher
            context.system.scheduler.schedule(0 seconds, delay seconds, self, CheckForUpdates)
        }
        case CheckForUpdates => {
            try {
                val rspUpdate = Telegram.getUpdates(offset)

                if (rspUpdate.is2xx) {
                    val astUpdate = JsonParser(rspUpdate.body).asJsObject

                    val isValid: Boolean = astUpdate.getFields("ok")(0).convertTo[Boolean]
                    if (isValid) {
                        val result: Vector[JsValue] = astUpdate.getFields("result")(0).convertTo[JsArray].elements
                        for (i <- 0 until result.length) {
                            val update: JsObject = result(i).asJsObject
                            val updateId: Integer = update.getFields("update_id")(0).convertTo[Double].toInt
                            offset = updateId + 1
                            val message: JsObject = update.getFields("message")(0).asJsObject
                            try {
                                parseMessage(message)
                            } catch {
                                case e: Exception => {
                                    Logger.log(message.prettyPrint)
                                    e.printStackTrace()
                                }
                            }
                        }
                    }
                } else {
                    Logger.log("Error while checking for updates")
                    Logger.log(rspUpdate.body)
                }
            } catch {
                case e: Exception => {
                    Logger.log("Exception while checking for updates")
                    e.printStackTrace()
                }
            }
        }
        case SubscribeForCommand(c) => {
            Logger.log("received subscription for " + c)
            subscribe(sender, c)
        }
        case SubscribeActorForCommand(a, c) => {
            Logger.log("received subscription for actor for " + c)
            subscribe(a, c)
        }
    }

    private def parseMessage(message: JsObject): Unit =
    {
        val msg = message.convertTo[Message]

        subscriptions.foreach {
            case (command, actors) => {
                if (msg.isCommand(command)) {
                    actors.foreach {
                        case actor => {
                            actor ! MessageReceived(msg)
                        }
                    }
                }
            }
        }
    }
}

class MessageActor extends Actor
{
    def receive = {
        case MessageReceived(msg) => {
            Logger.log("Received message from " + msg.from.firstName + " " + msg.from.lastName)

            if (msg.belongsToUser) {
                Logger.log("Message belongs to user " + msg.userChat.firstName + " " + msg.userChat.lastName)
            } else if (msg.belongsToGroup) {
                Logger.log("Message belongs to group " + msg.groupChat.title)
            } else {
                Logger.log("Message belongs to nobody")
            }

            Logger.log(msg.text)
        }
    }
}
