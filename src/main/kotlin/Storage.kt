val archives = mutableListOf<Archive>()

class Archive(val title: String, val notes: MutableList<Note>)

class Note(val title: String, val text: String)