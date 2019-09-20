package com.doc.uimodel

import com.doc.CComplexObjectTypes
import org.apache.xmlbeans.SimpleValue
import org.openehr.schemas.v1.*
import org.openehr.schemas.v1.impl.OPERATIONALTEMPLATEImpl
import java.io.File

class UITemplate(template: File) {

    private val uid: String
    private val concept: String
    private val templateId: String
    private val language: String
    val sections: MutableList<Section>

    init {
        val base = OPERATIONALTEMPLATE.Factory.parse(template)
        val baseObject = base.selectChildren("http://schemas.openehr.org/v1", "template")[0]
        val opt = OPERATIONALTEMPLATE.Factory.parse(baseObject.toString()) as OPERATIONALTEMPLATEImpl
        uid = opt.uid.value
        concept = opt.concept
        templateId = opt.templateId.value
        language = opt.description.detailsArray[0].language.codeString
        sections = mutableListOf()
        create(opt.definition)
    }

    private fun create(definition: CCOMPLEXOBJECT) {
        processComplexObject(definition, "", 0, null)
    }

    private fun processComplexObject(
        complexObject: CCOMPLEXOBJECT,
        path: String,
        orderInParent: Int,
        currentSection: Section?
    ) {
        //println(complexObject.rmTypeName)
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
                processAttribute(data, "", newSection)
            }

            CComplexObjectTypes.SECTION.type -> {

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
            element.attributesArray.single { cattribute: CATTRIBUTE? -> cattribute?.rmAttributeName == "value" }
                ?.let { attr: CATTRIBUTE ->
                    if (attr.childrenArray.isNotEmpty()) {
                        println(orderInParent);
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
            CComplexObjectTypes.CDVQUANTITY.type -> {
                controlBuilder.type(ControlType.QUANTITY.type)
                controlBuilder.quantityItems(dataValue)
            }
            CComplexObjectTypes.CCOMPLEXOBJECT.type -> {
                when (dataValue.rmTypeName) {
                    CComplexObjectTypes.DVCODEDTEXT.type -> {

                    }
                }
            }
        }
        section.addControl(controlBuilder.build())
    }

    private fun getChildType(child: COBJECT): String {
        return (child.selectAttribute("http://www.w3.org/2001/XMLSchema-instance", "type") as SimpleValue).stringValue
    }
}