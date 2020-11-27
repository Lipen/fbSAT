package ru.ifmo.fbsat.core.utils

import com.github.lipen.multiarray.IntMultiArray
import ru.ifmo.fbsat.core.automaton.Automaton
import ru.ifmo.fbsat.core.scenario.Scenario

// TODO: call `check` instead of returning Boolean
fun Automaton.checkMapping(
    scenarios: List<Scenario>,
    mapping: IntMultiArray,
): Boolean {
    var isOk = true
    for ((i, scenario) in scenarios.withIndex(start = 1)) {
        val automatonMapping = map(scenario).map { it?.id ?: 0 }
        val assignmentMapping = scenario.elements.map { mapping[it.nodeId!!] }
        if (automatonMapping != assignmentMapping) {
            mylog.error("Scenario $i/${scenarios.size} mapping mismatch:")
            mylog.error("Automaton mapping:  ${automatonMapping.joinToString(" ")}")
            mylog.error("Assignment mapping: ${assignmentMapping.joinToString(" ")}")
            isOk = false
        }
    }
    return isOk
}
