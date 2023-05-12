import java.util.Scanner

fun main() {
    val scanner = Scanner(System.`in`)
    var currentScreen: Screen = ArchiveOperationsScreen

    while (true) {
        println(currentScreen.infoAndPromptText)

        when (val screen = currentScreen) {
            is OptionScreen -> {
                if (screen is TextScreen) {
                    println(screen.text)
                }

                screen.menuItems.forEachIndexed { index, menuItem ->
                    println("${index + 1}. ${menuItem.title}")
                }

                val nextOption = scanner.nextLine()
                nextOption.toIntOrNull()?.let { index ->
                    screen.menuItems.getOrNull(index - 1)?.let {
                        currentScreen = it.action()
                    } ?: run {
                        println("Такого номера пункта нет. Попробуйте ещё раз")
                    }
                } ?: run {
                    println("Нужно ввести номер пункта")
                }
            }
            is PromptScreen -> {
                val input = if (screen.multiline) {
                    val stringBuilder = StringBuilder()

                    while (true) {
                        val line = scanner.nextLine()
                        if (line.isEmpty()) break
                        stringBuilder.append(line + "\n")
                    }
                    stringBuilder.toString()
                } else {
                    var line = scanner.nextLine()

                    while (line.isEmpty()) {
                        println("Название не может быть пустым")
                        println(currentScreen.infoAndPromptText)
                        line = scanner.nextLine()
                    }
                    line
                }
                currentScreen = screen.onTextInput(input)
            }
        }
    }
}
