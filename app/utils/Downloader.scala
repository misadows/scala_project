package utils

import net.ruippeixotog.scalascraper.browser.Browser
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL.Parse._
import net.ruippeixotog.scalascraper.scraper.ContentExtractors._
import net.ruippeixotog.scalascraper.util.ProxyUtils
import org.jsoup.nodes.Element
import akka.actor.{Actor, ActorRef, Props, ActorSystem}
import scala.concurrent.Await
import scala.concurrent.Future
import akka.util.Timeout
import scala.concurrent.duration._
import akka.pattern.ask
import models.{ReqData, Req}
import scala.util.Random

case class DownloadResults(criteria: Array[String])

case class ReturnResults(res: List[String])

/** Actor responsible for creating new actors and telling each of them to download one result page */
class StringConcatenatingActor(id: Long) extends Actor {
  var finishedDownloads: Int = 0
  var result = List[String]()
  private var boss: Option[ActorRef] = None

  def receive = {
    case DownloadResults(criteria: Array[String]) => {
      boss = Some(sender)

      /** Set pages to be downloaded here. */
      for (i <- 7 to 100) {
        context.actorOf(Props[PasswordDownloadingActor]) ! DownloadPage(i, criteria)
      }
    }

    /** Saves the returned strings to database */
    case ReturnResults(res: List[String]) => {
      this.result = this.result ::: res
      finishedDownloads += 1

      /** Set number of pages to be downloaded here. */
      if (finishedDownloads > 15) {
        for (i <- this.result) ReqData.insert(ReqData(0, i, id))
        println("Sending result")
        boss.map(_ ! "WUNGIEL PRZYWIEŹLIM")
      }
    }
    case _ => println("Error: message not recognized XDXDXDXD")
  }
}

case class DownloadPage(page: Int, criteria: Array[String])

class PasswordDownloadingActor extends Actor {
  val proxyList = List(
    ("106.184.7.45", 80),
    ("183.129.210.18", 8888),
    ("123.195.148.199", 80),
    ("121.42.142.104", 1080),
    ("206.246.82.2", 443),
    ("195.154.231.43", 3128),
    ("134.119.20.197", 80),
    ("189.219.202.204", 10000),
    ("173.44.178.241", 1080),
    ("220.226.188.171", 80)
  )
  var result: List[String] = List[String]()

  def receive = {
    case DownloadPage(page: Int, criteria: Array[String]) =>

      //set a random proxy
      val (proxy, port) = proxyList(Random.nextInt(10))
      ProxyUtils.setProxy(proxy, port)

      println("Trying to resolve " + proxy + ":" + port + "...")

      //download the page
      val browser = new Browser
      val doc = browser.get(this.getUrl(page, criteria))
      println("Success")

      //get specific content from the page
      val items: List[Element] = doc >> elementList(".blob-code")
      val newList: List[Seq[String]] = items.map(x => x >> texts("td"))

      //filter the content in order to throw away the trash
      val resultList = newList.map(x => x.head.filter(_ != ""))
      for (i <- resultList if check(i))
        this.result = this.result :+ i

      //return downloaded string to the sender
      sender ! ReturnResults(result)
    case _ => println("Error: message not recognized.")
  }

  def check(i: String): Boolean = {
    var flag: Boolean = true

    //result is probably trash if:
    if (!((i.dropRight(1) contains "=") || (i.dropRight(1) contains ":"))) flag = false;
    if ((i.count(_ == '\'') < 2) && (i.count(_ == '"') < 2)) flag = false;
    if (i.length < 12) flag = false
    flag;
  }

  //* Get the search criteria snippet*/
  def getSnippet(criteria: Array[String]): String = {
    var snippet: String = ""
    for (i <- criteria) {
      snippet += i + "+"
    }
    snippet.dropRight(1)
  }

  //* Get the final url to the github page.*/
  def getUrl(page: Int, criteria: Array[String]): String = {
    val result: String = "https://github.com/search?p=" + page.toString + "&q=" + this.getSnippet(criteria) + "&type=Code"
    result
  }
}

class Downloader(id: Long) {
  def runActors(criteria: Array[String]) {
    val system = ActorSystem("System")
    val actor = system.actorOf(Props(new StringConcatenatingActor(id)))
    implicit val timeout = Timeout(5 minutes)
    val future = actor ? DownloadResults(criteria)
    val result = Await.result(future, timeout.duration).asInstanceOf[String]
    //println("Got result")
    system.shutdown
  }
}