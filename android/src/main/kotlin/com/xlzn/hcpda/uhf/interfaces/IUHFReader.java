package com.xlzn.hcpda.uhf.interfaces;

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

public interface IUHFReader {
    UHFReaderResult<Boolean> connect(Context context);

    UHFReaderResult<Boolean> disConnect();

    ConnectState getConnectState();

    UHFReaderResult<Integer> getFrequencyRegion();

    UHFReaderResult<Boolean> getInventoryTidModel();

    UHFReaderResult<String> getModuleType();

    UHFReaderResult<Integer> getPower();

    UHFReaderResult<UHFSession> getSession();

    UHFReaderResult<int[]> getTarget();

    UHFReaderResult<Integer> getTemperature();

    UHFReaderResult<UHFVersionInfo> getVersions();

    UHFReaderResult<Boolean> kill(String str, SelectEntity selectEntity);

    UHFReaderResult<Boolean> lock(String str, LockMembankEnum lockMembankEnum, LockActionEnum lockActionEnum, SelectEntity selectEntity);

    UHFReaderResult<String> read(String str, int i, int i2, int i3, SelectEntity selectEntity);

    UHFReaderResult<Boolean> setBaudRate(int i);

    UHFReaderResult<Boolean> setDynamicTarget(int i);

    UHFReaderResult<Boolean> setFrequencyPoint(int i);

    UHFReaderResult<Boolean> setFrequencyRegion(int i);

    UHFReaderResult<Boolean> setInventoryModeForPower(InventoryModeForPower inventoryModeForPower);

    UHFReaderResult<Boolean> setInventorySelectEntity(SelectEntity selectEntity);

    UHFReaderResult<Boolean> setInventoryTid(boolean z);

    UHFReaderResult<Boolean> setModuleType(String str);

    void setOnInventoryDataListener(OnInventoryDataListener onInventoryDataListener);

    UHFReaderResult<Boolean> setPower(int i);

    UHFReaderResult<Boolean> setRFLink(int i);

    UHFReaderResult<Boolean> setSession(UHFSession uHFSession);

    UHFReaderResult<Boolean> setStaticTarget(int i);

    UHFReaderResult<UHFTagEntity> singleTagInventory(SelectEntity selectEntity);

    UHFReaderResult<Boolean> startInventory(SelectEntity selectEntity);

    UHFReaderResult<Boolean> stopInventory();

    UHFReaderResult<Boolean> write(String str, int i, int i2, int i3, String str2, SelectEntity selectEntity);
}
