package br.ufpe.cin.if710.rss

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey

@Entity(indices = [Index(value = ["entry_link"], unique = true)])
class Entry(
        @ColumnInfo(name = "entry_title") val title: String,
        @ColumnInfo(name = "entry_link") val link: String,
        @ColumnInfo(name = "entry_datetime") val datetime: String,
        @ColumnInfo(name = "entry_description") val description: String
) {
    @PrimaryKey(autoGenerate = true)
    var uid: Int = 0

    @ColumnInfo(name = "entry_read")
    var isRead: Boolean = false
}
