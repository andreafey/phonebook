package phonebook

import scala.io.Source
import java.nio.file.Path
import java.io.File
import java.io.FileWriter
import java.util.regex.Pattern

object Phonebook {
    val default = "hsphonebook.pb"
/*
created phonebook hsphonebook.pb in the current directory

$ phonebook lookup Sarah -b hsphonebook.pb # error message on no such phonebook
Sarah Ahmed 432 123 4321
Sarah Apple 509 123 4567
Sarah Orange 123 456 7890
sbt
sbt run add ...
$ phonebook add 'John Michael' '123 456 4323' -b hsphonebook.pb # error message on duplicate name    
$ phonebook change 'John Michael' '234 521 2332' -b hsphonebook.pb # error message on not exist
$ phonebook remove 'John Michael' -b hsphonebook.pb # error message on not exist
$ phonebook reverse-lookup '312 432 5432'
*/
    sealed abstract class Status
    case class Failure(message:String) extends Status
    case class TooManyMatches(message:String) extends Status
    case class NoMatchFound(message:String) extends Status
    case class Success(message:String) extends Status
    
	
    def fromFile(file:File):Phonebook = {
        val pb = new Phonebook()
        val lines = Source.fromFile(file).getLines
        lines.foreach { line =>
            val pieces = line.split("'").filter(_.size > 1)
            if (pieces.size > 1) pb.add(pieces(0), pieces(1))
        }
        pb
    }
    def saveToFile(phonebook:Phonebook, file:File) = {
        val pbdata = (phonebook.entries map (e => "'%s' '%s'".format(e.name, e.number))).mkString("\n")
		val out = new java.io.FileWriter(file)
		out.write(pbdata + "\n")
		out.close
    }
    
    def change(name:String, number:String, pb:Phonebook):Status =
    	pb.lookup(name).size match {
    	    case 0 => NoMatchFound("match not found")
    	    case 1 => 
    	        if (pb.change(name, number)) Success("entry changed")
    	        else Failure("error encountered")
    	    case _ => TooManyMatches("multiple matches found")
    	}
   
   def add(name: String, number: String, pb: phonebook.Phonebook): Status = {
      val matches = pb.lookup(name)
      if (matches.isEmpty) {
          pb.add(name, number)
          Success("entry added")
      }
      else TooManyMatches("not added; match(es) found")
    }
   
    def remove(name: String, pb: phonebook.Phonebook): Status = {
       val matches = pb.lookup(name).toList
       matches.size match {
           case 0 => NoMatchFound("match not found")
           case 1 =>
               if (pb.remove(matches(0))) Success("entry removed")
               else Failure("encountered error")
           case _ => TooManyMatches("multiple matches found")
        }
    }
    /* Splits arguments in the form "'xx' 'yy'" into ("xx", "yy") */
    private def splitArgs(args:String):(String,String) = {
        val list = args.split("'").filter(_.size > 1)
        assert(list.size == 2)
        (list(0), list(1))
    }
    
    case class ParserConfig(file: File = new File("hsphonebook.pb"), pb: Phonebook = new Phonebook(), interactive: Boolean = false,
            create: Boolean = false, remove: Boolean = false, add: Boolean = false, 
            change: Boolean = false, lookup: Boolean = false, reverse: Boolean = false,
            search: String = "", name: String = "", number: String = "")
            
    private val parser = new scopt.OptionParser[ParserConfig]("phonebook") {
        override def showUsageOnError = true
        head("phonebook", "0.x")
        cmd("create") optional() action { (_, c) =>
            c.copy(create = true, interactive = true) } text (
                "creates phonebook and opens in an interactive console") children(
            arg[File]("<file>") action { (x, c) => {
                if (x.createNewFile) c.copy(file = x)
                else c }} text("path to new phonebook file"))
        cmd("open") optional() action { (_, c) =>
            c.copy(interactive = true) } text (
                "opens existing phonebook in an interactive console") children(
            arg[File]("<file>") optional() action { (x, c) =>
                c.copy(file = x, pb = fromFile(x)) } text("path to existing phonebook file; default used if omitted"))
        cmd("lookup") optional() action { (srch, c) => c.copy(lookup = true) } children(
            arg[String]("<search>") action { (x, c) => c.copy(search = x) }) text("lookup names")
        cmd("reverse-lookup") optional() action { (srch, c) => c.copy(lookup = true, reverse = true) } children(
            arg[String]("<search>") action { (x, c) => c.copy(search = x) }) text("lookup by phone number")
        cmd("add") optional() action { (srch, c) => c.copy(add = true) } children(
            arg[String]("'<name>'") action { (x, c) => c.copy(name = x) } text("full name, surrounded by single quotes"),
            arg[String]("'<number>'") action { (x, c) => c.copy(number = x) } text(
                    "phone number, surrounded by single quotes")) text("add phonebook entry")
        cmd("change") optional() action { (srch, c) => c.copy(change = true) } children(
            arg[String]("'<name>'") action { (x, c) => c.copy(name = x) } text("must match exactly one name"),
            arg[String]("'<number>'") action { (x, c) => c.copy(number = x) } text(
                    "phone number, surrounded by single quotes")) text("change phone number associated with name")
        cmd("remove") optional() action { (srch, c) => c.copy(remove = true) } children(
            arg[String]("<search>") action { (x, c) => c.copy(search = x) } text(
                    "must match exactly one name")) text("remove item which matches search")
        opt[File]('b', "book") action { (x, c) => c.copy(file = x, pb = fromFile(x)) } text("path to existing phonebook file; default used if omitted")
        opt[Unit]('i', "interactive") action { (_, c) => c.copy(interactive = true) } text ("applies other commands and opens phonebook in interactive mode")
        help("help") text("displays this usage information")
    }
        
    private val consoleParser = new scopt.OptionParser[ParserConfig]("phonebook") {
        override def showUsageOnError = true
        cmd("lookup") optional() action { (srch, c) => c.copy(lookup = true) } children(
            arg[String]("<search>") action { (x, c) => c.copy(search = x) }) text("lookup names")
        cmd("reverse-lookup") optional() action { (srch, c) => c.copy(lookup = true, reverse = true) } children(
            arg[String]("<search>") action { (x, c) => c.copy(search = x) }) text("lookup by phone number")
        cmd("add") optional() action { (srch, c) => c.copy(add = true) } children(
            arg[String]("'<name>'") action { (x, c) => c.copy(name = x) } text("full name, surrounded by single quotes"),
            arg[String]("'<number>'") action { (x, c) => c.copy(number = x) } text(
                    "phone number, surrounded by single quotes")) text("add phonebook entry")
        cmd("change") optional() action { (srch, c) => c.copy(change = true) } children(
            arg[String]("'<name>'") action { (x, c) => c.copy(name = x) } text("must match exactly one name"),
            arg[String]("'<number>'") action { (x, c) => c.copy(number = x) } text(
                    "phone number, surrounded by single quotes")) text("change phone number associated with name")
        cmd("remove") optional() action { (srch, c) => c.copy(remove = true) } children(
            arg[String]("<search>") action { (x, c) => c.copy(search = x) } text(
                    "must match exactly one name")) text("remove item which matches search")
        cmd("help") action { (_, c) => {
            showUsage
            c } } text("displays this usage information")
    }
    /**
     * Prints search output to console or error message if no matches found
     */
    private def lookupAction(pb:Phonebook, search:String, reverse:Boolean):Unit = {
        val matches = pb.lookup(search, reverse)
        if (matches.isEmpty) println("No matches")
        else matches.foreach(e => println(e))
    }
    /**
     * Adds entry to Phonebook and prints status message; saves file on success
     */
    private def addAction(pb:Phonebook, name:String, number:String, file:File) = 
        add(name, number, pb) match {
	        case Success(m) => {
	            println(m)
	            saveToFile(pb, file)
	        }
	        case NoMatchFound(m) => println(m)
	        case TooManyMatches(m) => {
	            println(m)
	            pb.lookup(name).foreach(println(_))
	        }
	        case Failure(m) => println(m)
    	}
    /**
     * Changes Phonebook entry and print status message; saves file on success
     */
    private def changeAction(search:String, number:String, pb:Phonebook, file:File) =
        change(search, number, pb) match {
	        case Success(m) => {
	            println(m)
	            saveToFile(pb, file)
	        }
	        case NoMatchFound(m) => println(m)
	        case TooManyMatches(m) => {
	            println(m)
	            pb.lookup(search).foreach(println(_))
	        }
	        case Failure(m) => println(m)
	    }
    /**
     * Removes Phonebook entry and prints status message; saves file on success
     */
    private def removeAction(search:String, pb:Phonebook, file:File) =
        remove(search, pb) match{
            case Success(m) => {
                println(m)
                saveToFile(pb, file)
            }
            case TooManyMatches(m) => {
                println(m)
                pb.lookup(search).foreach(println(_))
            }
            case Failure(m) => println(m)
            case NoMatchFound(m) => println(m)
        }
    def configActions(c: ParserConfig):Unit = { 
        if (c.create)
            println("New phonebook created")
        else if (c.lookup)
            lookupAction(c.pb, c.search, c.reverse)
        else if (c.add) 
            addAction(c.pb, c.name, c.number, c.file)
        else if (c.change) 
            changeAction(c.name, c.number, c.pb, c.file)
        else if (c.remove)
            removeAction(c.search, c.pb, c.file)
        if (c.interactive)
            interactive(c.pb, c.file)
    }
    /**
     * Parse command line arguments and 
     */
    def main(args: Array[String]): Unit = {
        val conf = ParserConfig()
        parser.parse(args, conf) map (configActions)
//        saveToFile(conf.pb, conf.file)
    }
    
   
    /**
     * The interactive UI to a Phonebook; prompts user for input and responds
     * according to commands.
     */
    private def interactive(pb: Phonebook, file:File): Unit = {
        println("Enter a command (--help for command list):")
        Iterator.continually(Console.readLine).takeWhile(_ != "").foreach(line => {
            val args = parseArgs(line)
            // TODO add try/catch feeds to help
            consoleParser.parse(args, ParserConfig().copy(file = file, pb = pb)) map (configActions)
        })
    }
  // copied from StackOverflow: 
  // http://stackoverflow.com/questions/366202/regex-for-splitting-a-string-using-space-when-not-surrounded-by-single-or-double
    /**
     * Argument parser which behaves exactly like command line parser in transforming
     * a string into an args list
     */
    private def parseArgs(str:String) = {
    	var list:List[String] = List();
    	val regex = Pattern.compile("[^\\s\"']+|\"([^\"]*)\"|'([^']*)'")
    	val regexMatcher = regex.matcher(str)
    	while (regexMatcher.find()) {
    		if (regexMatcher.group(1) != null)
    			// Add double-quoted string without the quotes
    			list = regexMatcher.group(1) :: list
    		else if (regexMatcher.group(2) != null)
    			// Add single-quoted string without the quotes
    			list = regexMatcher.group(2) :: list
    		else
    			// Add unquoted word
    			list = regexMatcher.group() :: list
    	} 
    	list.reverse
    }
}

class Phonebook {
    
    /**
     * The prefix trie which stores entries in name order
     */
	val nametrie:PrefixTrie[Entry] = new LowerAlphanumTrie()
	/**
	 * The prefix trie which stores entries in number order
	 */
	val numtrie:PrefixTrie[Entry] = new LowerAlphanumTrie()
    /**
     * A name/phone number domain object
     */
	class Entry(val name:String, var number:String) {
	    override def toString = name + " " + number
	}
	
	/**
	 * Add an entry to the phonebook
	 */
    def add(name:String, number:String):Unit = {
        val entry = new Entry(name, number)
        nametrie.put(name, entry)
        numtrie.put(number, entry)
        Unit
    } 
    /**
     * An iterable of all the items in this (sub)trie
     */
    def entries = nametrie.items
    /**
     * Find the entries which match a search term; reverse does a phone number
     * lookup instead of the default name lookup
     */
    def lookup(search:String, reverse:Boolean = false):Stream[Entry] =
		if (reverse) entries(search.toList, numtrie)
		else entries(search.toList, nametrie)
	/**
	 * Find the entries within a trie which match chars
	 */
    private def entries(chars:List[Char], trie:PrefixTrie[Entry]):Stream[Entry] =
        trie.find(chars) match {
		    case None => Stream()
		    case Some(t) => t.items
	    }
    /**
     * Change the entry with this name to have a new phone number
     */
    def change(name:String, number:String):Boolean = {
        val nodes = lookup(name).filter(e => e.name.equalsIgnoreCase(name)).toList
        nodes match {
            case Nil => false
            case e :: Nil => {
                if (remove(e)) {
                	add(e.name, number)
                	true
                } else false
            }
            // multiple matches
            case _ => false
        }
    }
    /**
     * Remove this entry from the phonebook
     */
    def remove(entry:Entry):Boolean = {
        nametrie.remove(entry.name)
        numtrie.remove(entry.number)
    }

}