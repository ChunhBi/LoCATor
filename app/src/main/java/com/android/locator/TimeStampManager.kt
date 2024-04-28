import android.content.Context
import android.content.SharedPreferences

object TimestampManager {
    private const val PREF_NAME = "TimestampPrefs"
    private const val KEY_LAST_CHECKED_TIMESTAMP = "lastCheckedTimestamp"

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun saveLastCheckedTimestamp(context: Context, timestamp: Long) {
        val editor = getSharedPreferences(context).edit()
        editor.putLong(KEY_LAST_CHECKED_TIMESTAMP, timestamp)
        editor.apply()
    }

    fun getLastCheckedTimestamp(context: Context): Long {
        return getSharedPreferences(context).getLong(KEY_LAST_CHECKED_TIMESTAMP, 0L)
    }
}
