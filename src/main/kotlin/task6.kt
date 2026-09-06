fun main() {
    val numbers = arrayOf(1, 2, 3, 6, 5, 8, 7, 10, 23)

    println("Array: ${numbers.joinToString()}")

    // цикл for
    var productFor = 1
    var minFor = numbers[0]
    var maxFor = numbers[0]

    for (num in numbers) {
        productFor *= num
        if (num < minFor) minFor = num
        if (num > maxFor) maxFor = num
    }
    println("For loop - Product: $productFor, Min: $minFor, Max: $maxFor")

    // цикл while
    var productWhile = 1
    var minWhile = numbers[0]
    var maxWhile = numbers[0]
    var i = 0

    while (i < numbers.size) {
        productWhile *= numbers[i]
        if (numbers[i] < minWhile) minWhile = numbers[i]
        if (numbers[i] > maxWhile) maxWhile = numbers[i]
        i++
    }
    println("While loop - Product: $productWhile, Min: $minWhile, Max: $maxWhile")

    // цикл forЕach
    var productForEach = 1
    var minForEach = numbers[0]
    var maxForEach = numbers[0]

    numbers.forEach { num ->
        productForEach *= num
        if (num < minForEach) minForEach = num
        if (num > maxForEach) maxForEach = num
    }
    println("ForEach - Product: $productForEach, Min: $minForEach, Max: $maxForEach")

    // цикл reduce
    val productReduce = numbers.reduce { acc, num -> acc * num }
    println("Reduce - Product: $productReduce")

    // макс и мин
    val minFunc = numbers.min()
    val maxFunc = numbers.max()
    println("Min/Max functions - Min: $minFunc, Max: $maxFunc")
}