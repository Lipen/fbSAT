package ru.ifmo.fbsat.core.task.modular.extended.consecutive

import ru.ifmo.fbsat.core.automaton.ConsecutiveModularAutomaton
import ru.ifmo.fbsat.core.scenario.positive.ScenarioTree
import ru.ifmo.fbsat.core.task.Inferrer
import ru.ifmo.fbsat.core.task.consecutiveModularExtendedVars
import ru.ifmo.fbsat.core.task.modular.basic.consecutive.consecutiveModularBasic
import ru.ifmo.fbsat.core.task.modular.basic.consecutive.consecutiveModularBasicMinC
import ru.ifmo.fbsat.core.task.optimizeTopDown
import ru.ifmo.fbsat.core.utils.log

fun Inferrer.inferConsecutiveModularExtended(): ConsecutiveModularAutomaton? {
    val rawAssignment = solver.solve() ?: return null
    val vars = solver.consecutiveModularExtendedVars
    val assignment = ConsecutiveModularExtendedAssignment.fromRaw(rawAssignment, vars)
    val automaton = assignment.toAutomaton()

    // TODO: check automaton
    check(true)

    return automaton
}

fun Inferrer.optimizeConsecutiveModularN(start: Int? = null, end: Int = 0): ConsecutiveModularAutomaton? {
    log.info("Optimizing N...")
    val vars = solver.consecutiveModularExtendedVars
    return optimizeTopDown(
        start = start,
        end = end,
        nextInitial = { N ->
            vars.cardinality.updateUpperBoundLessThanOrEqual(N)
            inferConsecutiveModularExtended()
        },
        next = { N ->
            vars.cardinality.updateUpperBoundLessThan(N)
            inferConsecutiveModularExtended()
        },
        query = { it.totalGuardsSize }
    )
}

fun Inferrer.consecutiveModularExtended(
    scenarioTree: ScenarioTree,
    numberOfModules: Int, // M
    numberOfStates: Int, // C
    maxOutgoingTransitions: Int? = null, // K, =C if null
    maxGuardSize: Int, // P
    maxTransitions: Int? = null, // T, unconstrained if null
    maxTotalGuardsSize: Int? = null, // N, unconstrained if null
    isEncodeReverseImplication: Boolean = true
): ConsecutiveModularAutomaton? {
    reset()
    consecutiveModularBasic(
        scenarioTree = scenarioTree,
        numberOfModules = numberOfModules,
        numberOfStates = numberOfStates,
        maxOutgoingTransitions = maxOutgoingTransitions,
        maxTransitions = maxTransitions,
        isEncodeReverseImplication = isEncodeReverseImplication
    )
    solver.declareConsecutiveModularExtended(
        maxGuardSize = maxGuardSize,
        maxTotalGuardsSize = maxTotalGuardsSize
    )
    return inferConsecutiveModularExtended()
}

fun Inferrer.consecutiveModularExtendedMin(
    scenarioTree: ScenarioTree,
    numberOfModules: Int, // M
    numberOfStates: Int? = null, // C_start, 1 if null
    maxGuardSize: Int // P
): ConsecutiveModularAutomaton? {
    consecutiveModularBasicMinC(scenarioTree, numberOfModules, start = numberOfStates ?: 1)
    solver.declareConsecutiveModularExtended(maxGuardSize)
    return optimizeConsecutiveModularN()
}
