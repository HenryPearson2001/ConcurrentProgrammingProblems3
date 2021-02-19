import io.threadcso._

class resourceMonitor {

    // total of the ids of processes that are accessing the resource
    private var total = 0

    def enter(id: Int) = synchronized {
        while ((total % 3) != 0) wait()
        total += id
    }

    def exit(id: Int) = synchronized {
        total -= id
        notifyAll()
    }
}

import ox.cads.testing._
import scala.collection.immutable.Set

object resourceMonitorTest {

    type SeqDatabase = Set[Int]
    type ConcDatabase = resourceMonitor

    // number of readers
    var N = 5

    // functions for sequential implementation
    // precondition that the sum of ids must be a multiple of 3
    def seqEnter(me: Int)(set: Set[Int]) : (Unit, Set[Int]) = {
        require(set.sum % 3 == 0)
        ((), set.incl(me))
    }

    def seqExit(me: Int)(set: Set[Int]) : (Unit, Set[Int]) = {
        ((), set.excl(me))
    }

    def reader(me: Int, log: GenericThreadLog[SeqDatabase, ConcDatabase]) = {
        for (i <- 0 until 10) {
            // attempt to access the resource
            log.log(_.enter(me), "Entering", seqEnter(me))
            // attempt to exit accessing the resource
            log.log(_.exit(me), "Exiting", seqExit(me))
        }
    }

    def doTest = {
        // concurrent resource monitor object
        val concDatabase = new resourceMonitor
        // set to store which processes are active for sequential part of test
        val seqDatabase: Set[Int] = Set()
        val tester = LinearizabilityTester.JITGraph[SeqDatabase, ConcDatabase](seqDatabase, concDatabase, N, reader _, 20)
        assert(tester() > 0)
    }

    def main(args: Array[String]) = {
        for(i <- 0 until 1000){
            doTest
            if(i%10 == 0) print(".")
        }
        println; io.threadcso.exit
    }
}