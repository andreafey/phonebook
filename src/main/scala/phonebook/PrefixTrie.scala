package phonebook;

import collection.mutable.Map
import scala.annotation.tailrec

class PrefixTrie[I] {
	val children:Map[Char, PrefixTrie[I]] = Map()
	var item:Option[I] = None
	
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
	def remove(string:String):Boolean = remove(string.toList)
	// TODO prune - perhaps a "last itemed parent" acc?
//	@tailrec
	private def remove(chars:List[Char]):Boolean = findTrie(chars) match {
	    case None => false
	    case Some(t) => {
	        t.item = None
	        true
	    }
	}
	// TODO maybe this should return a trie? then could get iterator from it...
	def find(string:String):Option[I] = findTrie(string.toList) match {
	    case None => None
	    case Some(t) => t.item
	}
//	@tailrec
	// if it exists, find the node at the end of this search
	def findTrie(chars:List[Char]):Option[PrefixTrie[I]] = chars match {
	    case Nil => Some(this)
	    case c :: cs => children.get(c) match {
	        case None => None
	        case Some(t) => t.findTrie(cs) 
	    }
	}
	def iterator():Iterator[I] = ilist.iterator
	
	// TODO is this lazy, or does toList force eval?
	private def ilist:List[I] = item match {
	    case None => ((for {
	        (k,v) <- children
	    } yield v.ilist).toList).flatten
	    case Some(i) => i :: ((for {
	        (k,v) <- children
	    } yield v.ilist).toList).flatten
	}
}