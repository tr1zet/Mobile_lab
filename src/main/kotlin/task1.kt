fun main() {
    println("Enter a positive integer:")
    val number = readln()
    val firstDigit = number.first()
    val lastDigit = number.last()
    val first = firstDigit.toString().toInt()
    val last = lastDigit.toString().toInt()
    val sum = first + last
    println("First digit: $first")
    println("Last digit: $last")
    println("Sum: $first + $last = $sum")
}