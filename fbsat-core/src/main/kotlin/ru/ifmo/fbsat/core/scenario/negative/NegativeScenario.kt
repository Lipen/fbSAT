package ru.ifmo.fbsat.core.scenario.negative

import ru.ifmo.fbsat.core.automaton.InputEvent
import ru.ifmo.fbsat.core.automaton.InputValues
import ru.ifmo.fbsat.core.automaton.OutputEvent
import ru.ifmo.fbsat.core.automaton.OutputValues
import ru.ifmo.fbsat.core.scenario.InputAction
import ru.ifmo.fbsat.core.scenario.OutputAction
import ru.ifmo.fbsat.core.scenario.Scenario
import ru.ifmo.fbsat.core.scenario.ScenarioElement
import java.io.File

data class NegativeScenario(
    override val elements: List<ScenarioElement>,
    /**
     * One-based index of loop-back state
     */
    val loopPosition: Int?
) : Scenario {
    init {
        if (loopPosition != null) {
            val loop = elements[loopPosition - 1]
            val last = elements.last()
            require(loop.outputEvent == last.outputEvent) {
                "Mismatch of output event (loopBack: ${loop.outputEvent}, last: ${last.outputEvent})"
            }
            require(loop.outputValues == last.outputValues) {
                "Mismatch of output values (loopBack: ${loop.outputValues}, last: ${last.outputValues})"
            }
        }
        require(loopPosition != null) {
            "Loopless counter-examples are not supported yet"
        }

        // println("[*] NegativeScenario with loop at $loopPosition:")
        // elements.forEachIndexed { index, elem ->
        //     println("[${index + 1}/${elements.size}] $elem")
        // }
    }

    override fun toString(): String {
        return "NegativeScenarios(loopPosition=$loopPosition, elements=$elements)"
    }

    companion object {
        fun fromFile(
            file: File,
            inputEvents: List<InputEvent>,
            outputEvents: List<OutputEvent>,
            inputNames: List<String>,
            outputNames: List<String>
        ): List<NegativeScenario> =
            Counterexample.fromFile(file).map {
                it.toNegativeScenario(inputEvents, outputEvents, inputNames, outputNames)
            }
    }
}

fun Counterexample.toNegativeScenario(
    inputEvents: List<InputEvent>,
    outputEvents: List<OutputEvent>,
    inputNames: List<String>,
    outputNames: List<String>
): NegativeScenario {
    require(inputEvents.isNotEmpty())
    require(outputEvents.isNotEmpty())
    require(inputNames.isNotEmpty())
    require(outputNames.isNotEmpty())

    val elements = states.zipWithNext { first, second ->
        ScenarioElement(
            InputAction(
                InputEvent.of(first.getFirstTrue(inputEvents.map { it.name })),
                InputValues(first.getBooleanValues(inputNames))
            ),
            OutputAction(
                OutputEvent.of(second.getFirstTrue(outputEvents.map { it.name })),
                OutputValues(second.getBooleanValues(outputNames))
            )
        ).apply {
            ceState = second.variables["_state"]
        }
    }

    // Note: subtract 1. Just because.
    return NegativeScenario(elements, loopPosition!! - 1)
}
