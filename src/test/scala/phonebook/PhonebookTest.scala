package phonebook

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSuite
import org.scalatest.BeforeAndAfterEach
import org.scalatest.Matchers
import java.io.File
import java.io.PrintWriter
import scala.io.Source
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.nio.ByteBuffer
import java.io.InputStream
import java.io.ByteArrayInputStream

@RunWith(classOf[JUnitRunner])
class PhonebookTest extends FunSuite with Matchers with BeforeAndAfterEach {
 
    println ("running test")
    val mockdir = "tmptests"
    val samplepbname = "sample.pb"
    val pb = mockdir + "/" + samplepbname
    val defaultpbname = "hsphonebook.pb"
    val originals = List("Sarah Ahmed", "Sarah Apple", "Sarah Orange", "Bob Orangutan")
    val originalNums = List("432 123 4321", "509 123 4567", "123 456 7890", "345 653 6732")
    // cleanup before and after
    override def beforeEach = {
        cleanup
        val pbdata = (for {
                (na, nu) <- originals.zip(originalNums)
            } yield "'%s' '%s'".format(na, nu)).mkString("\n")
        val dir = new File(mockdir).mkdirs
        val file = new File(mockdir, samplepbname)
        val writer = new PrintWriter(pb)
        writer.print(pbdata)
        writer.close
        
        val writer2 = new PrintWriter(defaultpbname)
        writer.print("'Jennifer Aniston' '324 040 4020'")
        writer.close

// Note: each write will open a new connection to file and 
//       each write is executed at the begining of the file,
//       so in this case the last write will be the contents of the file.
// See Seekable for append and patching files
// Also See openOutput for performing several writes with a single connection

    }
    override def afterEach = {
        cleanup
    }
    def cleanup = {
        rm(new File(defaultpbname))
        rm(new File(mockdir))
    }
    // removes a file; recursively removes if file is a directory
    def rm(file:File):Unit = {
        if (file.isDirectory) file.listFiles.foreach(f=> rm(f))
        file.delete
    }
    // returns true if a file contains the contents (on any single line)
    def contains(filename:String, contents:String):Boolean = 
        ! Source.fromFile(filename).getLines.forall(l => ! l.contains(contents))
    
	test ("add should add someone if they're not in the pb") {
	    val name = "Big Bird"
        val number = "1 800 SESAMEST"
        // show person not in pb file
        contains(pb, name) should be (false)
        // add person 
        Phonebook.main(Array("add", name, number, "-b", pb))
        // show person is in pb file
        contains(pb, name) should be (true)
	}
	test ("add should print error message if name duplicated") {
        val name = "Bob Orangutan"
        // show name already in file
        contains(pb, name) should be (true)
        val out = new ByteArrayOutputStream();
        Console.setOut(new PrintStream(out));
        // attempt to add duplicate
        Phonebook.main(Array("add", name, "342 323", "-b", pb))
        val output = out.toString
        // TODO this prints to console instead of redirecting output
        println("expect not added:  " + output)
        output.contains("not added") should be (true)
	}
	test("add should use the default phonebook if filename not provided") {
	    val name = "Joel Spolsky"
        val number = "555 FOG CREEK"
        // show person not in pb file
        contains(defaultpbname, name) should be (false)
        // add person 
        Phonebook.main(Array("add", name, number))
        // show person is in defaultpb file
        contains(defaultpbname, name) should be (true)
	}
	test("entries should return the entries in an existing file") {
	    val entries = new Phonebook(pb).entries

	    // corresponds to the sample pb entries
	    val expected = Array(("Sarah Ahmed","432 123 4321"), 
            ("Sarah Apple", "509 123 4567"), ("Sarah Orange","123 456 7890"),
            ("Bob Orangutan","345 653 6732"))
            
        entries.size should be (expected.size)
        entries.forall (e => expected.contains((e.name, e.number))) should be (true)
	}
	test("main -> add should leave existing entries as is") {
	    val name = "Big Bird"
        val number = "1 800 SESAMEST"
        // show person not in pb file
        contains(pb, name) should be (false)
        originals.forall(e => contains(pb, e)) should be (true)
        // add person 
        Phonebook.main(Array("add", name, number, "-b", pb))
        // show person is in pb file
        contains(pb, name) should be (true)
        originals.forall(e => contains(pb, e)) should be (true)
	}
	
	test("add should support multiple phone number formats") {
	    val names = Array("John Doe", "Jane Doe", "Bill Murray", "Joan Collins", "Jimmy Fallon")
	    val numbers = Array("263 478 7040", "1 888 CALLNOW", "(345) 231-5678", "234-234-2345", "912.532.8643")
	    // show numbers not in pb
	    numbers.forall(n => !contains(pb, n)) should be (true)
	    // add entries
	    names.zip(numbers).foreach {
	        case (na,nu) => Phonebook.main(Array("add", na, nu, "-b", pb))
	    }
//	    show all numbers in pb	
	    numbers.forall(n => contains(pb, n)) should be (true)
	}
	test("add should support switch before data and vice versa") {
	    // TODO not yet supported
	    // phonebook add 'Santa Claus' '232 456 3425' -b sample.pb
	    // phonebook add -b sample.pb 'Rudolph Reindeer' '234 352 4677'
	}
	test("pb -> add should immediately make item available in trie for lookup") {
	    val name = "Big Bird"
        val number = "1 800 SESAMEST"
        val phonebook = new Phonebook(pb)
	    println(phonebook.lookup("b").toList)
        phonebook.add(name, number)
        phonebook.entries.exists(e => e.name.equals(name)) should be (true)
	    println(phonebook.lookup("big").toList)
	    println(phonebook.lookup("b").toList)
	    phonebook.lookup("big").exists(e => e.name.equals(name)) should be (true)
	    phonebook.lookup("1800", true).exists(e => e.name.equals(name)) should be (true)
	}
	test ("class -> lookup should find person in pb") {
        val phonebook = new Phonebook(pb)
        val name = "Bob Orangutan"
        val it = phonebook.lookup(name)
        it.isEmpty should be (false)
        it.next.name should be (name)
        it.isEmpty should be (true)
	}
	test ("main -> lookup should find person in pb") {
        // show person is in pb file
        val name = "Bob Orangutan"
        contains(pb, name) should be (true)
        // lookup person 
        val out = new ByteArrayOutputStream();
        Console.setOut(new PrintStream(out));
        Phonebook.main(Array("lookup", "Bob", "-b", pb))
        val output = out.toString
        output.contains(name) should be (true)
	}
//	test ("lookup should return message if not in pb") {
//	    
//	}
//	test ("lookup should return multiple matching entries") {
//	    
//	}
//	test ("main -> reverse-lookup should find person in pb") {
//	    
//	}
//	test ("reverse-lookup should return message if not in pb") {
//		
//	}
//	test ("reverse-lookup should return multiple matching entries") {
//		
//	}
	test ("class -> reverse-lookup should find person in pb") {
        val phonebook = new Phonebook(pb)
        val it = phonebook.lookup("345 653 6732", true)
        it.isEmpty should be (false)
        it.next.name should be ("Bob Orangutan")
	}
	test("remove should remove person from pb") {
        val phonebook = new Phonebook(pb)
        val name = "Bob Orangutan"
        // verify person is in phonebook
        val it = phonebook.lookup(name)
        it.isEmpty should be (false)
        val entry = it.next
        entry.name should be (name)
        // remove from book
	    phonebook.remove(entry) should be (true)
        // verify person is gone
        phonebook.entries.exists(e => e.name.equals(name)) should be (false)
        val newit = phonebook.lookup(entry.name)
        newit.isEmpty should be (true)
	}
	test("remove should prune trie") {
	    
	}
//	test("remove should leave remaining people in file") {
//	    
//	}
//	test("remove should return error message if person not found") {
//	    
//	}
//	test("remove should remove from phone trie") {
//	    
//	}
	test("change should modify file") {
	    
	}
	test("change should modify both tries") {
	    
	}
	test("change should not modify other pb members") {
	    
	}
}
