package br.ufpe.cin.if710.rss

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update

@Dao
interface EntryDao {
    @get:Query("SELECT * FROM entry")
    val all: List<Entry>

    @get:Query("SELECT * FROM entry WHERE entry_read = 0")
    val items: List<Entry>

    @Insert
    fun insertAll(vararg entries: Entry)

    @Update
    fun update(entry: Entry)

    @Delete
    fun delete(entry: Entry)
}
