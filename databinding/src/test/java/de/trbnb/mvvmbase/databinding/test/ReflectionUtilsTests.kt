package de.trbnb.mvvmbase.databinding.test

import androidx.databinding.Bindable
import de.trbnb.mvvmbase.MvvmBase
import de.trbnb.mvvmbase.databinding.BaseViewModel
import de.trbnb.mvvmbase.databinding.initDataBinding
import de.trbnb.mvvmbase.databinding.resetDataBinding
import de.trbnb.mvvmbase.databinding.utils.brFieldName
import de.trbnb.mvvmbase.databinding.utils.findGenericSuperclass
import de.trbnb.mvvmbase.databinding.utils.resolveFieldId
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ReflectionUtilsTests {
    open class A<T>
    class B : A<String>()

    @BeforeEach
    fun setup() {
        MvvmBase.disableViewModelLifecycleThreadConstraints()
    }

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

    @Test
    fun `BR field name from property`() {
        val viewModel = TestViewModel()

        assert(viewModel::amount.brFieldName() == "amount")
        assert(viewModel::isAmount.brFieldName() == "isAmount")
        assert(viewModel::isLoading.brFieldName() == "loading")
        assert(viewModel::loading.brFieldName() == "loading")
        assert(viewModel::isLoadingNullable.brFieldName() == "isLoadingNullable")
    }

    @Test
    fun `BR field integer from property`() {
        MvvmBase.initDataBinding()
        val viewModel = TestViewModel()

        assert(viewModel::amount.resolveFieldId() == BR.amount)
        assert(viewModel::isAmount.resolveFieldId() == BR._all)
        assert(viewModel::isLoading.resolveFieldId() == BR.loading)
        assert(viewModel::loading.resolveFieldId() == BR.loading)
        assert(viewModel::isLoadingNullable.resolveFieldId() == BR._all)

        // reset MvvmBase for other tests
        MvvmBase.resetDataBinding()
    }

    class TestViewModel : BaseViewModel() {
        @get:Bindable val amount = 4
        /* No bindable as it violates JavaBeans convention */ val isAmount = 4
        @get:Bindable val isLoading = false
        @get:Bindable val loading = false
        /* No bindable as it violates JavaBeans convention */ val isLoadingNullable: Boolean? = null
    }
}
