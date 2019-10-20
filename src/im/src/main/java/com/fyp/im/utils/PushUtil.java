package com.fyp.im.utils;

import com.fyp.im.TioApplication;
import org.tio.core.ChannelContext;
import org.tio.core.Tio;
import org.tio.server.ServerGroupContext;
import org.tio.utils.lock.SetWithLock;

/**
 * @author fyp
 * @crate 2017/12/6 22:24
 * @project SpringBootLayIM
 * 服务端主动推送
 */
public final class PushUtil {



    /**
     * 获取channelContext
     * */
    private static ChannelContext getChannelContext(String toId) {
        ServerGroupContext context = getServerGroupContext();
        //找到用户
        SetWithLock<ChannelContext> channelContexts = Tio.getChannelContextsByUserid(context, toId);
        if (channelContexts == null) {
            return null;
        }
        if (channelContexts.getObj().size() > 0) {
            return channelContexts.getObj().iterator().next();
        }
        return null;

    }

    private static TioApplication getApplication(){
        return (TioApplication) BeanUtil.getBean("tioApplication");
    }


    private static ServerGroupContext getServerGroupContext(){
        return getApplication().getServerGroupContext();
    }



    /**
     * 判断一个用户是否在线
     * */
    public static boolean isOnline(long userId){
        ChannelContext channelContext = getChannelContext(userId+"");
        return channelContext!=null && !channelContext.isClosed&&!channelContext.isRemoved;
    }

}
