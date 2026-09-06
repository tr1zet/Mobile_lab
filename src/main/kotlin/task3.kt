fun main() {
    val secretNumber = (0..10).random()
    println("Guess the number from 0 to 10:")

    while (true) {
        val guess = readln().toInt()

        if (guess > secretNumber) {
            println("Too high")
        } else if (guess < secretNumber) {
            println("Too low")
        } else {
            println("You guessed it!")
            break
        }
    }
}