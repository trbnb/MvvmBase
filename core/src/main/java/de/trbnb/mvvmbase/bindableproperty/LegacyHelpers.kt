package de.trbnb.mvvmbase.bindableproperty

import de.trbnb.mvvmbase.ViewModel
import de.trbnb.mvvmbase.observableproperty.ObservableProperty
import de.trbnb.mvvmbase.observableproperty.StateSaveOption
import de.trbnb.mvvmbase.observableproperty.observable
import de.trbnb.mvvmbase.savedstate.StateSavingViewModel

/**
 *  Migration helper for [de.trbnb.mvvmbase.databinding.bindableproperty.bindableBoolean].
 */
@Deprecated("Use observable() instead.", ReplaceWith("observable(defaultValue, stateSaveOption)", "de.trbnb.mvvmbase.observableproperty.observable"))
fun ViewModel.bindableBoolean(
    defaultValue: Boolean = false,
    fieldId: Int? = null,
    stateSaveOption: StateSaveOption = (this as? StateSavingViewModel)?.defaultStateSaveOption ?: StateSaveOption.None
): ObservableProperty.Provider<Boolean> = observable(defaultValue, stateSaveOption)

/**
 *  Migration helper for [de.trbnb.mvvmbase.databinding.bindableproperty.bindableBoolean].
 */
@Deprecated("Use observable() instead.", ReplaceWith("observable(defaultValue)", "de.trbnb.mvvmbase.observableproperty.observable"))
fun ViewModel.bindableBoolean(
    defaultValue: Boolean = false,
    fieldId: Int? = null,
): ObservableProperty.Provider<Boolean> = observable(defaultValue)

/**
 *  Migration helper for [de.trbnb.mvvmbase.databinding.bindableproperty.bindableByte].
 */
@Deprecated("Use observable() instead.", ReplaceWith("observable(defaultValue, stateSaveOption)", "de.trbnb.mvvmbase.observableproperty.observable"))
fun ViewModel.bindableByte(
    defaultValue: Byte = 0,
    fieldId: Int? = null,
    stateSaveOption: StateSaveOption = (this as? StateSavingViewModel)?.defaultStateSaveOption ?: StateSaveOption.None
): ObservableProperty.Provider<Byte> = observable(defaultValue, stateSaveOption)

/**
 *  Migration helper for [de.trbnb.mvvmbase.databinding.bindableproperty.bindableByte].
 */
@Deprecated("Use observable() instead.", ReplaceWith("observable(defaultValue)", "de.trbnb.mvvmbase.observableproperty.observable"))
fun ViewModel.bindableByte(
    defaultValue: Byte = 0,
    fieldId: Int? = null,
): ObservableProperty.Provider<Byte> = observable(defaultValue)

/**
 *  Migration helper for [de.trbnb.mvvmbase.databinding.bindableproperty.bindableChar].
 */
@Deprecated("Use observable() instead.", ReplaceWith("observable(defaultValue, stateSaveOption)", "de.trbnb.mvvmbase.observableproperty.observable"))
fun ViewModel.bindableChar(
    defaultValue: Char,
    fieldId: Int? = null,
    stateSaveOption: StateSaveOption = (this as? StateSavingViewModel)?.defaultStateSaveOption ?: StateSaveOption.None
): ObservableProperty.Provider<Char> = observable(defaultValue, stateSaveOption)

/**
 *  Migration helper for [de.trbnb.mvvmbase.databinding.bindableproperty.bindableChar].
 */
@Deprecated("Use observable() instead.", ReplaceWith("observable(defaultValue)", "de.trbnb.mvvmbase.observableproperty.observable"))
fun ViewModel.bindableChar(
    defaultValue: Char,
    fieldId: Int? = null
): ObservableProperty.Provider<Char> = observable(defaultValue)

/**
 *  Migration helper for [de.trbnb.mvvmbase.databinding.bindableproperty.bindableDouble].
 */
@Deprecated("Use observable() instead.", ReplaceWith("observable(defaultValue, stateSaveOption)", "de.trbnb.mvvmbase.observableproperty.observable"))
fun ViewModel.bindableDouble(
    defaultValue: Double = 0.0,
    fieldId: Int? = null,
    stateSaveOption: StateSaveOption = (this as? StateSavingViewModel)?.defaultStateSaveOption ?: StateSaveOption.None
): ObservableProperty.Provider<Double> = observable(defaultValue, stateSaveOption)

/**
 *  Migration helper for [de.trbnb.mvvmbase.databinding.bindableproperty.bindableDouble].
 */
@Deprecated("Use observable() instead.", ReplaceWith("observable(defaultValue)", "de.trbnb.mvvmbase.observableproperty.observable"))
fun ViewModel.bindableDouble(
    defaultValue: Double = 0.0,
    fieldId: Int? = null
): ObservableProperty.Provider<Double> = observable(defaultValue)

/**
 * Migration helper for [de.trbnb.mvvmbase.databinding.bindableproperty.bindableFloat]
 */
@Deprecated("Use observable() instead.", ReplaceWith("observable(defaultValue, stateSaveOption)", "de.trbnb.mvvmbase.observableproperty.observable"))
fun ViewModel.bindableFloat(
    defaultValue: Float = 0f,
    fieldId: Int? = null,
    stateSaveOption: StateSaveOption = (this as? StateSavingViewModel)?.defaultStateSaveOption ?: StateSaveOption.None
): ObservableProperty.Provider<Float> = observable(defaultValue, stateSaveOption)

/**
 * Migration helper for [de.trbnb.mvvmbase.databinding.bindableproperty.bindableFloat]
 */
@Deprecated("Use observable() instead.", ReplaceWith("observable(defaultValue)", "de.trbnb.mvvmbase.observableproperty.observable"))
fun ViewModel.bindableFloat(
    defaultValue: Float = 0f,
    fieldId: Int? = null
): ObservableProperty.Provider<Float> = observable(defaultValue)

/**
 * Migration helper for [de.trbnb.mvvmbase.databinding.bindableproperty.bindableInt]
 */
@Deprecated("Use observable() instead.", ReplaceWith("observable(defaultValue, stateSaveOption)", "de.trbnb.mvvmbase.observableproperty.observable"))
fun ViewModel.bindableInt(
    defaultValue: Int = 0,
    fieldId: Int? = null,
    stateSaveOption: StateSaveOption = (this as? StateSavingViewModel)?.defaultStateSaveOption ?: StateSaveOption.None
): ObservableProperty.Provider<Int> = observable(defaultValue, stateSaveOption)

/**
 * Migration helper for [de.trbnb.mvvmbase.databinding.bindableproperty.bindableInt]
 */
@Deprecated("Use observable() instead.", ReplaceWith("observable(defaultValue)", "de.trbnb.mvvmbase.observableproperty.observable"))
fun ViewModel.bindableInt(
    defaultValue: Int = 0,
    fieldId: Int? = null
): ObservableProperty.Provider<Int> = observable(defaultValue)

/**
 * Migration helper for [de.trbnb.mvvmbase.databinding.bindableproperty.bindableLong]
 */
@Deprecated("Use observable() instead.", ReplaceWith("observable(defaultValue, stateSaveOption)", "de.trbnb.mvvmbase.observableproperty.observable"))
fun ViewModel.bindableLong(
    defaultValue: Long = 0,
    fieldId: Int? = null,
    stateSaveOption: StateSaveOption = (this as? StateSavingViewModel)?.defaultStateSaveOption ?: StateSaveOption.None
): ObservableProperty.Provider<Long> = observable(defaultValue, stateSaveOption)

/**
 * Migration helper for [de.trbnb.mvvmbase.databinding.bindableproperty.bindableLong]
 */
@Deprecated("Use observable() instead.", ReplaceWith("observable(defaultValue)", "de.trbnb.mvvmbase.observableproperty.observable"))
fun ViewModel.bindableLong(
    defaultValue: Long = 0,
    fieldId: Int? = null
): ObservableProperty.Provider<Long> = observable(defaultValue)

/**
 * Migration helper for [de.trbnb.mvvmbase.databinding.bindableproperty.bindable]
 */
@Deprecated("Use observable() instead.", ReplaceWith("observable(defaultValue, stateSaveOption)", "de.trbnb.mvvmbase.observableproperty.observable"))
inline fun <reified T> ViewModel.bindable(
    defaultValue: T,
    fieldId: Int? = null,
    stateSaveOption: StateSaveOption? = null
): ObservableProperty.Provider<T> = observable(defaultValue, stateSaveOption)

/**
 * Migration helper for [de.trbnb.mvvmbase.databinding.bindableproperty.bindable]
 */
@Deprecated("Use observable() instead.", ReplaceWith("observable(defaultValue)", "de.trbnb.mvvmbase.observableproperty.observable"))
inline fun <reified T> ViewModel.bindable(
    defaultValue: T,
    fieldId: Int? = null,
): ObservableProperty.Provider<T> = observable(defaultValue)

/**
 * Migration helper for [de.trbnb.mvvmbase.databinding.bindableproperty.bindable]
 */
@Deprecated(
    "Use observable() instead.",
    ReplaceWith("observable(stateSaveOption = stateSaveOption)", "de.trbnb.mvvmbase.observableproperty.observable")
)
inline fun <reified T> ViewModel.bindable(
    fieldId: Int? = null,
    stateSaveOption: StateSaveOption? = null
): ObservableProperty.Provider<T?> = observable<T?>(null, stateSaveOption)

/**
 * MIgration helper for [de.trbnb.mvvmbase.databinding.bindableproperty.bindable].
 */
@Deprecated("Use observable() instead.", ReplaceWith("observable()", "de.trbnb.mvvmbase.observableproperty.observable"))
inline fun <reified T> ViewModel.bindable(
    fieldId: Int? = null,
): ObservableProperty.Provider<T?> = observable<T?>(null)

/**
 * MIgration helper for [de.trbnb.mvvmbase.databinding.bindableproperty.bindableShort].
 */
@Deprecated("Use observable() instead.", ReplaceWith("observable(defaultValue, stateSaveOption)", "de.trbnb.mvvmbase.observableproperty.observable"))
fun ViewModel.bindableShort(
    defaultValue: Short = 0,
    fieldId: Int? = null,
    stateSaveOption: StateSaveOption = (this as? StateSavingViewModel)?.defaultStateSaveOption ?: StateSaveOption.None
): ObservableProperty.Provider<Short> = observable(defaultValue, stateSaveOption)

/**
 * MIgration helper for [de.trbnb.mvvmbase.databinding.bindableproperty.bindableShort].
 */
@Deprecated("Use observable() instead.", ReplaceWith("observable(defaultValue)", "de.trbnb.mvvmbase.observableproperty.observable"))
fun ViewModel.bindableShort(
    defaultValue: Short = 0,
    fieldId: Int? = null
): ObservableProperty.Provider<Short> = observable(defaultValue)

/**
 * MIgration helper for [de.trbnb.mvvmbase.databinding.bindableproperty.bindableUByte].
 */
@Deprecated("Use observable() instead.", ReplaceWith("observable(defaultValue, stateSaveOption)", "de.trbnb.mvvmbase.observableproperty.observable"))
fun ViewModel.bindableUByte(
    defaultValue: UByte = 0U,
    fieldId: Int? = null,
    stateSaveOption: StateSaveOption = (this as? StateSavingViewModel)?.defaultStateSaveOption ?: StateSaveOption.None
): ObservableProperty.Provider<UByte> = observable(defaultValue, stateSaveOption)

/**
 * MIgration helper for [de.trbnb.mvvmbase.databinding.bindableproperty.bindableUByte].
 */
@Deprecated("Use observable() instead.", ReplaceWith("observable(defaultValue)", "de.trbnb.mvvmbase.observableproperty.observable"))
fun ViewModel.bindableUByte(
    defaultValue: UByte = 0U,
    fieldId: Int? = null
): ObservableProperty.Provider<UByte> = observable(defaultValue)

/**
 * MIgration helper for [de.trbnb.mvvmbase.databinding.bindableproperty.bindableUInt].
 */
@Deprecated("Use observable() instead.", ReplaceWith("observable(defaultValue, stateSaveOption)", "de.trbnb.mvvmbase.observableproperty.observable"))
fun ViewModel.bindableUInt(
    defaultValue: UInt = 0U,
    fieldId: Int? = null,
    stateSaveOption: StateSaveOption = (this as? StateSavingViewModel)?.defaultStateSaveOption ?: StateSaveOption.None
): ObservableProperty.Provider<UInt> = observable(defaultValue, stateSaveOption)

/**
 * MIgration helper for [de.trbnb.mvvmbase.databinding.bindableproperty.bindableUInt].
 */
@Deprecated("Use observable() instead.", ReplaceWith("observable(defaultValue)", "de.trbnb.mvvmbase.observableproperty.observable"))
fun ViewModel.bindableUInt(
    defaultValue: UInt = 0U,
    fieldId: Int? = null
): ObservableProperty.Provider<UInt> = observable(defaultValue)

/**
 * MIgration helper for [de.trbnb.mvvmbase.databinding.bindableproperty.bindableULong].
 */
@Deprecated("Use observable() instead.", ReplaceWith("observable(defaultValue, stateSaveOption)", "de.trbnb.mvvmbase.observableproperty.observable"))
fun ViewModel.bindableULong(
    defaultValue: ULong = 0UL,
    fieldId: Int? = null,
    stateSaveOption: StateSaveOption = (this as? StateSavingViewModel)?.defaultStateSaveOption ?: StateSaveOption.None
): ObservableProperty.Provider<ULong> = observable(defaultValue, stateSaveOption)

/**
 * MIgration helper for [de.trbnb.mvvmbase.databinding.bindableproperty.bindableULong].
 */
@Deprecated("Use observable() instead.", ReplaceWith("observable(defaultValue)", "de.trbnb.mvvmbase.observableproperty.observable"))
fun ViewModel.bindableULong(
    defaultValue: ULong = 0UL,
    fieldId: Int? = null
): ObservableProperty.Provider<ULong> = observable(defaultValue)

/**
 * MIgration helper for [de.trbnb.mvvmbase.databinding.bindableproperty.bindableUShort].
 */
@Deprecated("Use observable() instead.", ReplaceWith("observable(defaultValue, stateSaveOption)", "de.trbnb.mvvmbase.observableproperty.observable"))
fun ViewModel.bindableUShort(
    defaultValue: UShort = 0U,
    fieldId: Int? = null,
    stateSaveOption: StateSaveOption = (this as? StateSavingViewModel)?.defaultStateSaveOption ?: StateSaveOption.None
): ObservableProperty.Provider<UShort> = observable(defaultValue, stateSaveOption)

/**
 * MIgration helper for [de.trbnb.mvvmbase.databinding.bindableproperty.bindableUShort].
 */
@Deprecated("Use observable() instead.", ReplaceWith("observable(defaultValue)", "de.trbnb.mvvmbase.observableproperty.observable"))
fun ViewModel.bindableUShort(
    defaultValue: UShort = 0U,
    fieldId: Int? = null
): ObservableProperty.Provider<UShort> = observable(defaultValue)
