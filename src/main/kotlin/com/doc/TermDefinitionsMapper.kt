package com.doc

import org.openehr.schemas.v1.ARCHETYPETERM
import org.openehr.schemas.v1.CARCHETYPEROOT
import org.openehr.schemas.v1.CCOMPLEXOBJECT

class TermDefinitionsMapper {

    companion object {

        fun mapDefinitions(complexObject: CCOMPLEXOBJECT): Map<String, TermDefinition> {
            val archetypeRoot: CARCHETYPEROOT = CARCHETYPEROOT.Factory.parse(complexObject.toString())
            val termDescription: MutableMap<String, TermDefinition> = mutableMapOf()
            archetypeRoot.termDefinitionsArray.forEach { archetypeTerm: ARCHETYPETERM ->
                var description = ""
                var text = ""
                var comment = ""
                archetypeTerm.itemsArray.forEach {
                    when (it.id) {
                        "description" -> description = it.stringValue
                        "text" -> text = it.stringValue
                        "comment" -> comment = it.stringValue
                    }
                }
                termDescription[archetypeTerm.code] =
                    TermDefinition(text, description, comment)
            }
            return termDescription
        }
    }
}