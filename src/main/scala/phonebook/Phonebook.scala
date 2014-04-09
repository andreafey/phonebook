package phonebook

import scala.io.Source
import java.nio.file.Path
import java.io.File
import java.io.FileWriter

object Phonebook {
    val default = "hsphonebook.pb"
    /* Help is for the interactive console; usage is for the command line utility */
    def help = println("""Commands:
  list, help,
  lookup <str>, reverse-lookup <str>, remove <str>,
  add '<name>' '<number>', change '<name>' '<number>'
  [Enter] to quit""")
    def usage = {
        println("""Usage:
    phonebook create <file>.pb          Creates empty phonebook
    phonebook open [<file>.pb]          Opens an interactive phonebook; creates empty phonebook if needed
                                        Supports all commands below without the phonebook prefix or -b file switch
    phonebook lookup <name> [-b <file>.pb]     
    phonebook add '<name>' '123 456 4323' [-b <file>.pb]
    phonebook change '<name>' '232 987 3940' [-b <file>.pb]
    phonebook remove '<name>' [-b <file>.pb]
    phonebook reverse-lookup '312 432 4252' [-b <file>.pb]""")
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
* */
    }
    def create(file:String):Option[Phonebook] = {
        val pb = new File(file)
        if (!pb.exists) {
        	if (pb.getParentFile != null) pb.getParentFile.mkdirs
        	// create an empty file
        	new FileWriter(file).close
        	println("created phonebook " + pb.getPath)
        	Some(new Phonebook(file))
        } else {
            println(pb.getPath + " already exists")
            None
        }
    }
    def open(file:String):Option[Phonebook] = {
        if (!new File(file).exists) {
            println("file does not exist")
            None
        }
        else Some(new Phonebook(file))
    }
    def lookup(str: String, pb: phonebook.Phonebook, 
            reverse:Boolean = false, suppressOutput:Boolean = false) = {
        val matches = pb.lookup(str, reverse)
        if (!suppressOutput) {
        	if (matches.isEmpty) println("No matches")
        	else matches.foreach(e => println(e))
        }
    }
    def change(name:String, number:String, pb:Phonebook):Boolean = {
    	val matches = pb.lookup(name)
    	// TODO could simplify by just testing the result of pb.change and printing
    	matches.size match {
    	    case 0 => {
    	        println ("match not found")
    	        false
    	    }
    	    case 1 => {
    	        if (pb.change(name, number)) {
    	            println("entry changed")
    	            true
    	        } else {
    	            println("error encountered")
    	            false
    	        }
    	    }
    	    case _ => {
        	    println ("multiple matches found")
        	    matches.foreach(e => println(e))
        	    false
    	    }
    	}
    }
  
   
   def add(name: String, number: String, pb: phonebook.Phonebook): Boolean = {
      val matches = pb.lookup(name)
      if (matches.isEmpty) {
          pb.add(name, number)
          println ("entry added")
          true
      }
      else {
          println("not added; match(es) found")
          matches.foreach(e => println(e))
          false
      }
    }
   
    def remove(name: String, pb: phonebook.Phonebook): Boolean = {
       val matches = pb.lookup(name).toList
       matches.size match {
           case 0 => {
               println ("match not found")
               false
           }
           case 1 => {
               if (pb.remove(matches(0))) {
            	   println ("entry removed")
            	   true
               } else {
                   println ("encountered error")
                   false
               }
           }
           case _ =>  {
    	       println ("multiple matches found")
    	       matches.foreach(e => println(e))
    	       false
           }
        }
    }
    /* Splits arguments in the form "'xx' 'yy'" into ("xx", "yy") */
    def splitArgs(args:String):(String,String) = {
        val list = args.split("'").filter(_.size > 1)
        assert(list.size == 2)
        (list(0), list(1))
    }
    
    def main(args:Array[String]):Unit = {
        args match {
        case Array() => usage
        case _ => args(0) match {
            // phonebook impl adds phone numbers by default
	        case "lookup" => {
	            val name = args(1)
	            val file =
	                if (args.size > 2  && args(2) == "-b") args(3)
	            	else {
	            	    println ("Using default phonebook")
	            	    default
	            	}
	            open(file) match {
	                case Some(pb) => lookup(name, pb)
	                case None => Unit
	            }
	        }
	        case "reverse-lookup" => {
	            val number = args(1)
	            val file =
	                if (args.size > 2  && args(2) == "-b") args(3)
	            	else {
	            	    println ("Using default phonebook")
	            	    default
	            	}
	            open(file) match {
	                case Some(pb) => lookup(number, pb, true)
	                case None => Unit
	            }
	        }
	        case "add" => {
	            val name = args(1)
	            val number = args(2)
	            val file =
	                if (args.size > 3  && args(3) == "-b") args(4)
	            	else {
	            	    println ("Using default phonebook")
	            	    default
	            	}
	            open(file) match {
	                case Some(pb) => if (add(name, number, pb)) pb.save
	                case None => Unit
	            }
	        }
	        case "change" => {
	            val name = args(1)
	            val number = args(2)
	            val file =
	                if (args.size > 3  && args(3) == "-b") args(4)
	            	else default
	            open(file) match {
	                case Some(pb) => if (change(name, number, pb)) pb.save
	                case None => Unit
	            }
	        }
	        case "remove" => {
	            val name = args(1)
	            val file =
	                if (args.size > 2  && args(2) == "-b") args(3)
	            	else default
	            open(file) match {
	                case Some(pb) => if (remove(name, pb)) pb.save
	                case None => Unit
	            }
	        }
	        case "create" => {
	            create(args(1)) match {
	                case Some(pb) => interactive(pb)
	                case None => Unit
	            }
	        }
	        case "open" => {
	            val file = if (args.size > 1) args(1)
	            else {
            	    println ("Using default phonebook")
	                default
	            }
	            open(file) match {
	                case Some(pb) => interactive(pb)
	                case None => Unit
	            }
	        }
	        case _ => usage
        }
    }
   }
  /**
   * The interactive UI to a Phonebook; prompts user for input and responds
   * according to commands.
   */
  private def interactive(pb: phonebook.Phonebook): Unit = {
      println("Enter a command (help for command list):")
      Iterator.continually(Console.readLine).takeWhile(_ != "").foreach(line => {
          val spacei = line.indexOf(" ", 1)
          val (cmd, cmdargs):(String, String) = 
              if (spacei < 0) (line, "") else line.splitAt(spacei)
          cmd match {
              case "list" => pb.entries.foreach(e => println(e))
              case "exit" => println("return to exit or type another command")
              case "help" => println(help)
              case "lookup" => {
              // what follows is the search string
              val search = line.substring(cmd.length + 1)
              println("cmdargs: " + cmdargs)
              println("search " + search)
              lookup(cmdargs, pb)
              }
              case "reverse-lookup" => lookup(cmdargs, pb, true)
              case "add" => {
                  val (name, number) = splitArgs(cmdargs)
                  add(name, number, pb)
              }
              case "change" => {
                  val (name, number) = splitArgs(cmdargs)
                  change(name, number, pb)
              }
              case "remove" => remove(cmdargs, pb)
              case _ => help
          }
      })
      pb.save
      println ("Changes saved. Goodbye.")
    }
}
class Phonebook(file:String) {
    
	val nametrie:Trie = new Trie(' ')
	val numtrie:Trie = new Trie(' ')
    buildTrie(file)
	
    def buildTrie(file:String):Unit = {
        val lines = Source.fromFile(file).getLines
        val entries = lines.foreach { line =>
            val pieces = line.split("'").filter(_.size > 1)
            if (pieces.size > 1) add(pieces(0), pieces(1))
        }
    }
    
	class Entry(val name:String, var number:String) {
	    override def toString = name + " " + number
	}
	
	// TODO generify
    class Trie(val c:Char, 
            val children:collection.mutable.SortedSet[Trie] = collection.mutable.SortedSet(), 
            val entries:collection.mutable.Set[Entry] = collection.mutable.Set()) 
            extends Comparable[Trie] {
        override def toString = "%s -> [%d] -> ".format(c, children.size) + entries
        def iterator:Iterator[Entry] = toList(children.toList, List()).iterator
        private def toList(children:List[Trie], 
                acc:List[Entry]):List[Entry] = children.toList match {
            // TODO make more efficient with cons lists instead of concat
            case List() => entries.toList ++ acc
            case ch :: chs => entries.toList ++ acc ++ ch.toList(ch.children.toList, List()) ++ toList(chs, List())
        }
        def find(chars:List[Char]):Option[Trie] = chars match {
            case Nil => Some(this)
            case c :: cs => {
                children.find(t => t.c == c) match {
                    case None => None
                    case Some(child) => child.find(cs)
                }
            }
        }
        override def compareTo(that:Trie) = this.c.compareTo(that.c)
    }
    
    /** Helper method to strip off non-alphanumeric chars */
    private def tx4lookup(str:String):String = 
        str.toLowerCase.replaceAll("[^a-z0-9]", "")
    
    def add(name:String, number:String):Unit = {
        val entry = new Entry(name, number)
        add(nametrie, entry, tx4lookup(name).toList)
        add(numtrie, entry, tx4lookup(number).toList)
        Unit
    } 
    private def add(trie:Trie, entry:Entry, seq:List[Char]):Trie = seq match {
        case List() => {
            trie.entries.add(entry)
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
    
    def entries = nametrie.iterator
    
    def save = {
		val pbdata = if (entries.nonEmpty) (for {
			e <- entries
		} yield "'%s' '%s'".format(e.name, e.number)).mkString("\n")
		else ""
		val out = new java.io.FileWriter(file)
		out.write(pbdata)
		out.close
    }
    
    def lookup(str:String, reverse:Boolean = false):Iterator[Entry] =
    		if (reverse) lookup(tx4lookup(str), numtrie)
    		else lookup(tx4lookup(str), nametrie)
    		
    private def lookup(str:String, trie:Trie):Iterator[Entry] = matches(str)(trie) match {
        case None => Iterator()
        case Some(m) => m.iterator
    }
    
    def matches(str:String)(trie:Trie):Option[Trie] = trie.find(str.toList)
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
    
    def remove(entry:Entry):Boolean = {
        val nameT = matches(tx4lookup(entry.name))(nametrie)
		val numT = matches(tx4lookup(entry.number))(numtrie)
		
        (nameT, numT) match {
            case (Some(na), Some(nu)) => {
                na.entries.remove(entry)
                nu.entries.remove(entry)
                true
            }
            case _ => false
        } 
    }
    // find the parent of this entry in a particular trie 
    private def findParent(entry:Entry, lookupBy:String)(trie:Trie):Option[Trie] = {
        val chars = tx4lookup(lookupBy)
        val (firstPart, lastChar) = chars.splitAt(chars.length - 1)
        matches(firstPart.mkString)(trie) match {
            case None => None
            case Some(m) => m.find(lastChar.toList) match {
                case None => None
                case Some(n) => if (n.entries.contains(entry)) Some(m) else None
            }
        }
    }
    // find the node containing this entry in a particular trie 
    private def findTrie(entry:Entry, lookupBy:String)(trie:Trie):Option[Trie] = {
        val chars = tx4lookup(lookupBy)
        val (firstPart, lastChar) = chars.splitAt(chars.length - 1)
        matches(chars)(trie) match {
            case None => None
            case Some(m) => m.find(lastChar.toList) match {
                case None => None
                case Some(n) => if (n.entries.contains(entry)) Some(m) else None
            }
        }
    }

}