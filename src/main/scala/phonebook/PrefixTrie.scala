package phonebook;

import collection.mutable.Map
import scala.annotation.tailrec

class PrefixTrie[I] extends Iterable[I] {
	private val children:Map[Char, PrefixTrie[I]] = Map()
	private var item:Option[I] = None
	
	def put(string:String, item:I):Unit = put(string.toList, item)
	@tailrec
	private def put(chars:List[Char], item:I):Unit = chars match {
	    case Nil => this.item = Some(item)
	    case c :: cs => {
	        // if node is not already a child, create it and put it in children
	        val child = children.get(c) match {
                case None => {
                    val node = new PrefixTrie[I]
                    children.put(c, node)
                    node
                }
                case Some(t) => t
		    }
	        child.put(cs, item)
	    }
	}
	def remove(string:String):Boolean = {
	    val removed = remove(string.toList)
	    prune(string.toList)
	    removed
	}
	// Assume this is a valid path in the trie
	private def prune(chars:List[Char]):Unit = chars match {
	    case Nil => Unit
	    case c :: cs => if (children.contains(c)) {
	        if (children(c).items.isEmpty) children.remove(c)
	    }
	}
	private def remove(chars:List[Char]):Boolean = find(chars) match {
	    case None => false
	    case Some(t) => {
	        t.item = None
	        true
	    }
	}
	// if it exists, find the node at the end of this search
	def find(chars:List[Char]):Option[PrefixTrie[I]] = chars match {
	    case Nil => Some(this)
	    case c :: cs => children.get(c) match {
	        case None => None
	        case Some(t) => t.find(cs) 
	    }
	}
	override def iterator():Iterator[I] = items.iterator
	
	def items:Stream[I] = item match {
	    case None => (children.map { case (k,v) => v.items }).flatten.toStream
	    case Some(i) => i #:: (children.map { case (k,v) => v.items }).flatten.toStream
	}
}