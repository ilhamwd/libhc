package com.xlzn.hcpda.uhf.interfaces;

import com.xlzn.hcpda.uhf.entity.UHFTagEntity;
import java.util.List;

public interface OnInventoryDataListener {
    void onInventoryData(List<UHFTagEntity> list);
}
