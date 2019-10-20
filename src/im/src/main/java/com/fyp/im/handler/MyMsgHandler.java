package com.fyp.im.handler;

import com.fyp.entity.LayIMConstants;
import com.fyp.entity.User;
import com.fyp.im.pack.BodyConvert;
import com.fyp.im.pack.LayimToClientOnlineStatusMsgBody;
import com.fyp.im.utils.BeanUtil;
import com.fyp.service.intf.LayIMService;
import com.fyp.utils.jwt.JwtUtil;
import com.fyp.utils.jwt.JwtVertifyResult;
import org.tio.core.ChannelContext;
import org.tio.core.Tio;
import org.tio.http.common.HttpRequest;
import org.tio.http.common.HttpResponse;
import org.tio.http.common.HttpResponseStatus;
import org.tio.websocket.common.WsRequest;
import org.tio.websocket.common.WsResponse;
import org.tio.websocket.server.handler.IWsMsgHandler;

import java.io.IOException;
import java.util.List;


/**
 * WebSocket 核心消息处理
 * */
public class MyMsgHandler implements IWsMsgHandler {
    /**
     * 握手
     * */
    public HttpResponse handshake(HttpRequest httpRequest, HttpResponse httpResponse, ChannelContext channelContext) throws Exception {
        String token = httpRequest.getParam("access_token");
        JwtVertifyResult result = JwtUtil.verifyToken(token);
        if (result.isVertified()) {
            bindUser(channelContext, result.getUserId());
        } else {
            httpResponse.setStatus(HttpResponseStatus.C401);
        }
        return httpResponse;
    }

    private LayIMService getLayIMService(){
        return (LayIMService) BeanUtil.getBean(LayIMConstants.LAYIM_SERVICE);
    }

    private void bindUser(ChannelContext channelContext, Long userId) throws IOException {
        //绑定用户
        Tio.bindUser(channelContext, userId.toString());
        //绑定User属性信息
        User user = getLayIMService().getByUserId(userId);
        channelContext.setAttribute(LayIMConstants.CURRENT_USER_ATTRIBUTE, user);
        //绑定群组
        List<Long> groupIds = getLayIMService().getGroupIds(userId);
        for (Long id : groupIds) {
            Tio.bindGroup(channelContext, id.toString());
        }
        //通知所有好友本人上线了
        notify(channelContext,true);
    }

    /**
     * 通知该用户的好友上线消息
     * */
    private void notify(ChannelContext channelContext,boolean online) throws IOException {
        long uid = Long.parseLong(channelContext.userid);
        //获取用户所有的好友ID
        List<String> allFriendIds = getLayIMService().getAllFriends(uid);
        if(allFriendIds.size()==0){
            return;
        }
        //构建消息体
        LayimToClientOnlineStatusMsgBody msgBody = new LayimToClientOnlineStatusMsgBody(uid,online);
        WsResponse statusPacket = BodyConvert.getInstance().convertToTextResponse(msgBody);

        //调用sendToAll的方法
        Tio.sendToAll(channelContext.getGroupContext(), statusPacket, filterChannelContext -> {
            //筛选掉已经移除和关闭的连接
            if(filterChannelContext.isRemoved||filterChannelContext.isClosed) {
                return false;
            }
            //筛选掉非当前用户好友的连接
            String channelContextUserid = filterChannelContext.userid;
            boolean exists = allFriendIds.stream().anyMatch(friendUserId ->
                    friendUserId.equals(channelContextUserid));
            return exists;
        });
    }

    /**
     * 握手完毕
     * */
    public void onAfterHandshaked(HttpRequest httpRequest, HttpResponse httpResponse, ChannelContext channelContext) throws Exception {
        System.out.println("握手完毕");
    }

    /**
     * 字节传输
     * */
    public Object onBytes(WsRequest wsRequest, byte[] bytes, ChannelContext channelContext) throws Exception {
        return Processor.process(channelContext, bytes);
    }

    /**
     * 关闭
     * */
    public Object onClose(WsRequest wsRequest, byte[] bytes, ChannelContext channelContext) throws Exception {
        return null;
    }

    /**
     * 文本传输
     * */
    public Object onText(WsRequest wsRequest, String s, ChannelContext channelContext) throws Exception {
        return null;
    }
}
