package com.doc

import com.doc.uimodel.UITemplate
import java.io.File


fun main(args: Array<String>) {
    val file = File("src/main/resources/soap.opt")
    val uiTemplate = UITemplate(file)
    println(uiTemplate.toJson())
}

