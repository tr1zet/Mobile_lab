fun main() {
    val numbers = arrayOf(1, 2, 3, 6, 5, 8, 7, 0, 10, 23)

    println("Array: ${numbers.joinToString()}")

    //Способ 1: Цикл for
    println("\n Method for")
    for (i in 1 until numbers.size - 1) {
        if (numbers[i] > numbers[i - 1] && numbers[i] > numbers[i + 1]) {
            println(numbers[i])
        }
    }

    //Способ 2: Цикл while
    println("\n Method while")
    var i = 1
    while (i < numbers.size - 1) {
        if (numbers[i] > numbers[i - 1] && numbers[i] > numbers[i + 1]) {
            println(numbers[i])
        }
        i++
    }

    //Способ 3: forEach
    println("\n Method forEach")
    numbers.forEachIndexed { index, value ->
        if (index > 0 && index < numbers.size - 1) {
            if (value > numbers[index - 1] && value > numbers[index + 1]) {
                println(value)
            }
        }
    }
}