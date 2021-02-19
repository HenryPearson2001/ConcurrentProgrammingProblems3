import io.threadcso._

class PrefixSums(n: Int, a: Array[Int]) {
    require(n == a.size)

    // array where sum is stored - read from then processes synced then updated then processes synced again
    private val sum = new Array[Int](n)

    // create barrier object
    private val barrier = new Barrier(n)

    // a single worker
    private def summer(me: Int) = proc {
        // Invariant: gap = 2r and s = 􏰅 a(me−gap .. me]
        // (with fictious values a(i) = 0 for i < 0). r is the round number.
        var r = 0; var gap = 1; var s = a(me)
        // set initial value of sum and sync
        sum(me) = s
        barrier.sync()
        while (gap < n) {
            // read from array and then sync
            if (gap <= me) {
                val inc = sum(me - gap)
                s = s + inc
            }
            barrier.sync()
            // update the r and gap values
            r += 1; gap += gap
            // update array and sync
            sum(me) = s
            barrier.sync()
        }
    }

    // create and run all the workers
    def apply(): Array[Int] = {
        (|| (for (i <- 0 until n) yield summer(i)))()
        sum
    }


}

import scala.util.Random

object PrefixSumsTest{
  val reps = 10000

  /** Do a single test. */
  def doTest = {
    // Pick random n and array
    val n = 1+Random.nextInt(20)
    val a = Array.fill(n)(Random.nextInt(100))
    // Calculate prefix sums sequentially
    val mySum = new Array[Int](n)
    var s = 0
    for(i <- 0 until n){ s += a(i); mySum(i) = s }
    // Calculate them concurrently
    val sum = new PrefixSums(n, a)()
    // Compare
    assert(sum.sameElements(mySum),
           "a = "+a.mkString(", ")+"\nsum = "+sum.mkString(", ")+
             "\nmySum = "+mySum.mkString(", "))
  }

  def main(args : Array[String]): Unit = {
    for(r <- 0 until reps){ doTest; if(r%100 == 0) print(".") }
    println; exit
  }
}