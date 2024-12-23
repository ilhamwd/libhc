package com.xlzn.hcpda.uhf;

import android.content.Context;

import com.xlzn.hcpda.uhf.entity.SelectEntity;
import com.xlzn.hcpda.uhf.entity.UHFReaderResult;
import com.xlzn.hcpda.uhf.entity.UHFTagEntity;
import com.xlzn.hcpda.uhf.entity.UHFVersionInfo;
import com.xlzn.hcpda.uhf.enums.ConnectState;
import com.xlzn.hcpda.uhf.enums.InventoryModeForPower;
import com.xlzn.hcpda.uhf.enums.LockActionEnum;
import com.xlzn.hcpda.uhf.enums.LockMembankEnum;
import com.xlzn.hcpda.uhf.enums.UHFSession;
import com.xlzn.hcpda.uhf.interfaces.IUHFReader;
import com.xlzn.hcpda.uhf.interfaces.OnInventoryDataListener;
import com.xlzn.hcpda.uhf.module.UHFReaderSLR;
import com.xlzn.hcpda.utils.LoggerUtils;

public class UHFReader {
    public static boolean isMoreTag = false;
    private static UHFReader uhfReader = new UHFReader();
    private OnInventoryDataListener onInventoryDataListener;
    private IUHFReader reader = null;

    private UHFReader() {
    }

    public void setIsMoreTag(boolean moreTag) {
        isMoreTag = moreTag;
    }

    public static UHFReader getInstance() {
        return uhfReader;
    }

    public synchronized UHFReaderResult<Boolean> connect(Context context) {
        if (getConnectState() == ConnectState.CONNECTED) {
            return new UHFReaderResult<>(0, "模块已经连接成功,请勿重复连接!", true);
        }
        UHFReaderResult result = UHFReaderSLR.getInstance().connect(context);
        if (result.getResultCode() == 0) {
            this.reader = UHFReaderSLR.getInstance();
            return result;
        }
        return new UHFReaderResult<>(1);
    }

    public synchronized UHFReaderResult<Boolean> getInventoryTidModel() {
        return this.reader.getInventoryTidModel();
    }

    public synchronized UHFReaderResult<Boolean> disConnect() {
        if (getConnectState() != ConnectState.CONNECTED) {
            return new UHFReaderResult<>(2, UHFReaderResult.ResultMessage.READER_NOT_CONNECTED, false);
        }
        return this.reader.disConnect();
    }

    public synchronized UHFReaderResult<Boolean> startInventory(SelectEntity selectEntity) {
        if (getConnectState() != ConnectState.CONNECTED) {
            return new UHFReaderResult<>(2, UHFReaderResult.ResultMessage.READER_NOT_CONNECTED, false);
        }
        this.reader.setOnInventoryDataListener(this.onInventoryDataListener);
        return this.reader.startInventory(selectEntity);
    }

    public synchronized UHFReaderResult<Boolean> startInventory() {
        return startInventory((SelectEntity) null);
    }

    public synchronized UHFReaderResult<Boolean> stopInventory() {
        if (getConnectState() != ConnectState.CONNECTED) {
            return new UHFReaderResult<>(2, UHFReaderResult.ResultMessage.READER_NOT_CONNECTED, false);
        }
        return this.reader.stopInventory();
    }

    public UHFReaderResult<UHFTagEntity> singleTagInventory(SelectEntity selectEntity) {
        if (getConnectState() != ConnectState.CONNECTED) {
            return new UHFReaderResult<UHFTagEntity>(2, UHFReaderResult.ResultMessage.READER_NOT_CONNECTED, null);
        }
        if (selectEntity != null) {
            if (selectEntity.getLength() == 0) {
                return new UHFReaderResult<UHFTagEntity>(1, "选择的标签长度不能为0!", null);
            }
            if (selectEntity.getData() == null) {
                return new UHFReaderResult<UHFTagEntity>(1, "选择的标签数据不能为null!", null);
            }
        }
        return this.reader.singleTagInventory(selectEntity);
    }

    public synchronized UHFReaderResult<UHFTagEntity> singleTagInventory() {
        return singleTagInventory((SelectEntity) null);
    }

    public UHFReaderResult<Boolean> setInventorySelectEntity(SelectEntity selectEntity) {
        return this.reader.setInventorySelectEntity(selectEntity);
    }

    public UHFReaderResult<Boolean> setInventoryTid(boolean flag) {
        LoggerUtils.d("TAG", "是否设置TID = " + flag);
        if (getConnectState() != ConnectState.CONNECTED) {
            return new UHFReaderResult<>(2, UHFReaderResult.ResultMessage.READER_NOT_CONNECTED, false);
        }
        return this.reader.setInventoryTid(flag);
    }

    public synchronized ConnectState getConnectState() {
        IUHFReader iUHFReader = this.reader;
        if (iUHFReader == null) {
            return ConnectState.DISCONNECT;
        }
        return iUHFReader.getConnectState();
    }

    public synchronized UHFReaderResult<UHFVersionInfo> getVersions() {
        if (getConnectState() != ConnectState.CONNECTED) {
            return new UHFReaderResult<UHFVersionInfo>(2, UHFReaderResult.ResultMessage.READER_NOT_CONNECTED, null);
        }
        return this.reader.getVersions();
    }

    public UHFReaderResult<Boolean> setSession(UHFSession vlaue) {
        if (getConnectState() != ConnectState.CONNECTED) {
            return new UHFReaderResult<>(2, UHFReaderResult.ResultMessage.READER_NOT_CONNECTED, false);
        }
        return this.reader.setSession(vlaue);
    }

    public UHFReaderResult<UHFSession> getSession() {
        if (getConnectState() != ConnectState.CONNECTED) {
            return new UHFReaderResult<UHFSession>(2, UHFReaderResult.ResultMessage.READER_NOT_CONNECTED, null);
        }
        return this.reader.getSession();
    }

    public UHFReaderResult<Boolean> setDynamicTarget(int vlaue) {
        if (getConnectState() != ConnectState.CONNECTED) {
            return new UHFReaderResult<>(2, UHFReaderResult.ResultMessage.READER_NOT_CONNECTED, false);
        }
        return this.reader.setDynamicTarget(vlaue);
    }

    public UHFReaderResult<Boolean> setStaticTarget(int vlaue) {
        if (getConnectState() != ConnectState.CONNECTED) {
            return new UHFReaderResult<>(2, UHFReaderResult.ResultMessage.READER_NOT_CONNECTED, false);
        }
        return this.reader.setStaticTarget(vlaue);
    }

    public UHFReaderResult<int[]> getTarget() {
        if (getConnectState() != ConnectState.CONNECTED) {
            return new UHFReaderResult<int[]>(2, UHFReaderResult.ResultMessage.READER_NOT_CONNECTED, null);
        }
        return this.reader.getTarget();
    }

    public UHFReaderResult<Boolean> setInventoryModeForPower(InventoryModeForPower InventoryMode) {
        if (getConnectState() != ConnectState.CONNECTED) {
            return new UHFReaderResult<>(2, UHFReaderResult.ResultMessage.READER_NOT_CONNECTED, false);
        }
        return this.reader.setInventoryModeForPower(InventoryMode);
    }

    public void setOnInventoryDataListener(OnInventoryDataListener onInventoryDataListener2) {
        this.onInventoryDataListener = onInventoryDataListener2;
    }

    public UHFReaderResult<Boolean> setFrequencyRegion(int region) {
        if (getConnectState() != ConnectState.CONNECTED) {
            return new UHFReaderResult<>(2, UHFReaderResult.ResultMessage.READER_NOT_CONNECTED, false);
        }
        return this.reader.setFrequencyRegion(region);
    }

    public UHFReaderResult<Integer> getFrequencyRegion() {
        if (getConnectState() != ConnectState.CONNECTED) {
            return new UHFReaderResult<Integer>(2, UHFReaderResult.ResultMessage.READER_NOT_CONNECTED, null);
        }
        return this.reader.getFrequencyRegion();
    }

    public UHFReaderResult<Integer> getTemperature() {
        if (getConnectState() != ConnectState.CONNECTED) {
            return new UHFReaderResult<>(2, UHFReaderResult.ResultMessage.READER_NOT_CONNECTED, 0);
        }
        return this.reader.getTemperature();
    }

    public UHFReaderResult<String> read(String password, int membank, int address, int wordCount, SelectEntity selectEntity) {
        if (getConnectState() != ConnectState.CONNECTED) {
            return new UHFReaderResult<>(2, UHFReaderResult.ResultMessage.READER_NOT_CONNECTED, null);
        }
        if (selectEntity != null) {
            if (selectEntity.getLength() == 0) {
                return new UHFReaderResult<>(1, "选择的标签长度不能为0!", null);
            }
            if (selectEntity.getData() == null) {
                return new UHFReaderResult<>(1, "选择的标签数据不能为null!", null);
            }
        }
        return this.reader.read(password, membank, address, wordCount, selectEntity);
    }

    public UHFReaderResult<Boolean> write(String password, int membank, int address, int wordCount, String data, SelectEntity selectEntity) {
        if (getConnectState() != ConnectState.CONNECTED) {
            return new UHFReaderResult<>(2, UHFReaderResult.ResultMessage.READER_NOT_CONNECTED, false);
        }
        if (selectEntity != null) {
            if (selectEntity.getLength() == 0) {
                return new UHFReaderResult<>(1, "选择的标签长度不能为0!", null);
            }
            if (selectEntity.getData() == null) {
                return new UHFReaderResult<>(1, "选择的标签数据不能为null!", null);
            }
        }
        return this.reader.write(password, membank, address, wordCount, data, selectEntity);
    }

    public UHFReaderResult<Boolean> kill(String password, SelectEntity selectEntity) {
        if (getConnectState() != ConnectState.CONNECTED) {
            return new UHFReaderResult<>(2, UHFReaderResult.ResultMessage.READER_NOT_CONNECTED, false);
        }
        if (selectEntity != null) {
            if (selectEntity.getLength() == 0) {
                return new UHFReaderResult<>(1, "选择的标签长度不能为0!", null);
            }
            if (selectEntity.getData() == null) {
                return new UHFReaderResult<>(1, "选择的标签数据不能为null!", null);
            }
        }
        return this.reader.kill(password, selectEntity);
    }

    public UHFReaderResult<Boolean> lock(String password, LockMembankEnum membankEnum, LockActionEnum actionEnum, SelectEntity selectEntity) {
        if (getConnectState() != ConnectState.CONNECTED) {
            return new UHFReaderResult<>(2, UHFReaderResult.ResultMessage.READER_NOT_CONNECTED, false);
        }
        if (selectEntity != null) {
            if (selectEntity.getLength() == 0) {
                return new UHFReaderResult<>(1, "选择的标签长度不能为0!", null);
            }
            if (selectEntity.getData() == null) {
                return new UHFReaderResult<>(1, "选择的标签数据不能为null!", null);
            }
        }
        return this.reader.lock(password, membankEnum, actionEnum, selectEntity);
    }

    public UHFReaderResult<Boolean> setPower(int power) {
        if (getConnectState() != ConnectState.CONNECTED) {
            return new UHFReaderResult<>(2, UHFReaderResult.ResultMessage.READER_NOT_CONNECTED, false);
        }
        return this.reader.setPower(power);
    }

    public UHFReaderResult<Integer> getPower() {
        if (getConnectState() != ConnectState.CONNECTED) {
            return new UHFReaderResult<Integer>(2, UHFReaderResult.ResultMessage.READER_NOT_CONNECTED, null);
        }
        return this.reader.getPower();
    }

    public UHFReaderResult<String> getModuleType() {
        if (getConnectState() != ConnectState.CONNECTED) {
            return new UHFReaderResult<String>(2, UHFReaderResult.ResultMessage.READER_NOT_CONNECTED, null);
        }
        return new UHFReaderResult<>(0);
    }

    public UHFReaderResult<Boolean> setModuleType(String moduleType) {
        if (getConnectState() != ConnectState.CONNECTED) {
            return new UHFReaderResult<>(2, UHFReaderResult.ResultMessage.READER_NOT_CONNECTED, false);
        }
        return this.reader.setModuleType(moduleType);
    }

    public UHFReaderResult<Boolean> setFrequencyPoint(int point) {
        if (getConnectState() != ConnectState.CONNECTED) {
            return new UHFReaderResult<>(2, UHFReaderResult.ResultMessage.READER_NOT_CONNECTED, false);
        }
        return this.reader.setFrequencyPoint(point);
    }

    public UHFReaderResult<Boolean> setRFLink(int mode) {
        if (getConnectState() != ConnectState.CONNECTED) {
            return new UHFReaderResult<>(2, UHFReaderResult.ResultMessage.READER_NOT_CONNECTED, false);
        }
        return this.reader.setRFLink(mode);
    }
}
