fun sqr(n: Double): Double {
    return n * n
}

fun discriminant(a: Double, b: Double, c: Double): Double {
    return sqr(b) - 4 * a * c
}

fun rootsNumber(a: Double, b: Double, c: Double): Int {
    val d = discriminant(a, b, c)

    return when {
        d > 0 -> 2
        d == 0.0 -> 1
        else -> 0
    }
}

fun quadraticRoot(a: Double, b: Double, c: Double) {
    val d = discriminant(a, b, c)
    val roots = rootsNumber(a, b, c)

    println("Equation: ${a}x^2 + ${b}x + $c = 0")
    println("Discriminant: $d")
    println("Number of roots: $roots")

    when {
        roots == 2 -> {
            val x1 = (-b + Math.sqrt(d)) / (2 * a)
            val x2 = (-b - Math.sqrt(d)) / (2 * a)
            println("Root 1: $x1")
            println("Root 2: $x2")
        }
        roots == 1 -> {
            val x = -b / (2 * a)
            println("Root: $x")
        }
        else -> {
            println("No real roots")
        }
    }
}

fun main() {
    println("Enter coefficients a, b, c:")
    println("a:")
    val a = readln().toDouble()
    println("b:")
    val b = readln().toDouble()
    println("c:")
    val c = readln().toDouble()

    quadraticRoot(a, b, c)
}