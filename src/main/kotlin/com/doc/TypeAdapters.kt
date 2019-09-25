package com.doc

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import org.openehr.schemas.v1.CQUANTITYITEM
import org.openehr.schemas.v1.IntervalOfInteger
import org.openehr.schemas.v1.IntervalOfReal

class CQuantityItemAdapter : TypeAdapter<CQUANTITYITEM>() {
    override fun write(out: JsonWriter?, value: CQUANTITYITEM?) {
        out?.beginObject()
        IntervalOfRealAdapter().write(out, value?.magnitude)
        out?.name("units")?.value(value?.units)
        out?.endObject()
    }

    override fun read(`in`: JsonReader?): CQUANTITYITEM {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}

class IntervalOfRealAdapter : TypeAdapter<IntervalOfReal>() {
    override fun write(out: JsonWriter?, value: IntervalOfReal?) {
        out?.name("magnitude")?.beginObject()
        out?.name("lower_included")?.value(value?.lowerIncluded ?: false)
        out?.name("upper_included")?.value(value?.upperIncluded ?: false)
        out?.name("lower_unbounded")?.value(value?.lowerUnbounded ?: false)
        out?.name("upper_unbounded")?.value(value?.upperUnbounded ?: false)
        out?.name("lower")?.value(value?.lower ?: 0f)
        out?.name("upper")?.value(value?.upper ?: 1f)
        out?.endObject()
    }

    override fun read(`in`: JsonReader?): IntervalOfReal? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}

class IntervalOfIntegerAdapter : TypeAdapter<IntervalOfInteger>() {
    override fun write(out: JsonWriter?, value: IntervalOfInteger?) {
        out?.beginObject()
        out?.name("lower_included")?.value(value?.lowerIncluded ?: false)
        out?.name("upper_included")?.value(value?.upperIncluded ?: false)
        out?.name("lower_unbounded")?.value(value?.lowerUnbounded ?: false)
        out?.name("upper_unbounded")?.value(value?.upperUnbounded ?: false)
        out?.name("lower")?.value(value?.lower ?: 0)
        out?.name("upper")?.value(value?.upper ?: 1)
        out?.endObject()
    }

    override fun read(`in`: JsonReader?): IntervalOfInteger {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}