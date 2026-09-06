fun main() {
    var count = 0
    var sum = 0.0
    println("Enter numbers (enter 0 to stop):")
    while (true) {
        val input = readln().toDouble()
        if (input == 0.0) {
            break
        }
        count++
        sum += input
    }

    if (count > 0) {
        val average = sum / count
        println("Count of numbers: $count")
        println("Sum: $sum")
        println("Average: $average")
    } else {
        println("No numbers were entered")
    }
}