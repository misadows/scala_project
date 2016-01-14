package utils

import net.ruippeixotog.scalascraper.browser.Browser
import net.ruippeixotog.scalascraper.browser.Browser
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL.Parse._
import net.ruippeixotog.scalascraper.scraper.ContentExtractors._
import org.jsoup.nodes.Element
import org.jsoup.nodes.Element


class Downloader(criteria: Array[String])
{
  def getData(): List[String] ={
    var result: List[String] = List[String]("first")
    try {
      val browser = new Browser
      for(i <- 7 to 9){
        //Get ith page of search
        val doc = browser.get(this.getUrl(i))

        var items: List[Element] = doc >> elementList(".blob-code")
        val newList: List[Seq[String]] = items.map(x => x>>texts("td"))
        val resultList = newList
          .map(x => x(0))
          .filter(_ != "")
        for(i <- resultList /*if (i.dropRight(1) contains "=") || (i.dropRight(1) contains ":")*/) result = result :+ i
      }
    }
    catch {
      case e: Exception => List[String]()
    }
    result
  }

  def getSnippet(): String = {
    var snippet: String = ""
    for(i <- criteria){
      snippet += i + "+"
    }
    snippet.dropRight(1)
  }
  def getUrl(page: Int): String = {
    val result: String = "https://github.com/search?p=" + page.toString + "&q=" + this.getSnippet() + "&type=Code"
    result
  }
}