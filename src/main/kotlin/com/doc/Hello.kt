package com.doc

import com.doc.Constants.Companion.typeValuesMap
import com.doc.uimodel.UITemplate
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import openEHR.v1.template.COMPOSITION
import org.apache.xmlbeans.SimpleValue
import org.openehr.schemas.v1.*
import org.openehr.schemas.v1.impl.IntervalOfDateTimeImpl
import org.openehr.schemas.v1.impl.IntervalOfIntegerImpl
import java.io.File

var termDescription = mutableMapOf<String, TermDefinition>()

fun getClassSimpleName(any: Any): String? {
    return any::class.simpleName
}

fun getChildType(child: COBJECT): String {
    return (child.selectAttribute("http://www.w3.org/2001/XMLSchema-instance", "type") as SimpleValue).stringValue
}

fun main(args: Array<String>) {
    val file = File("src/main/resources/encounter_test.opt")
    val uiTemplate = UITemplate(file)
    //println(uiTemplate.sections[0].sections[0].sections.size)
    //println(uiTemplate.sections[0].sections[0].controls.size)
    val gsonBuilder: GsonBuilder = GsonBuilder().setPrettyPrinting()
    gsonBuilder.registerTypeAdapter(IntervalOfInteger::class.java, IntervalOfIntegerAdapter())
    gsonBuilder.registerTypeAdapter(IntervalOfIntegerImpl::class.java, IntervalOfIntegerImplAdapter())
    gsonBuilder.registerTypeAdapter(IntervalOfDateTimeImpl::class.java, IntervalOfDateTimeImplAdapter())
    //gsonBuilder.registerTypeAdapter(IntervalOfReal::class.java, IntervalOfRealAdapter())
    gsonBuilder.registerTypeAdapter(CQUANTITYITEM::class.java, CQuantityItemAdapter())

    val gson: Gson = gsonBuilder.create()
    println(gson.toJson(uiTemplate))
}

fun findDefinitionElements(root: CARCHETYPEROOT) {
    if (root.rmTypeName == COMPOSITION::class.simpleName) {
        val content: CMULTIPLEATTRIBUTE =
            CMULTIPLEATTRIBUTE.Factory.parse(root.attributesArray.single { cattribute: CATTRIBUTE? -> cattribute?.rmAttributeName == "content" }.toString())
        findContentElementsRecursively(content)
    }
}

fun findContentElementsRecursively(content: CMULTIPLEATTRIBUTE) {
    content.childrenArray.forEach {
        processChild(it, "")
    }
}

fun processArchetypeRoot(root: CARCHETYPEROOT) {

    root.attributesArray.forEach {
        processAttribute(it, "")
    }

}

fun processChild(child: COBJECT, path: String) {
    val type = getChildType(child)
    //println("$type -> ${child.rmTypeName}")
    //println(path)
    when (type) {
        typeValuesMap[CARCHETYPEROOT::class.simpleName] -> {
            processArchetypeRoot(CARCHETYPEROOT.Factory.parse(child.toString()))
        }
        typeValuesMap[CCOMPLEXOBJECT::class.simpleName] -> {
            processComplexObject(CCOMPLEXOBJECT.Factory.parse(child.toString()), path)
        }

    }
}

fun processComplexObject(complexObject: CCOMPLEXOBJECT, path: String) {
    val newPath = if (complexObject.nodeId == "") path else "$path[${complexObject.nodeId}]"
    if (complexObject.rmTypeName == ELEMENT::class.simpleName) {
        processElement(complexObject, newPath)
    } else {
        complexObject.attributesArray.forEach {
            processAttribute(it, newPath)
        }
    }
}

fun processAttribute(attribute: CATTRIBUTE, path: String) {
    attribute.childrenArray.forEach {
        processChild(it, "$path/${attribute.rmAttributeName}")
    }
}


fun processElement(element: CCOMPLEXOBJECT, path: String) {
    if (element.attributesArray.isNotEmpty()) {
        element.attributesArray.single { cattribute: CATTRIBUTE? -> cattribute?.rmAttributeName == "value" }
            ?.let { attr: CATTRIBUTE ->
                if (attr.childrenArray.isNotEmpty()) {
                    processDataValue(attr.getChildrenArray(0), "$path/value")
                }
            }
    }
}

fun processDataValue(dataValue: COBJECT, path: String) {
    println(dataValue.rmTypeName)
}

