package ru.ifmo.fbsat.core.solver

import ru.ifmo.fbsat.core.utils.pairs

fun Solver.atLeastOne(literals: Iterable<Int>) {
    clause(literals)
}

fun Solver.atMostOne(literals: Iterable<Int>) {
    for ((a, b) in literals.pairs())
        clause(-a, -b)
}

fun Solver.exactlyOne(literals: Iterable<Int>) {
    atLeastOne(literals)
    atMostOne(literals)
}

/**
 * [lhs] => [rhs]
 */
fun Solver.imply(lhs: Int, rhs: Int) {
    clause(-lhs, rhs)
}

/**
 * [lhs] => AND([rhs])
 */
fun Solver.implyAnd(lhs: Int, rhs: Iterable<Int>) {
    for (x in rhs)
        imply(lhs, x)
}

/**
 * [lhs] => OR([rhs])
 */
fun Solver.implyOr(lhs: Int, rhs: Iterable<Int>) {
    clause {
        yield(-lhs)
        for (x in rhs)
            yield(x)
    }
}

/**
 * [x1] => ([x2] => [x3])
 */
fun Solver.implyImply(x1: Int, x2: Int, x3: Int) {
    clause()
}

/**
 * [x1] => ([x2] <=> [x3])
 */
fun Solver.implyIff(x1: Int, x2: Int, x3: Int) {
    clause(-x1, -x2, x3)
    clause(-x1, x2, -x3)
}

/**
 * [x1] => ([x2] => ([x3] <=> [x4]))
 */
fun Solver.implyImplyIff(x1: Int, x2: Int, x3: Int, x4: Int) {
    clause(-x1, -x2, -x3, x4)
    clause(-x1, -x2, x3, -x4)
}

/**
 * [x1] => ([x2] => ([x3] <=> AND([xs]))
 */
fun Solver.implyImplyIffAnd(x1: Int, x2: Int, x3: Int, xs: Iterable<Int>) {
    clause {
        yield(-x1)
        yield(-x2)
        yield(x3)
        for (x in xs) {
            clause(-x1, -x2, -x3, x)
            yield(-x)
        }
    }
}

/**
 * [x1] => ([x2] => ([x3] <=> OR([xs])))
 */
fun Solver.implyImplyIffOr(x1: Int, x2: Int, x3: Int, xs: Iterable<Int>) {
    clause {
        yield(-x1)
        yield(-x2)
        yield(-x3)
        for (x in xs) {
            clause(-x1, -x2, x3, -x)
            yield(x)
        }
    }
}

/**
 * [x1] => ([x2] <=> AND([xs]))
 */
fun Solver.implyIffAnd(x1: Int, x2: Int, xs: Iterable<Int>) {
    clause {
        yield(-x1)
        yield(x2)
        for (x in xs) {
            clause(-x1, -x2, x)
            yield(-x)
        }
    }
}

/**
 * [x1] => ([x2] <=> OR([xs]))
 */
fun Solver.implyIffOr(x1: Int, x2: Int, xs: Iterable<Int>) {
    clause {
        yield(-x1)
        yield(-x2)
        for (x in xs) {
            clause(-x1, x2, -x)
            yield(x)
        }
    }
}

/**
 * [lhs] <=> [rhs]
 */
fun Solver.iff(lhs: Int, rhs: Int) {
    imply(lhs, rhs)
    imply(rhs, lhs)
}

/**
 * [lhs] <=> AND([rhs])
 */
fun Solver.iffAnd(lhs: Int, rhs: Iterable<Int>) {
    clause {
        yield(lhs)
        for (x in rhs) {
            clause(-lhs, x)
            yield(-x)
        }
    }
}

/**
 * [lhs] <=> OR([rhs])
 */
fun Solver.iffOr(lhs: Int, rhs: Iterable<Int>) {
    clause {
        yield(-lhs)
        for (x in rhs) {
            clause(lhs, -x)
            yield(x)
        }
    }
}

/**
 * [aux] <=> ([lhs] => [rhs])
 */
fun Solver.iffImply(aux: Int, lhs: Int, rhs: Int) {
    clause(-aux, -lhs, rhs)
    clause(aux, lhs)
    clause(aux, -rhs)
}
