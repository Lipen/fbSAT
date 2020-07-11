package ru.ifmo.fbsat.core.task.modular.extended.consecutive

import ru.ifmo.fbsat.core.automaton.ConsecutiveModularAutomaton
import ru.ifmo.fbsat.core.scenario.positive.ScenarioTree
import ru.ifmo.fbsat.core.task.Inferrer
import ru.ifmo.fbsat.core.task.consecutiveModularExtendedVars
import ru.ifmo.fbsat.core.task.modular.basic.consecutive.ConsecutiveModularBasicTask
import ru.ifmo.fbsat.core.task.modular.basic.consecutive.consecutiveModularBasicMinC
import ru.ifmo.fbsat.core.task.optimizeConsecutiveModularN
import ru.ifmo.fbsat.core.utils.log

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
    declare(
        ConsecutiveModularBasicTask(
            scenarioTree = scenarioTree,
            numberOfModules = numberOfModules,
            numberOfStates = numberOfStates,
            maxOutgoingTransitions = maxOutgoingTransitions,
            maxTransitions = maxTransitions,
            isEncodeReverseImplication = isEncodeReverseImplication
        )
    )
    declare(
        ConsecutiveModularExtendedTask(
            maxGuardSize = maxGuardSize,
            maxTotalGuardsSize = maxTotalGuardsSize
        )
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
    declare(ConsecutiveModularExtendedTask(maxGuardSize))
    return optimizeConsecutiveModularN()
}

fun Inferrer.inferConsecutiveModularExtended(): ConsecutiveModularAutomaton? {
    val rawAssignment = solver.solve() ?: return null
    val vars = solver.context.consecutiveModularExtendedVars
    val assignment = ConsecutiveModularExtendedAssignment.fromRaw(rawAssignment, vars)
    val automaton = assignment.toAutomaton()

    // TODO: check automaton
    log.warn("Mapping check is not implemented yet")

    return automaton
}
