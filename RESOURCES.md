# Kotlin and Android Calendar Resources

## Knowledge

- [CalendarView.OnDateChangeListener — Android Developers](https://developer.android.com/reference/android/widget/CalendarView.OnDateChangeListener)
  Primary API reference for the selected-date callback and its year, zero-based month, and day parameters. Use for: handling date selections correctly.
- [Collections overview — Kotlin Documentation](https://kotlinlang.org/docs/collections-overview.html#map)
  Official overview of `Map`, which stores key-value pairs. Use for: associating a date key with calendar information.
- [Map-specific operations — Kotlin Documentation](https://kotlinlang.org/docs/map-operations.html)
  Official guide to map lookup and missing-key behavior. Use for: retrieving information for a selected date.
- [Data classes — Kotlin Documentation](https://kotlinlang.org/docs/data-classes.html)
  Official guide to data-holding classes. Use for: representing appointments, habit checks, and period logs with named fields.
- [Filtering collections — Kotlin Documentation](https://kotlinlang.org/docs/collection-filtering.html)
  Official guide to selecting elements with predicates. Use for: retrieving feature records that match the selected date.
- [Enum classes — Kotlin Documentation](https://kotlinlang.org/docs/enum-classes.html)
  Official guide to finite sets of values. Use for: representing habit states without ambiguous Booleans.

- [Conditions and loops — Kotlin Documentation](https://kotlinlang.org/docs/control-flow.html#if-expression)
  Official guide to Kotlin `if` as both control flow and a value-producing expression. Use for: converting a Boolean habit result into checked or unchecked display text.
- [Strings — Kotlin Documentation](https://kotlinlang.org/docs/strings.html#string-templates)
  Official guide to strings, escape sequences, and interpolation. Use for: assembling readable feature rows in the daily summary.
- [EditText ? Android Developers](https://developer.android.com/reference/android/widget/EditText)
  Primary API reference for Android's editable text widget. Use for: reading an appointment title and time entered in XML controls.
- [Button ? Android Developers](https://developer.android.com/reference/android/widget/Button)
  Primary API reference for a tappable action and click-listener wiring. Use for: running appointment-addition logic after the user taps Add.
- [Collections overview ? Kotlin Documentation](https://kotlinlang.org/docs/collections-overview.html)
  Official distinction between read-only and mutable collections, including mutation through a `val` reference. Use for: understanding why appointment storage becomes a `MutableList`.
- [Collection write operations ? Kotlin Documentation](https://kotlinlang.org/docs/collection-write.html)
  Official guide to mutable-collection operations such as `add`. Use for: appending a constructed `Appointment` to the in-memory collection.

- [EditText ? Android Developers](https://developer.android.com/reference/android/widget/EditText)
  Primary API reference for Android's editable text widget. Use for: reading an appointment title and time entered in XML controls.
- [Button ? Android Developers](https://developer.android.com/reference/android/widget/Button)
  Primary API reference for a tappable action and click-listener wiring. Use for: running appointment-addition logic after the user taps Add.
- [Collections overview ? Kotlin Documentation](https://kotlinlang.org/docs/collections-overview.html)
  Official distinction between read-only and mutable collections, including mutation through a `val` reference. Use for: understanding why appointment storage becomes a `MutableList`.
- [Collection write operations ? Kotlin Documentation](https://kotlinlang.org/docs/collection-write.html)
  Official guide to mutable-collection operations such as `add`. Use for: appending a constructed `Appointment` to the in-memory collection.

## Wisdom (Communities)

- [Kotlin Discussions](https://discuss.kotlinlang.org/)
  Official Kotlin community forum. Use for: language design questions and Kotlin-specific implementation discussion.
