package com.fyp.im.pack;

import com.fyp.im.constant.LayimMsgType;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Auther: lingyun.jiang
 * @Date: 2019/10/18 16:57
 * @Description:
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class LayimToClientOnlineStatusMsgBody extends LayimBaseBody {
    private static final String ONLINE = "online";
    private static final String OFFLINE = "offline";
    @Override
    public byte getMtype() {
        return LayimMsgType.CLIENT_ONLINE_STATUS;
    }

    @Override
    public void setMtype(byte mtype) {

    }

    public LayimToClientOnlineStatusMsgBody(long userId,boolean online){
        this.id = userId;
        this.status = online ? ONLINE : OFFLINE;
    }

    public String getStatus() {
        return status;
    }

    private String status;
    private long id;
}
