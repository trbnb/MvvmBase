package de.trbnb.kotlindaggerdatabindingtemplate

import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

interface API {

    fun loadData(callback: (Array<String>) -> Unit)

}

class APIImpl : API{
    override fun loadData(callback: (Array<String>) -> Unit) {
        doAsync {
            Thread.sleep(5000)

            uiThread {
                callback(arrayOf(
                        "This is Mock-Data",
                        "More Mock-Data",
                        "MOOOOOOCK-DAAAATA"
                ))
            }
        }
    }
}