package com.doc.uimodel

import com.doc.CComplexObjectTypes
import org.apache.xmlbeans.SimpleValue
import org.apache.xmlbeans.XmlObject
import org.openehr.am.archetype.ontology.ConstraintBinding
import org.openehr.rm.datatypes.text.DvText
import org.openehr.schemas.v1.*
import org.openehr.schemas.v1.impl.CDVORDINALImpl
import org.openehr.schemas.v1.impl.OPERATIONALTEMPLATEImpl
import java.io.File
import java.net.URI

class UITemplate(template: File) {

    private val uid: String
    private val concept: String
    private val templateId: String
    private val language: String
    private val sections: MutableList<Section>
    private val viewConstraints: MutableMap<String, ViewConstraint>

    init {
        val base = OPERATIONALTEMPLATE.Factory.parse(template)
        val baseObject = base.selectChildren("http://schemas.openehr.org/v1", "template")[0]
        val opt = OPERATIONALTEMPLATE.Factory.parse(baseObject.toString()) as OPERATIONALTEMPLATEImpl
        uid = opt.uid.value
        concept = opt.concept
        templateId = opt.templateId.value
        language = opt.description.detailsArray[0].language.codeString
        sections = mutableListOf()
        viewConstraints = mutableMapOf()
        mapViewConstraints(opt)
        create(opt.definition)
    }

    private fun create(definition: CCOMPLEXOBJECT) {
        processComplexObject(definition, "", 0, null)
    }

    private fun mapViewConstraints(opt: XmlObject) {
        val viewElementArray = opt.selectChildren("http://schemas.openehr.org/v1", "view")
        if (viewElementArray.isNotEmpty()) {
            val viewElement = viewElementArray[0]
            val constraintArray = viewElement.selectChildren("http://schemas.openehr.org/v1", "constraints")
            constraintArray.forEach { constraint: XmlObject ->
                viewConstraints[(constraint.selectAttribute("", "path") as SimpleValue).stringValue] =
                    ViewConstraint(
                        (constraint.selectChildren(
                            "http://schemas.openehr.org/v1",
                            "items"
                        )[0].selectAttribute("", "id") as SimpleValue).stringValue, (constraint.selectChildren(
                            "http://schemas.openehr.org/v1",
                            "items"
                        )[0].selectChildren("http://schemas.openehr.org/v1", "value")[0] as SimpleValue).stringValue
                    )
            }
        }
    }

    private fun processComplexObject(
        complexObject: CCOMPLEXOBJECT,
        path: String,
        orderInParent: Int,
        currentSection: Section?
    ) {


        if (getChildType(complexObject) == CComplexObjectTypes.ARCHETYPE_SLOT.type) return

        when (complexObject.rmTypeName) {
            CComplexObjectTypes.COMPOSITION.type -> {
                val newSection = Section(complexObject, orderInParent, currentSection?.termDefinitions)
                if (currentSection == null) sections.add(newSection) else currentSection.addSection(newSection)
                val content: CMULTIPLEATTRIBUTE =
                    CMULTIPLEATTRIBUTE.Factory.parse(complexObject.attributesArray.single { cattribute: CATTRIBUTE? -> cattribute?.rmAttributeName == "content" }.toString())
                processAttribute(content, "", newSection)
            }

            CComplexObjectTypes.OBSERVATION.type -> {
                val newSection = Section(complexObject, orderInParent, currentSection?.termDefinitions)
                if (currentSection == null) sections.add(newSection) else currentSection.addSection(newSection)
                val data: CATTRIBUTE =
                    CATTRIBUTE.Factory.parse(complexObject.attributesArray.single { cattribute: CATTRIBUTE? -> cattribute?.rmAttributeName == "data" }.toString())
                val archetypeRoot: CARCHETYPEROOT = CARCHETYPEROOT.Factory.parse(complexObject.toString())
                processAttribute(data, "[${archetypeRoot.archetypeId.value}]", newSection)
            }

            CComplexObjectTypes.SECTION.type -> {
                val newSection = Section(complexObject, orderInParent, currentSection?.termDefinitions)
                if (currentSection == null) sections.add(newSection) else currentSection.addSection(newSection)

                val content: CMULTIPLEATTRIBUTE =
                    CMULTIPLEATTRIBUTE.Factory.parse(complexObject.attributesArray.single { cattribute: CATTRIBUTE? -> cattribute?.rmAttributeName == "items" }.toString())
                processAttribute(content, "$path[${complexObject.nodeId}]", newSection)
            }

            CComplexObjectTypes.CLUSTER.type -> {
                val newSection = Section(complexObject, orderInParent, currentSection?.termDefinitions)
                if (currentSection == null) sections.add(newSection) else currentSection.addSection(newSection)
                val content: CMULTIPLEATTRIBUTE =
                    CMULTIPLEATTRIBUTE.Factory.parse(complexObject.attributesArray.single { cattribute: CATTRIBUTE? -> cattribute?.rmAttributeName == "items" }.toString())
                processAttribute(content, "$path[${complexObject.nodeId}]", newSection)
            }

            CComplexObjectTypes.EVALUATION.type -> {
                val newSection = Section(complexObject, orderInParent, currentSection?.termDefinitions)
                if (currentSection == null) sections.add(newSection) else currentSection.addSection(newSection)
                val content: CSINGLEATTRIBUTE =
                    CSINGLEATTRIBUTE.Factory.parse(complexObject.attributesArray.single { cattribute: CATTRIBUTE? -> cattribute?.rmAttributeName == "data" }.toString())
                processAttribute(content, "$path[${complexObject.nodeId}]", newSection)
            }

            CComplexObjectTypes.ACTIVITY.type -> {
                val newSection = Section(complexObject, orderInParent, currentSection?.termDefinitions)
                if (currentSection == null) sections.add(newSection) else currentSection.addSection(newSection)
                val content: CSINGLEATTRIBUTE =
                    CSINGLEATTRIBUTE.Factory.parse(complexObject.attributesArray.single { cattribute: CATTRIBUTE? -> cattribute?.rmAttributeName == "description" }.toString())
                processAttribute(content, "$path[${complexObject.nodeId}]", newSection)
            }

            CComplexObjectTypes.ACTION.type -> {
                val newSection = Section(complexObject, orderInParent, currentSection?.termDefinitions)
                if (currentSection == null) sections.add(newSection) else currentSection.addSection(newSection)
                val content: CSINGLEATTRIBUTE =
                    CSINGLEATTRIBUTE.Factory.parse(complexObject.attributesArray.single { cattribute: CATTRIBUTE? -> cattribute?.rmAttributeName == "description" }.toString())
                processAttribute(content, "$path[${complexObject.nodeId}]", newSection)
            }

            CComplexObjectTypes.INSTRUCTION.type -> {
                val newSection = Section(complexObject, orderInParent, currentSection?.termDefinitions)
                if (currentSection == null) sections.add(newSection) else currentSection.addSection(newSection)
                val content: CMULTIPLEATTRIBUTE =
                    CMULTIPLEATTRIBUTE.Factory.parse(complexObject.attributesArray.single { cattribute: CATTRIBUTE? -> cattribute?.rmAttributeName == "activities" }.toString())
                processAttribute(content, "$path[${complexObject.nodeId}]", newSection)
            }

            CComplexObjectTypes.HISTORY.type -> {
                complexObject.attributesArray.forEach {
                    processAttribute(it, "$path[${complexObject.nodeId}]", currentSection)
                }
            }

            CComplexObjectTypes.POINT_EVENT.type -> {
                complexObject.attributesArray.forEach {
                    processAttribute(it, "$path[${complexObject.nodeId}]", currentSection)
                }
            }

            CComplexObjectTypes.EVENT.type -> {
                complexObject.attributesArray.forEach {
                    processAttribute(it, "$path[${complexObject.nodeId}]", currentSection)
                }
            }

            CComplexObjectTypes.ITEM_TREE.type -> {
                complexObject.attributesArray.forEach {
                    processAttribute(it, "$path[${complexObject.nodeId}]", currentSection)
                }
            }

            CComplexObjectTypes.ITEM_LIST.type -> {
                complexObject.attributesArray.forEach {
                    processAttribute(it, "$path[${complexObject.nodeId}]", currentSection)
                }
            }

            CComplexObjectTypes.ITEM_STRUCTURE.type -> {
                complexObject.attributesArray.forEach {
                    processAttribute(it, "$path[${complexObject.nodeId}]", currentSection)
                }
            }

            CComplexObjectTypes.ELEMENT.type -> {
                processElement(complexObject, "$path[${complexObject.nodeId}]", orderInParent, currentSection)
            }

        }
    }

    private fun processAttribute(attribute: CATTRIBUTE, path: String, section: Section?) {
        //println(attribute.rmAttributeName)
        var orderInParent = 0
        attribute.childrenArray.forEach {
            val complexObject: CCOMPLEXOBJECT = CCOMPLEXOBJECT.Factory.parse(it.toString())
            processComplexObject(complexObject, "$path/${attribute.rmAttributeName}", orderInParent, section)
            orderInParent++
        }
    }

    private fun processElement(element: CCOMPLEXOBJECT, path: String, orderInParent: Int, section: Section?) {
        if (element.attributesArray.isNotEmpty()) {

            var nullFlavour: CATTRIBUTE? = null
            val nullFlavourAttribute =
                element.attributesArray.filter { cattribute: CATTRIBUTE? -> cattribute?.rmAttributeName == CComplexObjectTypes.NULL_FLAVOUR.type }
            if (!nullFlavourAttribute.isNullOrEmpty()) {
                nullFlavour = nullFlavourAttribute[0]
            }

            element.attributesArray.single { cattribute: CATTRIBUTE? -> cattribute?.rmAttributeName == CComplexObjectTypes.VALUE.type }
                ?.let { attr: CATTRIBUTE ->
                    if (attr.childrenArray.isNotEmpty()) {
                        //println(orderInParent);
                        processDataValue(
                            attr.getChildrenArray(0),
                            element.nodeId,
                            "$path/value",
                            orderInParent,
                            section,
                            nullFlavour
                        )
                    }
                }
        }
    }

    private fun processDataValue(
        dataValue: COBJECT,
        nodeId: String,
        path: String,
        orderInParent: Int,
        section: Section?,
        nullFlavour: CATTRIBUTE?
    ) {
        //println(dataValue.rmTypeName)
        //println(path)
        val controlBuilder = Control.Builder(dataValue, path, orderInParent, section?.getTerm(nodeId, path)!!)
        when (getChildType(dataValue)) {

            CComplexObjectTypes.C_DV_QUANTITY.type -> {
                controlBuilder.type(ControlType.QUANTITY.type)
                controlBuilder.quantityItems(dataValue)
            }

            CComplexObjectTypes.C_DV_ORDINAL.type -> {
                controlBuilder.type(ControlType.ORDINAL.type)
                controlBuilder.ordinalList(CDVORDINAL.Factory.parse(dataValue.toString()), section.termDefinitions)
            }

            CComplexObjectTypes.C_COMPLEX_OBJECT.type -> {
                val complexObject = CCOMPLEXOBJECT.Factory.parse(dataValue.toString())
                when (dataValue.rmTypeName) {
                    CComplexObjectTypes.DV_CODED_TEXT.type -> {
                        controlBuilder.codedText(complexObject, section)
                    }

                    CComplexObjectTypes.DV_TEXT.type -> {
                        controlBuilder.type(ControlType.FREE_TEXT.type)
                    }

                    CComplexObjectTypes.DV_COUNT.type -> {
                        controlBuilder.type(ControlType.COUNT.type)
                        controlBuilder.range(complexObject)
                    }

                    CComplexObjectTypes.DV_DATE_TIME.type -> {
                        controlBuilder.type(ControlType.DATETIME.type)
                        controlBuilder.datePattern(complexObject)
                    }

                    CComplexObjectTypes.DV_DATE.type -> {
                        controlBuilder.type(ControlType.DATE.type)
                        controlBuilder.datePattern(complexObject)
                    }

                    CComplexObjectTypes.DV_BOOLEAN.type -> {
                        controlBuilder.type(ControlType.BOOLEAN_CHECK.type)
                    }

                    CComplexObjectTypes.DV_DURATION.type -> {
                        controlBuilder.type(ControlType.DURATION.type)
                        controlBuilder.range(complexObject)
                    }

                    CComplexObjectTypes.DV_PARSABLE.type -> {
                        controlBuilder.type(ControlType.PARSABLE.type)
                        controlBuilder.formalism(complexObject)
                    }
                }
            }

        }

        section.addControl(controlBuilder.build())
    }

}

private fun getChildType(child: COBJECT): String {
    child.selectAttribute("http://www.w3.org/2001/XMLSchema-instance", "type")?.let {
        return (it as SimpleValue).stringValue
    }
    return ""
}
