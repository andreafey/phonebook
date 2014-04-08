package phonebook

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSuite
import org.scalatest.BeforeAndAfterEach
import org.scalatest.Matchers
import java.io.File
import java.io.PrintWriter
import scala.io.Source

@RunWith(classOf[JUnitRunner])
class PhonebookTest extends FunSuite with Matchers with BeforeAndAfterEach {
 
    println ("running test")
    val mockdir = "tmptests"
    val samplepb = "sample.pb"
    val defaultpb = "hsphonebook.pb"
    val originals = List("Sarah Ahmed", "Sarah Apple", "Sarah Orange", "Bob Orangutan")
    val originalNums = List("432 123 4321", "509 123 4567", "123 456 7890", "345 653 6732")
    // cleanup before and after
    override def beforeEach {
        cleanup
        val pbdata = (for {
                (na, nu) <- originals.zip(originalNums)
            } yield "'%s' '%s'".format(na, nu)).mkString("\n")
        val dir = new File(mockdir).mkdirs
        val file = new File(mockdir, samplepb)
        val writer = new PrintWriter(mockdir + "/" + samplepb)
        writer.print(pbdata)
        writer.close
        
        val writer2 = new PrintWriter(defaultpb)
        writer.print("'Jennifer Aniston' '324 040 4020'")
        writer.close

// Note: each write will open a new connection to file and 
//       each write is executed at the begining of the file,
//       so in this case the last write will be the contents of the file.
// See Seekable for append and patching files
// Also See openOutput for performing several writes with a single connection

    }
    override def afterEach {
        cleanup
    }
    def cleanup = {
        rm(new File(defaultpb))
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
    
    test("create should create new directory if needed") {
	    // test that the dir does not exist
        val dir = mockdir + "/stragetsrgtgsr"
		val file = "testfile.pb"
        new File(dir).exists should be (false)
        new File(dir, file).exists should be (false)
        Phonebook.main(Array("create", dir + "/" + file))
        val ndir = new File(dir)
        ndir.exists should be (true)
        ndir.isDirectory should be (true)
        new File(dir, file).exists should be (true)
    }
	test("create should create a new file") {
	    // create file
	    val file = "testfile.pb"
	    // test that file does not exist
        new File(mockdir, file).exists should be (false)
        Phonebook.main(Array("create", mockdir + "/" + file))
	    // test that file exists
        new File(mockdir, file).exists should be (true)
	}
	test ("add should add someone if they're not in the pb") {
	    val name = "Big Bird"
        val number = "1 800 SESAMEST"
        val pb =  mockdir + "/" + samplepb
        // show person not in pb file
        contains(pb, name) should be (false)
        // add person 
        Phonebook.main(Array("add", name, number, "-b", pb))
        // show person is in pb file
        contains(pb, name) should be (true)
	}
	test ("add should print error message if someone is already in the pb") {
	    // TODO how to harness console output? intercept system.out into output buffer?
	}
	test("add should use the default phonebook if filename not provided") {
	    val name = "Joel Spolsky"
        val number = "555 FOG CREEK"
//        val pb =  mockdir + "/" + samplepb
        // show person not in pb file
        contains(defaultpb, name) should be (false)
        // add person 
        Phonebook.main(Array("add", name, number))
        // show person is in defaultpb file
        contains(defaultpb, name) should be (true)
	}
	test("entries should return the entries in an existing file") {
	    val pb = mockdir + "/" + samplepb
	    val entries = new Phonebook(pb).entries

	    // corresponds to the sample pb entries
	    val expected = Array(("Sarah Ahmed","432 123 4321"), 
            ("Sarah Apple", "509 123 4567"), ("Sarah Orange","123 456 7890"),
            ("Bob Orangutan","345 653 6732"))
            
        entries.size should be (expected.size)
        entries.forall (e => expected.contains((e.name, e.number))) should be (true)
	}
	test("add should leave existing entries as is") {
	    val name = "Big Bird"
        val number = "1 800 SESAMEST"
        val pb =  mockdir + "/" + samplepb
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
	    val pb = mockdir + "/" + samplepb
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
	
	test ("lookup should find person in pb") {
	    
	}
	test ("lookup should return message if not in pb") {
	    
	}
	test ("lookup should return multiple matching entries") {
	    
	}
	test ("reverse-lookup should find person in pb") {
	    
	}
	test ("reverse-lookup should return message if not in pb") {
	    
	}
	test ("reverse-lookup should return multiple matching entries") {
	    
	}
	test("remove should remove person from pb") {
	    
	}
	test("remove should leave remaining people in file") {
	    
	}
	test("remove should return error message if person not found") {
	    
	}
	test("remove should remove from phone trie") {
	    
	}
	test("change should modify file") {
	    
	}
	test("change should modify both tries") {
	    
	}
	test("change should not modify other pb members") {
	    
	}
}
