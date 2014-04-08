package phonebook

import scala.io.Source
import java.nio.file.Path
<<<<<<< HEAD
import java.io.File
import java.io.FileWriter
=======
>>>>>>> 9be7a4793f104bba7ed6d0db570197afbf419e4c

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
<<<<<<< HEAD
sbt
sbt run add ...
=======

>>>>>>> 9be7a4793f104bba7ed6d0db570197afbf419e4c
$ phonebook add 'John Michael' '123 456 4323' -b hsphonebook.pb # error message on duplicate name    

$ phonebook change 'John Michael' '234 521 2332' -b hsphonebook.pb # error message on not exist

$ phonebook remove 'John Michael' -b hsphonebook.pb # error message on not exist

$ phonebook reverse-lookup '312 432 5432'
* */
    }
<<<<<<< HEAD
    def create(file:String) = {
        val pb = new File(file)
        assert(!pb.exists)
        pb.getParentFile.mkdirs
        new Phonebook(pb.getPath)
    }
    def open(file:String) = new Phonebook(file)
=======
>>>>>>> 9be7a4793f104bba7ed6d0db570197afbf419e4c
    def main(args:Array[String]) = {
        args match {
        case Array() => usage
        case _ => args(0) match {
<<<<<<< HEAD
            case "create" => create(args(1)).save
            // phonebook impl adds phone numbers by default
=======
            case "create" => {
                val file = args(1)
                assert(file.endsWith(".pb"))
                val out = new java.io.FileWriter(file)
                out.close
            }
>>>>>>> 9be7a4793f104bba7ed6d0db570197afbf419e4c
	        case "lookup" => {
	            val name = args(1)
	            val file =
	                if (args.size > 2  && args(2) == "-b") args(3)
	            	else default
<<<<<<< HEAD
            	val pb = open(file)
            	val matches = pb.lookup(name)(pb.nametrie)
            	if (matches.isEmpty) println("No matches")
            	else matches.foreach(e => println(e))
	        }
	        case "reverse-lookup" => {
	            val number = args(1)
	            val file =
	                if (args.size > 2  && args(2) == "-b") args(3)
	            	else default
            	val pb = open(file)
            	val matches = pb.lookup(number)(pb.numtrie)
            	if (matches.isEmpty) println("No matches")
            	else matches.foreach(e => println(e))
	            
=======
>>>>>>> 9be7a4793f104bba7ed6d0db570197afbf419e4c
	        }
	        case "add" => {
	            val name = args(1)
	            val number = args(2)
	            val file =
	                if (args.size > 3  && args(3) == "-b") args(4)
	            	else default
<<<<<<< HEAD
            	val pb = open(file)
            	val matches = pb.lookup(name)(pb.nametrie)
            	if (matches.isEmpty) {
            	    pb.add(name, number)
            	    pb.save
            	}
	            // TODO see requirements for error
            	else matches.foreach(e => println(e))
=======
>>>>>>> 9be7a4793f104bba7ed6d0db570197afbf419e4c
	            
	        }
	        case "change" => {
	            val name = args(1)
	            val number = args(2)
	            val file =
	                if (args.size > 3  && args(3) == "-b") args(4)
	            	else default
<<<<<<< HEAD
	            // TODO this should just call change and let change return true or false
            	val pb = open(file)
            	val matches = pb.lookup(name)(pb.nametrie)
            	matches.size match {
            	    case 0 => println ("match not found")
            	    case 1 => {
            	        matches.next().number = number  
            	        pb.save
            	    } 
            	    case _ => {
	            	    println ("multiple matches found")
	            	    matches.foreach(e => println(e))
            	    }
            	}
=======
	            
>>>>>>> 9be7a4793f104bba7ed6d0db570197afbf419e4c
	        }
	        case "remove" => {
	            val name = args(1)
	            val file =
	                if (args.size > 2  && args(2) == "-b") args(3)
	            	else default
<<<<<<< HEAD
            	val pb = open(file)
            	val matches = pb.lookup(name)(pb.nametrie)
            	matches.size match {
            	    case 0 => println ("match not found")
            	    case 1 => {
            	        pb.remove(matches.next())
            	        pb.save
            	    }
            	    case _ =>  {
	            	    println ("multiple matches found")
	            	    matches.foreach(e => println(e))
            	    }
            	}
	        }
	        case "open" => {
	            val file = args(1)
	            
	        }
        case _ => usage
=======
	            
	        }
	        case "reverse-lookup" => {
	            val num = args(1)
	            val file =
	                if (args.size > 2  && args(2) == "-b") args(3)
	            	else default
	            
	        }
        case _ => usage
        // parse args
>>>>>>> 9be7a4793f104bba7ed6d0db570197afbf419e4c
        }
    }
}
}
class Phonebook(file:String) {
<<<<<<< HEAD
    
	val nametrie:Trie = new Trie(' ')
	val numtrie:Trie = new Trie(' ')
    buildTrie(file)
	
    def buildTrie(file:String):Unit = {
        val lines = Source.fromFile(file).getLines
        val entries = lines.foreach { line =>
            val pieces = line.split("'").filter(_.size > 1)
            if (pieces.size > 1) {
	            val entry = new Entry(pieces(0), pieces(1))
	            // name, phone number without spaces or extra chars
	            add(nametrie, entry, tx4Lookup(entry.name))
	            add(numtrie, entry, tx4Lookup(entry.number))
            }
        }
    }
    def tx4Lookup(str:String):List[Char] = str.toLowerCase.replace(" ", "").toList
    
=======
>>>>>>> 9be7a4793f104bba7ed6d0db570197afbf419e4c
	class Entry(val name:String, var number:String) {
	    override def toString = name + " " + number
	}
	
<<<<<<< HEAD
	// TODO abstract and generify?
    class Trie(val c:Char, 
            val children:collection.mutable.SortedSet[Trie] = collection.mutable.SortedSet(), 
            val entries:collection.mutable.Set[Entry] = collection.mutable.Set()) 
            extends Comparable[Trie] {
        override def toString = "%s -> [%d] -> ".format(c, children.size) + entries
        def iterator:Iterator[Entry] = toList(children.toList, List()).iterator
        private def toList(children:List[Trie], 
                acc:List[Entry]):List[Entry] = children.toList match {
            // TODO revisit to make more efficient
            case List() => entries.toList ++ acc
            case ch :: chs => entries.toList ++ acc ++ ch.toList(ch.children.toList, List()) ++ toList(chs, List())
        }
        def find(chars:List[Char]):Option[Trie] = chars match {
            case Nil => Some(this)
            case c :: cs => {
                if (c.toLower == this.c)
	                children.find(t => t.c == c) match {
	                    case None => None
	                    case Some(child) => child.find(cs)
	                }
                else None
            }
        }
        override def compareTo(that:Trie) = this.c.compareTo(that.c)
=======
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
>>>>>>> 9be7a4793f104bba7ed6d0db570197afbf419e4c
            // scala tree separates node from leaf and uses this as iterator impl
//332	  def toList(acc: List[(A,B)]): List[(A,B)] =
//333	    smaller.toList((key, value) :: bigger.toList(acc))
//334	
<<<<<<< HEAD
//        override def toString = toSeq(children.toSeq, Seq(c)).mkString(" ")
//        def toSeq(tries:Seq[Trie], acc:Seq[Char]):Seq[Char] = tries match {
//            case Seq() => acc
//            case t :: ts =>
//                // TODO this is wrong - recursive
//                toSeq(ts, acc ++ Seq(c)) ++ toSeq(t.children.toSeq, Seq())
//        }
    }
    
    def add(name:String, number:String):Unit = {
		add(nametrie, new Entry(name, number), name.toList) 
        Unit
    } 
    private def add(trie:Trie, entry:Entry, seq:List[Char]):Trie = seq match {
        case List() => {
            trie.entries.add(entry)
//            println ("added " + entry)
=======
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
>>>>>>> 9be7a4793f104bba7ed6d0db570197afbf419e4c
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
    
<<<<<<< HEAD
    def entries = nametrie.iterator
    
    def save = {
        if (entries.nonEmpty) {
            val pbdata = (for {
                e <- entries
            } yield "'%s' '%s'".format(e.name, e.number)).mkString("\n")
        	val out = new java.io.FileWriter(file)
        	out.write(pbdata)
        	out.close
        }
    }
    def lookup(str:String)(trie:Trie = nametrie):Iterator[Entry] = matches(str)(trie) match {
        case None => Iterator()
        case Some(m) => m.iterator
    }
    
    def matches(str:String)(trie:Trie):Option[Trie] = trie.find(str.toList)
    
    def change(str:String, number:String):Boolean = {
        // TODO change both tries
        val nodes = lookup(str)(nametrie).toList
=======
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
>>>>>>> 9be7a4793f104bba7ed6d0db570197afbf419e4c
        nodes match {
            case Nil => false
            case e :: Nil => {
                e.number = number
                true
            }
            case _ => false
        }
    }
<<<<<<< HEAD
    
    def remove(entry:Entry):Boolean = {
        val namePar = findParent(entry, entry.name)(nametrie)
		val numPar = findParent(entry, entry.name)(numtrie)
		
        (namePar, numPar) match {
            case (Some(na), Some(nu)) => {
                na.entries.remove(entry)
                nu.entries.remove(entry)
                true
            }
            case _ => {
            	println ("entry not removed")
                false
            } 
        } 
    }
    // find the parent of this entry in a particular trie 
    private def findParent(entry:Entry, lookupBy:String)(trie:Trie):Option[Trie] = {
        val chars = tx4Lookup(lookupBy)
        val (firstPart, lastChar) = chars.splitAt(chars.length - 1)
        matches(firstPart.mkString)(nametrie) match {
            case None => None
            case Some(m) => m.find(lastChar) match {
                case None => None
                case Some(n) => if (n.entries.contains(entry)) Some(m) else None
            }
        }
=======
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
>>>>>>> 9be7a4793f104bba7ed6d0db570197afbf419e4c
    }

}