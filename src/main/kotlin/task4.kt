fun main() {
    println("Enter the number of prime numbers to display:")
    val n = readln().toInt()
    var count = 0
    var number = 2

    while (count < n) {
        var isPrime = true

        for (i in 2..<number) {
            if (number % i == 0) {
                isPrime = false
                break
            }
        }
        if (isPrime) {
            count++
            println("$count-th prime number: $number")
        }

        number++
    }
}