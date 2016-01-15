import net.ruippeixotog.scalascraper.browser.Browser
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL.Parse._
import org.jsoup.nodes.Element

object Hi {
	def main(args: Array[String]) = {
		println("Hi! Enter search criteria: ") 				
		val outFile = new java.io.PrintWriter("login-passwds.txt")
		try { 
			// Get the search criteria separated by " "
			val criteria = readLine.split(" ")
			val x = new Downloader(criteria)
			val browser = new Browser
			for(i <- 7 to 16){
				//Get ith page of search
				val doc = browser.get(x.getUrl(i))
				println("Finished downloading page " + i.toString)
				
				var items: List[Element] = doc >> elementList(".blob-code")
				val newList: List[Seq[String]] = items.map(x => x>>texts("td"))
				val resultList = newList
							.map(x => x(0))
							.filter(_ != "")

				//Save results to a file
				for(i <- resultList if (i.dropRight(1) contains "=") || (i.dropRight(1) contains ":")) outFile.println(i)
			}
		}
		catch {
		  case e: Exception => println(e.getStackTrace.mkString("\n"))
		}
		finally{
			outFile.close
		}
	}
}
class Downloader(criteria: Array[String])
{
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