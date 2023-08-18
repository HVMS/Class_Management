package com.globalitians.employees.customviews

class CustomEnumBrandonNew {
    enum class CustomFontType(val value: Int) {
        BLACK(1),
        BLACK_ITALIC(2),
        BOLD(3),
        BOLD_ITALIC(4),
        LIGHT(5),
        LIGHT_ITALIC(6),
        MEDIUM(7),
        MEDIUM_ITALIC(8),
        REGULAR(9),
        REGULAR_ITALIC(10),
        THIN(11),
        THIN_ITALIC(12);

        companion object {
            fun fromId(id: Int): CustomFontType {
                for (f in CustomFontType.values()) {
                    if (f.value == id) return f
                }
                throw IllegalArgumentException()
            }
        }

    }
}