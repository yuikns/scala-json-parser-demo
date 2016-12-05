import com.argcv.valhalla.string.json.JsonHelper._
import com.argcv.valhalla.utils.Awakable
import org.slf4j.Logger

import scala.collection.mutable.{Map => MMap}
import scala.util.parsing.json.JSON

/**
  * @author yu
  */
object Launcher extends App with Awakable {
  def useJSON(url:String, logger: Logger): Unit = {
    val timeStart = System.currentTimeMillis()
    val str1: String = scala.io.Source.fromURL(url).mkString
    val timeGetData = System.currentTimeMillis()
    logger.info(s"[JSON] time cost in reading .. ${timeGetData - timeStart}")
    val json: Option[Any] = JSON.parseFull(str1)
    val map: Map[String, Any] = json.get.asInstanceOf[Map[String, Any]]
    val list: List[Any] = map("res").asInstanceOf[List[Any]]
    val timeParseData = System.currentTimeMillis()
    logger.info(s"[JSON] time parse data ... ${timeParseData - timeGetData}")
    logger.info(s"[JSON] finished , all time cost : ${System.currentTimeMillis() - timeStart}")
  }

  /**
    * based on net.liftweb
    * @param logger logger
    */
  def useJsonHelper(url:String, logger: Logger): Unit = {
    case class ConfAuthorsResult(name:String)
    case class ConfPaperResult(pid:String, title:String, `abstract`:String, authors:List[ConfAuthorsResult], trackname:String, keywords:String, liked:Boolean, num_liked:Int, pub_number:Int, num_viewed:Int)
    case class PaperResult(status:Boolean, res:List[ConfPaperResult])

    val timeStart = System.currentTimeMillis()
    val dataStream: String = scala.io.Source.fromURL(url).mkString
    val timeGetData = System.currentTimeMillis()
    logger.info(s"[JsonHelper] time cost in reading .. ${timeGetData - timeStart}")
    dataStream.parseJsonToClass[PaperResult] match {
      case Some(pr) =>
        logger.info(s"parsed, status: ${pr.status}")
        if(pr.status) {
          logger.info(s"count ${pr.res.size}")
          if(pr.res.nonEmpty) {
            logger.info(pr.res.head.toJson(false))
          }
        }
      case None =>
        logger.warn("parse failed")
    }

    val timeParseData = System.currentTimeMillis()
    logger.info(s"[JsonHelper] time parse data ... ${timeParseData - timeGetData}")
    logger.info(s"[JsonHelper] finished , all time cost : ${System.currentTimeMillis() - timeStart}")
  }


  val JSON_URL = "http://api.aminer.org/api/pschedule/conference/CIKM%202016/papers"
  useJSON(JSON_URL, logger)
  useJsonHelper(JSON_URL, logger)
}
