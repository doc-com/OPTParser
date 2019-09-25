package com.doc.uimodel

import com.doc.CComplexObjectTypes
import org.apache.xmlbeans.SimpleValue
import org.apache.xmlbeans.XmlObject
import org.openehr.am.archetype.ontology.ConstraintBinding
import org.openehr.rm.datatypes.text.DvText
import org.openehr.schemas.v1.*
import org.openehr.schemas.v1.impl.OPERATIONALTEMPLATEImpl
import java.io.File

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
                val newSection = Section(complexObject, orderInParent)
                if (currentSection == null) sections.add(newSection) else currentSection.addSection(newSection)
                val content: CMULTIPLEATTRIBUTE =
                    CMULTIPLEATTRIBUTE.Factory.parse(complexObject.attributesArray.single { cattribute: CATTRIBUTE? -> cattribute?.rmAttributeName == "content" }.toString())
                processAttribute(content, "", newSection)
            }

            CComplexObjectTypes.OBSERVATION.type -> {
                val newSection = Section(complexObject, orderInParent)
                if (currentSection == null) sections.add(newSection) else currentSection.addSection(newSection)
                val data: CATTRIBUTE =
                    CATTRIBUTE.Factory.parse(complexObject.attributesArray.single { cattribute: CATTRIBUTE? -> cattribute?.rmAttributeName == "data" }.toString())
                val archetypeRoot: CARCHETYPEROOT = CARCHETYPEROOT.Factory.parse(complexObject.toString())
                processAttribute(data, "[${archetypeRoot.archetypeId.value}]", newSection)
            }

            CComplexObjectTypes.SECTION.type -> {
                val newSection = Section(complexObject, orderInParent)
                if (currentSection == null) sections.add(newSection) else currentSection.addSection(newSection)
                val content: CMULTIPLEATTRIBUTE =
                    CMULTIPLEATTRIBUTE.Factory.parse(complexObject.attributesArray.single { cattribute: CATTRIBUTE? -> cattribute?.rmAttributeName == "items" }.toString())
                processAttribute(content, "", newSection)
            }

            CComplexObjectTypes.CLUSTER.type -> {
                val content: CMULTIPLEATTRIBUTE =
                    CMULTIPLEATTRIBUTE.Factory.parse(complexObject.attributesArray.single { cattribute: CATTRIBUTE? -> cattribute?.rmAttributeName == "items" }.toString())
                if (getChildType(complexObject) == CComplexObjectTypes.C_ARCHETYPE_ROOT.type) {
                    val newSection = Section(complexObject, orderInParent)
                    if (currentSection == null) sections.add(newSection) else currentSection.addSection(newSection)
                    processAttribute(content, "", newSection)
                } else {
                    processAttribute(content, "", currentSection)
                }

            }

            CComplexObjectTypes.EVALUATION.type -> {

            }

            CComplexObjectTypes.ACTION.type -> {

            }

            CComplexObjectTypes.INSTRUCTION.type -> {

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

            CComplexObjectTypes.ELEMENT.type -> {
                processElement(complexObject, "$path[${complexObject.nodeId}]", orderInParent, currentSection)
            }

        }
    }

    private fun processAttribute(attribute: CATTRIBUTE, path: String, section: Section?) {
        println(attribute.rmAttributeName)
        var orderInParent = 0
        attribute.childrenArray.forEach {
            val complexObject: CCOMPLEXOBJECT = CCOMPLEXOBJECT.Factory.parse(it.toString())
            processComplexObject(complexObject, "$path/${attribute.rmAttributeName}", orderInParent, section)
            orderInParent++
        }
    }

    private fun processElement(element: CCOMPLEXOBJECT, path: String, orderInParent: Int, section: Section?) {
        if (element.attributesArray.isNotEmpty()) {

            /*element.attributesArray.single { cattribute: CATTRIBUTE? -> cattribute?.rmAttributeName == CComplexObjectTypes.NULL_FLAVOUR.type }
                ?.let {

                }*/

            element.attributesArray.single { cattribute: CATTRIBUTE? -> cattribute?.rmAttributeName == CComplexObjectTypes.VALUE.type }
                ?.let { attr: CATTRIBUTE ->
                    if (attr.childrenArray.isNotEmpty()) {
                        //println(orderInParent);
                        processDataValue(
                            attr.getChildrenArray(0),
                            element.nodeId,
                            "$path/value",
                            orderInParent,
                            section
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
        section: Section?
    ) {
        println(dataValue.rmTypeName)
        println(path)
        val controlBuilder = Control.Builder(dataValue, path, orderInParent, section?.getTerm(nodeId, path)!!)
        when (getChildType(dataValue)) {

            CComplexObjectTypes.C_DV_QUANTITY.type -> {
                controlBuilder.type(ControlType.QUANTITY.type)
                controlBuilder.quantityItems(dataValue)
            }

            CComplexObjectTypes.C_COMPLEX_OBJECT.type -> {
                when (dataValue.rmTypeName) {
                    CComplexObjectTypes.DV_CODED_TEXT.type -> {
                        val complexObject = CCOMPLEXOBJECT.Factory.parse(dataValue.toString())
                        if (complexObject.attributesArray.isNotEmpty()) {
                            if (complexObject.attributesArray[0].rmAttributeName == CComplexObjectTypes.DEFINING_CODE.type) {
                                val codePhrase: CCODEPHRASE =
                                    CCODEPHRASE.Factory.parse(complexObject.attributesArray[0].childrenArray[0].toString())
                                controlBuilder.type(ControlType.INTERNAL_CODED_TEXT.type)
                                controlBuilder.codeList(codePhrase.codeListArray, section.termDefinitions)
                            }
                        }
                    }

                    CComplexObjectTypes.DV_TEXT.type -> {
                        controlBuilder.type(ControlType.FREE_TEXT.type)
                        //val dvText: DvText = DVTEXT.Factory.parse(dataValue.toString())
                        //println(dvText.formatting)
                    }
                }
            }
        }
        section.addControl(controlBuilder.build())
    }

}

//fun toJsonString(): String {
//    val gson: Gson = Gson()
//    return gson.toJson(this)
//}

private fun getChildType(child: COBJECT): String {
    return (child.selectAttribute("http://www.w3.org/2001/XMLSchema-instance", "type") as SimpleValue).stringValue ?: ""
}
