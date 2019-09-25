package com.doc.uimodel

import com.doc.TermDefinition
import com.doc.TermDefinitionsMapper
import org.openehr.schemas.v1.CARCHETYPEROOT
import org.openehr.schemas.v1.CCOMPLEXOBJECT
import org.openehr.schemas.v1.IntervalOfInteger

class Section(complexObject: CCOMPLEXOBJECT, orderInParent: Int) {

    private val header: String?
    val controls: MutableList<Control>
    val sections: MutableList<Section>
    private val occurrences: IntervalOfInteger
    val termDefinitions: Map<String, TermDefinition>
    private val orderInParent: Int

    init {
        val archetypeRoot: CARCHETYPEROOT = CARCHETYPEROOT.Factory.parse(complexObject.toString())
        termDefinitions = TermDefinitionsMapper.mapDefinitions(complexObject)
        header = termDefinitions[archetypeRoot.nodeId]?.text
        occurrences = complexObject.occurrences
        controls = mutableListOf()
        sections = mutableListOf()
        this.orderInParent = orderInParent
    }

    fun addSection(section: Section) {
        sections.add(section)
    }

    fun addControl(control: Control) {
        controls.add(control)
    }

    fun getTerm(key: String, path: String): TermDefinition {
        return termDefinitions[key]
            ?: error("Term Definition not found for $key in element $path/nTermDefinitions ->/n$termDefinitions")
    }
}