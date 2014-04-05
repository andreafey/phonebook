package phonebook

import scala.io.Source
import java.nio.file.Path

object Phonebook {
    val default = "hsphonebook.pb"
    def usage = {
        println("""Usage:
    phonebook create <file>.pb 
    phonebook lookup <name> -b <file>.pb
    phonebook add '<name>' '123 456 4323' -b <file>.pb
    phonebook change '<name>' '232 987 3940' -b <file>.pb
    phonebook remove '<name>' -b <file>.pb
    phonebook reverse-lookup '312 432 4252' -b <file>.pb""")
                /*
created phonebook hsphonebook.pb in the current directory

$ phonebook lookup Sarah -b hsphonebook.pb # error message on no such phonebook
Sarah Ahmed 432 123 4321
Sarah Apple 509 123 4567
Sarah Orange 123 456 7890

$ phonebook add 'John Michael' '123 456 4323' -b hsphonebook.pb # error message on duplicate name    

$ phonebook change 'John Michael' '234 521 2332' -b hsphonebook.pb # error message on not exist

$ phonebook remove 'John Michael' -b hsphonebook.pb # error message on not exist

$ phonebook reverse-lookup '312 432 5432'
* */
    }
    def main(args:Array[String]) = {
        args match {
        case Array() => usage
        case _ => args(0) match {
            case "create" => {
                val file = args(1)
                assert(file.endsWith(".pb"))
                val out = new java.io.FileWriter(file)
                out.close
            }
	        case "lookup" => {
	            val name = args(1)
	            val file =
	                if (args.size > 2  && args(2) == "-b") args(3)
	            	else default
	        }
	        case "add" => {
	            val name = args(1)
	            val number = args(2)
	            val file =
	                if (args.size > 3  && args(3) == "-b") args(4)
	            	else default
	            
	        }
	        case "change" => {
	            val name = args(1)
	            val number = args(2)
	            val file =
	                if (args.size > 3  && args(3) == "-b") args(4)
	            	else default
	            
	        }
	        case "remove" => {
	            val name = args(1)
	            val file =
	                if (args.size > 2  && args(2) == "-b") args(3)
	            	else default
	            
	        }
	        case "reverse-lookup" => {
	            val num = args(1)
	            val file =
	                if (args.size > 2  && args(2) == "-b") args(3)
	            	else default
	            
	        }
        case _ => usage
        // parse args
        }
    }
}
}
class Phonebook(file:String) {
	class Entry(val name:String, var number:String) {
	    override def toString = name + " " + number
	}
	
	// TODO abstract this to a different class?
    class Trie(val c:Char, 
            val children:collection.mutable.Set[Trie] = collection.mutable.Set(), 
            var entry:Option[Entry] = None) {
        // ??? TODO re"visit" visitor pattern
        def traverse(visit:Trie => Any):Any = {
            visit(this)
            children.foreach(t => traverse(visit))
        }
        def iterator:Iterator[Entry] = toList(List()).iterator
        private def toList(acc:List[Entry]):List[Entry] = children.toSeq match {
            case Seq() => entry match {
                case None => acc
                case Some(e) => e :: acc
            }
            case cs => entry match {
                case None => acc ++ (for {
                	c <- cs
                } yield c.toList(acc)).flatten
                case Some(e) => e :: acc ++ (for {
                	c <- cs
                } yield c.toList(acc)).flatten
            }
        }
        def find(chars:Seq[Char]):Option[Trie] = chars match {
            case Nil => Some(this)
            case c :: cs => {
                children.find(t => t.c == c) match {
                    case None => None
                    case Some(child) => child.find(cs)
                }
            }
        }
            // scala tree separates node from leaf and uses this as iterator impl
//332	  def toList(acc: List[(A,B)]): List[(A,B)] =
//333	    smaller.toList((key, value) :: bigger.toList(acc))
//334	
        override def toString = toSeq(children.toSeq, Seq(c)).mkString(" ")
        def toSeq(tries:Seq[Trie], acc:Seq[Char]):Seq[Char] = tries match {
            case Seq() => acc
            case t :: ts =>
                // TODO this is wrong - recursive
                toSeq(ts, acc ++ Seq(c)) ++ toSeq(t.children.toSeq, Seq())
        }
    }
    
    def add(trie:Trie, entry:Entry, seq:Seq[Char]):Trie = seq match {
        case Seq() => {
            trie.entry = Some(entry)
            trie
        }
        case c :: cs => trie.children.find(child => c == child.c) match {
            case None => {
                val subTrie = new Trie(c)
                trie.children.add(subTrie)
                add(subTrie, entry, cs)
            }
            case Some(t) => add(t, entry, cs)
        }
    }
    
    def entries = trie.iterator
    
    def save = trie match {
        case None => Unit
        case Some(t) => {
            val entries = t.iterator
            if (entries.nonEmpty) {
            	val out = new java.io.FileWriter(file)
            	// TODO do I need to write a newline?
            	entries.foreach(e => out.write(e.toString))
            	out.close
            }
        }
    }
    def lookup(str:String):Iterator[Entry] = matches(str) match {
        case None => Iterator()
        case Some(m) => m.iterator
    }
    def matches(str:String):Option[Trie] = trie match {
        case None => None
        case Some(t) => t.find(str)
    } 
    def change(str:String, number:String):Boolean = {
        val nodes = lookup(str).toList
        nodes match {
            case Nil => false
            case e :: Nil => {
                e.number = number
                true
            }
            case _ => false
        }
    }
    def remove(entry:Entry) = {
        val (first, last) = entry.name.toSeq.splitAt(entry.name.length - 1)
        matches(first.mkString) match {
            case None => Unit
            case Some(m) => m.find(last) match {
                case None => Unit
                case Some(f) => m.children.remove(f)
            }
        }
    }
    lazy val trie:Option[Trie] = buildTrie(file)
    def buildTrie(file:String):Option[Trie] = {
        var root:Option[Trie] = None
        val lines = Source.fromFile(file).getLines
        val entries = lines.foreach { line =>
//val stuff = new scala.util.matching.Regex("""(([a-zA-Z] ?)*) ([\d ()-]*)""", "name", "number")
            val pieces = line.split("'").filter(_.size > 1)
            val entry = new Entry(pieces(0), pieces(1))
            // name, last name, phone number, all without spaces or extra chars
            // NOTE: assuming last name is last part of entry.name and 
            // phone number breaks down to only digits
            val inserts = List(entry.name.toLowerCase.replace(" ", ""),
                    entry.name.split(" ").last.toLowerCase,
                    entry.number.replaceAll("""[^\d]""", ""))
            inserts.foreach { item =>
                root match {
                	case None => root = Some(new Trie(item(0)))
                	case Some(t) => add(t, entry, item.toSeq)
                }
            }
        }
        root
    }

}