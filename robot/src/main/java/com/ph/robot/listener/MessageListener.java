package com.ph.robot.listener;

import com.alibaba.fastjson.JSONObject;
import com.ph.robot.util.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import love.forte.simbot.annotation.OnGroup;
import love.forte.simbot.annotation.OnPrivate;
import love.forte.simbot.api.message.Reply;
import love.forte.simbot.api.message.ReplyAble;
import love.forte.simbot.api.message.events.GroupMsg;
import love.forte.simbot.api.message.events.MessageGet;
import love.forte.simbot.api.message.events.PrivateMsg;
import love.forte.simbot.api.sender.MsgSender;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @author ph
 * @description 监听类
 * @since 2022/11/15 10:15
 */
@Component
@Slf4j
public class MessageListener {
    //青云客url
    static final String URLqyk = "http://api.qingyunke.com/api.php";
    //舔狗日记url
    static final String URLtgrj = "https://v.api.aa1.cn/api/tiangou/index.php";

    /**
     * 监听私聊消息
     */
    @OnPrivate
    public void privateMsg(PrivateMsg privateMsg, MsgSender sender) {
        // 智能聊天
        sendMsg(privateMsg, sender, false);
    }


    /**
     * 监听群消息
     */
    @OnGroup
    public ReplyAble groupMsg(GroupMsg groupMsg, MsgSender sender) {
        // 默认关闭群聊模式，需要的话把注释去掉
        return sendMsg(groupMsg, sender, true);

    }
    /**
     * 通过青客云封装智能聊天
     *
     * @param commonMsg commonMsg
     * @param sender    sender
     */
    private ReplyAble sendMsg(MessageGet commonMsg, MsgSender sender, boolean group) {
        log.info("智能聊天中~~~,接收消息：qq={}, msg={}", commonMsg.getAccountInfo().getAccountCode(),
                commonMsg.getMsgContent().getMsg());
        // MsgSender中存在三大送信器，以及非常多的重载方法。
        // 通过get请求调用聊天接口
        // 判断msg是否为#舔狗日记，否则进入青云客智能回复。
        if(commonMsg.getMsgContent().getMsg().equals("#舔狗日记")){
            String result = HttpUtil.get(URLtgrj).toString();
            JSONObject json = JSONObject.parseObject(result);
            String msg = json.getJSONArray("newslist").getJSONObject(0).getString("content");
            log.info("智能聊天中~~~,发送消息：qq={}, msg={}", commonMsg.getAccountInfo().getAccountCode(), msg);
            //发送群消息
            if (group) {
                // 参数1：回复的消息 参数2：是否at当事人
                return Reply.reply(msg, false);
            }
            //发送私聊消息
            sender.SENDER.sendPrivateMsg(commonMsg, msg);
        }else {
            String result = HttpUtil.get(URLqyk.concat("?key=free&appid=0&msg=").concat(commonMsg.getMsgContent().getMsg().replaceAll(" ", "")));
            if (!StringUtils.isEmpty(result)&&commonMsg.getMsgContent().getMsg().contains("CAT:at,code=2036772081")) {
                JSONObject json = JSONObject.parseObject(result);
                if (json.getInteger("result") == 0 && !StringUtils.isEmpty(json.getString("content"))) {
                    String msg = json.getString("content").replaceAll("<br>", "\n");
                    log.info("智能聊天中~~~,发送消息：qq={}, msg={}", commonMsg.getAccountInfo().getAccountCode(), msg);
                    //发送群消息
                    if (group) {
                        // 参数1：回复的消息 参数2：是否at当事人
                        return Reply.reply(msg, true);
                    }
                    //发送私聊消息
                    sender.SENDER.sendPrivateMsg(commonMsg, msg);
                }
            }

        }
        return null;
    }



}
