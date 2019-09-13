package com.doc

import openEHR.v1.template.COMPOSITION
import org.openehr.schemas.v1.*
import org.openehr.schemas.v1.impl.CMULTIPLEATTRIBUTEImpl
import org.openehr.schemas.v1.impl.OPERATIONALTEMPLATEImpl
import java.io.File

fun main(args: Array<String>) {

    val file = File("src/main/resources/actividad_fisica.opt");
    val base = OPERATIONALTEMPLATE.Factory.parse(file)
    val baseObject = base.selectChildren("http://schemas.openehr.org/v1", "template")[0]
    val opt = OPERATIONALTEMPLATE.Factory.parse(baseObject.toString()) as OPERATIONALTEMPLATEImpl
    findDefinitionElements(opt.definition)

}

fun findDefinitionElements(root: CARCHETYPEROOT) {
    if (root.rmTypeName == COMPOSITION::class.simpleName) {
        val content: CMULTIPLEATTRIBUTE =
            CMULTIPLEATTRIBUTE.Factory.parse(root.attributesArray.single { cattribute: CATTRIBUTE? -> cattribute?.rmAttributeName == "content" }.toString())
        findContentElementsRecursively(content)
    }
}

fun findContentElementsRecursively(content: CMULTIPLEATTRIBUTE) {
    println(content.childrenArray)
}

