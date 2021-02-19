import io.threadcso._

class resourceMonitor {

    // number of threads accessing the resource
    private var active = 0

    // total of the ids of processes that are accessing the resource
    private var total = 0

    def enter(id: Int) = synchronized {
        while ((total % 3) != 0) wait()
        active += 1
        total += id
    }

    def exit(id: Int) {
        active -= 1
        total -= id
        notifyAll()
    }
}

