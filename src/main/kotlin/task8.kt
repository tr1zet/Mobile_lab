class ArrayProcessor(private val numbers: DoubleArray) {

    fun sumPositive(): Double {
        var sum = 0.0
        for (num in numbers) {
            if (num > 0) {
                sum += num
            }
        }
        return sum
    }

    fun product(): Double {
        var result = 1.0
        for (num in numbers) {
            result *= num
        }
        return result
    }

    fun average(): Double {
        var sum = 0.0
        for (num in numbers) {
            sum += num
        }
        return sum / numbers.size
    }
}

fun main() {
    val arr = doubleArrayOf(2.0, -4.0, 5.0, -3.0, 10.0)
    val processor = ArrayProcessor(arr)

    println("Array: ${arr.joinToString()}")
    println("Sum of positive elements: ${processor.sumPositive()}")
    println("Product of all elements: ${processor.product()}")
    println("Average: ${processor.average()}")
}