package com.android.systemlog

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.android.systemloglib.*

class MainActivity : AppCompatActivity() {
    private var textViewCamera: TextView? = null
    private var textViewLocation: TextView? = null
    private var textViewPermission: TextView? = null
    private var textViewNfc: TextView? = null
    private var textViewApp: TextView? = null
    private var editText: EditText? = null
    private var textViewResult: TextView? = null

    @SuppressLint("MissingInflatedId", "SetTextI18n", "SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        textViewCamera = findViewById(R.id.cameraInfo)
        textViewLocation = findViewById(R.id.locationInfo)
        textViewPermission = findViewById(R.id.permissionInfo)
        textViewNfc = findViewById(R.id.nfcInfo)
        textViewApp = findViewById(R.id.appInfo)
        editText = findViewById(R.id.edit)
        textViewResult = findViewById(R.id.textInfo)

        textViewCamera?.movementMethod = ScrollingMovementMethod()
        textViewLocation?.movementMethod = ScrollingMovementMethod()
        textViewPermission?.movementMethod = ScrollingMovementMethod()
        textViewNfc?.movementMethod = ScrollingMovementMethod()
        textViewApp?.movementMethod = ScrollingMovementMethod()
        textViewResult?.movementMethod = ScrollingMovementMethod()

        findViewById<Button>(R.id.button_file).setOnClickListener {
            if(editText?.text.toString() == "") Toast.makeText(this, "请输入需要过滤的文件夹目录，eg: /data  多个目录请用,隔开", Toast.LENGTH_SHORT).show()
            else {
                val str = editText?.text.toString()
                val list = mutableListOf<String>()
                str.split(",").forEach { list.add(it) }
                //此处1000000表示从此时此刻往前1000000ms的时间段内
                val result = getFileUsageRecordList(list, 1000000)
                result.forEach {
                    textViewResult?.text = "${textViewResult?.text}包名：${it["packageName"]} 操作：${it["fileOperateType"]} 路径：${it["filePath"]} 时间: ${it["logTime"]}\n"
                }
            }
        }

        findViewById<Button>(R.id.button_network).setOnClickListener {
            if(editText?.text.toString() == "") Toast.makeText(this, "请输入包名", Toast.LENGTH_SHORT).show()
            else {
                //此处1000000表示从此时此刻往前1000000ms的时间段内
                val result = getNetworkRecordList(editText?.text.toString(), 1000000)
                result.forEach {
                    textViewResult?.text = "${textViewResult?.text}包名：${it["packageName"]} url：${it["url"]} 时间: ${it["logTime"]}\n"
                }
            }
        }

        findViewById<Button>(R.id.button_network1).setOnClickListener {
            if(editText?.text.toString() == "") Toast.makeText(this, "请输入包名", Toast.LENGTH_SHORT).show()
            else {
                val result = getTrafficByPackageName(this, editText?.text.toString())
                textViewResult?.text = "${textViewResult?.text}包名：$packageName   流量：${result["packageName"]}\n"
            }
        }

        registerListenInterface(this)

        setCameraUsageListener { packageName, enable ->
            textViewCamera?.text =
                "${textViewCamera?.text}${now()}->$packageName : ${if (enable) "相机打开" else "相机关闭"}\n"
        }

        setGpsUsageListener { packageName ->
            textViewLocation?.text = "${textViewLocation?.text}${now()}->$packageName 调用了GPS\n"
        }

        setNfcUsageListener { packageName ->
            textViewNfc?.text = "${textViewNfc?.text}${now()}->$packageName 调用了NFC\n"
        }

        setAppPermissionRequestListener { packageName, permission, status ->
            textViewPermission?.text =
                "${textViewPermission?.text}${now()}->$packageName $permission $status\n"
        }

        setAppUsageListener { packageName, status ->
            textViewApp?.text =
                "${textViewApp?.text}${now()}->$packageName ${if (status == 0) "应用打开" else if (status == 1) "应用关闭" else "应用进程结束"}\n"
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        unRegisterListenInterface(this)
    }
}