# ConcurrentProgrammingProblems3
University concurrent programming problems

Problems:

Question 3
[Programming] Re-implement the prefix sum example, so as to use shared variables, rather than message-passing. You need to think carefully how to avoid race conditions.
Test your code by adapting the test harness for the prefix sums example from lectures.
1
Question 4 [Programming]
(a) Suppose n2 worker processes are arranged in a n by n toroidal grid. Each process starts with an integer value x. The aim is for each process to end up in possession of the maximum of these n2 values. Each process may send messages to the processes above it and to the right of it in the grid (where we treat the bottom row as being above the top row, and the left hand column as being to the right of the right hand column).
Write code solving this problem, making use of a worker process:
def worker(i: Int, j: Int, x: Int, readUp: ?[Int], writeUp: ![Int], readRight: ?[Int], writeRight: ![Int]) = proc{ ... }
Give your code the following signature, where xss gives the values initially held by the processes:
class GridMax(n: Int, xss: Array[Array[Int]]){
require(n >= 1 && xss.length == n && xss.forall( .length == n))
/∗∗ Run the system, and return array storing results obtained. ∗/
def apply(): Array[Array[Int]] = ... }
Test your code using the test harness on the course website.
(b) Now consider the same scenario as in the previous part, except assume each process can send messages to any other processes. We now want a solution that takes O(log n) rounds.
Write code to solve this problem, making use of a worker process:
def worker(i: Int, j: Int, x: Int, read: ?[Int], write: List[List[![Int]]]) = proc{ ... }
The process can send values to another process (i1, j1) on the channel write(i1)(j1), and can receive messages from other processes on the channel read. You should briefly explain your solution. Again, test your code using the test harness provided.
What is the total running time of the program (in Θ( ) notation), assuming as many processors as processes?
Question 7
[Programming] Consider the following synchronisation problem. Each process has an integer-valued identity. A particular resource should be accessed according to the following constraint: a new process can start using the resource only if the sum of the identities of those processes currently using it is divisible by 3. Implement a monitor with procedures enter(id: Int) and exit(id: Int) to enforce this.
Write a testing framework for your monitor using the linearizability framework. Hint: one approach is to use a Set[Int] as the sequential specification object, representing the set of identities of processes currently using the resource, and to include a suitable precondition for the sequential version of enter.
