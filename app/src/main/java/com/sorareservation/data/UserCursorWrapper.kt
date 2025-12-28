package com.sorareservation.data

import android.database.Cursor
import android.database.CursorWrapper
import com.sorareservation.model.User
import java.util.UUID

/**
 * CursorWrapper for reading User objects from database
 */
class UserCursorWrapper(cursor: Cursor) : CursorWrapper(cursor) {
    
    /**
     * Get User object from current cursor position
     */
    fun getUser(): User {
        val idString = getString(getColumnIndex(SeferDbSchema.UserTable.Cols.ID))
        val email = getString(getColumnIndex(SeferDbSchema.UserTable.Cols.EMAIL))
        val password = getString(getColumnIndex(SeferDbSchema.UserTable.Cols.PASSWORD))
        val fullName = getString(getColumnIndex(SeferDbSchema.UserTable.Cols.FULL_NAME))
        val phone = getString(getColumnIndex(SeferDbSchema.UserTable.Cols.PHONE))
        val isAdmin = getInt(getColumnIndex(SeferDbSchema.UserTable.Cols.IS_ADMIN)) != 0
        
        return User(
            id = UUID.fromString(idString),
            email = email,
            password = password,
            fullName = fullName,
            phone = phone ?: "",
            isAdmin = isAdmin
        )
    }
}

