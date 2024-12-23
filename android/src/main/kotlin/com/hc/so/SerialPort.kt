import java.io.File
import java.io.FileDescriptor
import java.io.IOException


class SerialPort {
    external fun close()

    external fun open(str: String?, i: Int, i2: Int): FileDescriptor?

    fun chmod777(file: File?): Boolean {
        if (file == null || !file.exists()) {
            return false
        }
        try {
            val su = Runtime.getRuntime().exec("/system/bin/su")
            su.outputStream.write(
                """chmod 777 ${file.absolutePath}
    exit
    """.toByteArray()
            )
            return if ((su.waitFor() != 0) || !file.canRead() || !file.canWrite() || !file.canExecute()) {
                false
            } else true
        } catch (e: IOException) {
            e.printStackTrace()

            return false
        } catch (e: InterruptedException) {
            e.printStackTrace()

            return false
        }
    }

    companion object {
        private val TAG = SerialPort::class.java.getSimpleName()

        init {
            System.loadLibrary("SerialPortHc")
        }
    }
}