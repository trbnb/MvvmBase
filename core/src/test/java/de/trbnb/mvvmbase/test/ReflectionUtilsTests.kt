package de.trbnb.mvvmbase.test

import de.trbnb.mvvmbase.databinding.utils.findGenericSuperclass
import org.junit.jupiter.api.Test

class ReflectionUtilsTests {
    open class A<T>
    class B : A<String>()

    @Test
    fun `findGenericSuperclass() for objects`() {
        assert(Any().findGenericSuperclass<Any>() == null)
        assert(Any().findGenericSuperclass<List<*>>() == null)
        assert(B().findGenericSuperclass<A<*>>()?.rawType == A::class.java)
        assert(B().findGenericSuperclass<A<*>>()?.actualTypeArguments?.first() == String::class.java)
    }

    @Test
    fun `findGenericSuperclass() for types`() {
        assert(Any::class.java.findGenericSuperclass(Any::class.java) == null)
        assert(Any::class.java.findGenericSuperclass(List::class.java) == null)
        assert(B::class.java.findGenericSuperclass(A::class.java)?.rawType == A::class.java)
        assert(B::class.java.findGenericSuperclass(A::class.java)?.actualTypeArguments?.first() == String::class.java)
    }
}
