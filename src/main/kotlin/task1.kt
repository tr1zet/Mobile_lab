fun main() {
    println("Enter a positive integer:")
    val input = readln()
    val number = input.toInt()

    //Способ 1: Через toInt(), % и /
    val lastDigit1 = number % 10
    

    var temp = number
    var firstDigit1 = 0
    while (temp > 0) {
        firstDigit1 = temp % 10
        temp /= 10
    }
    
    val sum1 = firstDigit1 + lastDigit1
    println("First digit: $firstDigit1")
    println("Last digit: $lastDigit1")
    println("Sum: $firstDigit1 + $lastDigit1 = $sum1")
    
    //Способ 2: Через first() и last() 
    val firstDigit2 = input.first().toString().toInt()
    val lastDigit2 = input.last().toString().toInt()
    val sum2 = firstDigit2 + lastDigit2
    
    println("First digit: $firstDigit2")
    println("Last digit: $lastDigit2")
    println("Sum: $firstDigit2 + $lastDigit2 = $sum2")
}