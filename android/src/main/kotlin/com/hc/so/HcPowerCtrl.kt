package com.hc.so

class HcPowerCtrl {
    external fun fingerPower(i: Int)
    external fun identityCtrl(i: Int)
    external fun identityPower(i: Int)
    external fun irayPower(i: Int)
    external fun irayReset(i: Int)
    external fun psamCtrl(i: Int)
    external fun psamPower(i: Int)
    external fun scanPower(i: Int)
    external fun scanPwrdwn(i: Int)
    external fun scanTrig(i: Int)
    external fun scanWakeup(i: Int)
    external fun uhfCtrl(i: Int)
    external fun uhfPower(i: Int)

    companion object {
        init {
            System.loadLibrary("SerialPortHc")
        }
    }
}
