import kotlin.system.exitProcess

class MenuItem(val title: String, val action: () -> Screen)

sealed interface Screen {
    val infoAndPromptText: String
}

interface OptionScreen : Screen {
    val menuItems: List<MenuItem>
}

interface PromptScreen : Screen {
    val multiline: Boolean

    fun onTextInput(input: String): Screen
}

interface TextScreen : OptionScreen {
    val text: String
}

object ArchiveOperationsScreen : OptionScreen {
    override val infoAndPromptText: String = "Список архивов"

    override val menuItems: List<MenuItem>
        get() {
            val firstItems = listOf(
                MenuItem("Выход") { exitProcess(0) },
                MenuItem("Создать архив") { ArchiveCreationScreen }
            )

            val archives = archives.mapIndexed { index, archive ->
                MenuItem(archive.title) { ArchiveScreen(archives[index]) }
            }

            return firstItems + archives
        }
}

class NoteTitleScreen(private val archive: Archive) : PromptScreen {
    override val infoAndPromptText: String = "Создание заметки\nНазвание: "
    override val multiline: Boolean = false

    override fun onTextInput(input: String): Screen = NoteTextScreen(archive, input)
}

class NoteTextScreen(private val archive: Archive, private val title: String) : PromptScreen {
    override val infoAndPromptText: String = "Для выхода нажмите Enter\nТекст заметки: "
    override val multiline: Boolean = true

    override fun onTextInput(input: String): Screen {
        archive.notes.add(Note(title, input))
        return ArchiveScreen(archive)
    }
}

class NoteScreen(note: Note, archiveScreen: ArchiveScreen) : TextScreen {
    override val infoAndPromptText: String = "Текст заметки"
    override val menuItems: List<MenuItem> = listOf(MenuItem("Выход") { archiveScreen })
    override val text: String = note.text
}

object ArchiveCreationScreen : PromptScreen {
    override val infoAndPromptText: String = "Создание нового архива \nНазвание архива: "
    override val multiline: Boolean = false

    override fun onTextInput(input: String): Screen {
        archives.add(Archive(input, mutableListOf()))
        return ArchiveOperationsScreen
    }
}

class ArchiveScreen(private val archive: Archive) : OptionScreen {
    override val infoAndPromptText: String = "Список заметок"

    override val menuItems: List<MenuItem>
        get() {
            val firstItems = listOf(
                MenuItem("Выход") { ArchiveOperationsScreen },
                MenuItem("Создать заметку") { NoteTitleScreen(archive) })

            val notes = archive.notes.map { note ->
                MenuItem(note.title) { NoteScreen(note, this) }
            }
            return firstItems + notes
        }
}