fun main() {
        val numbers = arrayOf(1, 2, 3, 6, 5, 8, 7, 0, 10, 23)

    println("Array: ${numbers.joinToString()}")
    println("Elements greater than neighbors:")

    for (i in 1 until numbers.size - 1) {
        if (numbers[i] > numbers[i - 1] && numbers[i] > numbers[i + 1]) {
            println(numbers[i])
        }
    }
}