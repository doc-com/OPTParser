package com.doc

import org.openehr.schemas.v1.CARCHETYPEROOT
import org.openehr.schemas.v1.OPERATIONALTEMPLATE
import org.openehr.schemas.v1.impl.OPERATIONALTEMPLATEImpl
import java.io.File


fun main(args: Array<String>) {

    val file = File("src/main/resources/actividad_fisica.opt");
    val base = OPERATIONALTEMPLATE.Factory.parse(file)
    val baseObject = base.selectChildren("http://schemas.openehr.org/v1", "template")[0]
    val opt = OPERATIONALTEMPLATE.Factory.parse(baseObject.toString()) as OPERATIONALTEMPLATEImpl

    println((opt.definition as CARCHETYPEROOT))

}

