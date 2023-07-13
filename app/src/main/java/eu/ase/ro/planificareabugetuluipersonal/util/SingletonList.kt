package eu.ase.ro.planificareabugetuluipersonal.util

object SingletonList {
    private var myList: List<String> = emptyList()

    fun setList(list: List<String>) {
        myList = list
    }

    fun getList(): List<String> {
        return myList
    }
}
