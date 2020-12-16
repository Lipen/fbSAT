package ru.ifmo.fbsat.core.task.single.extended

import com.github.lipen.satlib.card.Cardinality
import com.github.lipen.satlib.solver.Solver
import ru.ifmo.fbsat.core.constraints.declareGuardConditionsAdhocConstraints
import ru.ifmo.fbsat.core.constraints.declareGuardConditionsBfsConstraints
import ru.ifmo.fbsat.core.constraints.declarePositiveGuardConditionsConstraints
import ru.ifmo.fbsat.core.task.Task
import ru.ifmo.fbsat.core.utils.Globals

data class ExtendedTask(
    val maxGuardSize: Int, // P
    val maxTotalGuardsSize: Int? = null, // N, unconstrained if null
) : Task() {
    override fun Solver.declare_() {
        /* Variables */
        comment("$name: Variables")
        declareExtendedVariables(
            P = maxGuardSize
        )

        /* Constraints */
        comment("$name: Constraints")
        declarePositiveGuardConditionsConstraints()
        if (Globals.IS_BFS_GUARD) declareGuardConditionsBfsConstraints()
        declareGuardConditionsAdhocConstraints()

        /* Initial cardinality constraints */
        comment("$name: Initial cardinality (N) constraints")
        val cardinalityN: Cardinality = context["cardinalityN"]
        cardinalityN.declareUpperBoundLessThanOrEqual(maxTotalGuardsSize)
    }
}
