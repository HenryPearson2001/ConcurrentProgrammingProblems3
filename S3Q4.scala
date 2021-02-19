import io.threadcso._

class GridMax(n: Int, xss: Array[Array[Int]]) {
    require(n >= 1 && xss.length == n && xss.forall(xs =>  xs.length == n))

    // channels for communication up and to the right
    private val upChannels, rightChannels = Array.fill(n)(Array.fill(n)(OneOneBuf[Int](1)))

    // output array
    private val outputArray = Array.fill(n)(Array.fill(n)(0))

    // barrier process
    private val barrier = new Barrier(n * n)

    // pass max value to the right n times and update your own max, and then pass up and n times and update your max
    // (synced by barrier process)
    def worker(i: Int, j: Int, x: Int, readUp: ?[Int], writeUp: ![Int], readRight: ?[Int], writeRight: ![Int]) = proc{
        // initially set the max value to x
        var max = x; var count = 0
        // send to the right n times
        while (count < n) {
            // send value to right
            writeRight!(max)
            // receive value from left and update max
            val y = readRight?()
            if (y > max) max = y
            count += 1
            barrier.sync()
        }
        count = 0
        // send up n times
        while (count < n) {
            // send value up
            writeUp!(max)
            // receive value from below and update max
            val y = readUp?()
            if (y > max) max = y
            count += 1
            barrier.sync()
        }
        // write to outputArray
        outputArray(i)(j) = max
    }


    // Run the system, and return array storing results obtained.
    def apply(): Array[Array[Int]] = {
        // create workers
        val workers = || (for (i <- 0 until n; j <- 0 until n) yield worker(i, j, xss(i)(j), upChannels(i)(j), upChannels((i + 1) % n)(j), rightChannels(i)(j), rightChannels(i)((j + 1) % n)))
        run (workers)
        outputArray
    }
}

import scala.util.Random

/** Test for GridMax and LogGridMax. */
object GridMaxTest{
  /** Run a single test.
    * @param useLog should the logarithmic version be used? */
  def doTest(useLog: Boolean) = {
    val n = 1+Random.nextInt(10)
    val xss = Array.fill[Int](n, n)(Random.nextInt(1000))
    val results = new GridMax(n, xss)() //if(useLog) new LogGridMax(n, xss)() else new GridMax(n, xss)()
    val expected = xss.map(_.max).max
    assert(results.forall(_.forall(_ == expected)))
  }

  /** Main method. */
  def main(args: Array[String]) = {
    val useLog = args.nonEmpty && args(0) == "--useLog"
    for(i <- 0 until 10000){
      doTest(useLog)
      if(i%100 == 0) print(".")
    }
    println; io.threadcso.exit
  }
}